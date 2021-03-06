/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.user.service.impl;

import com.pos.common.sms.constant.MemcachedPrefixType;
import com.pos.common.sms.service.SmsService;
import com.pos.common.util.basic.AddressUtils;
import com.pos.common.util.basic.JsonUtils;
import com.pos.common.util.exception.CommonErrorCode;
import com.pos.common.util.exception.ErrorCode;
import com.pos.common.util.exception.ValidationException;
import com.pos.common.util.mvc.support.ApiResult;
import com.pos.common.util.security.MD5Utils;
import com.pos.common.util.validation.FieldChecker;
import com.pos.common.util.validation.Validator;
import com.pos.user.constant.CustomerType;
import com.pos.user.constant.UserType;
import com.pos.user.dao.CustomerDao;
import com.pos.user.dao.ManagerDao;
import com.pos.user.dao.UserClassDao;
import com.pos.user.dao.UserDao;
import com.pos.user.domain.Customer;
import com.pos.user.domain.Manager;
import com.pos.user.domain.User;
import com.pos.user.domain.UserClass;
import com.pos.user.dto.IdentityInfoDto;
import com.pos.user.dto.LoginInfoDto;
import com.pos.user.dto.UserRegConfirmDto;
import com.pos.user.dto.converter.UserDtoConverter;
import com.pos.user.dto.customer.CustomerDto;
import com.pos.user.dto.manager.ManagerDto;
import com.pos.user.exception.UserErrorCode;
import com.pos.user.service.RegisterService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户注册服务实现类
 *
 * @author cc
 * @version 1.0, 2016/6/8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegisterServiceImpl implements RegisterService {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Resource
    private UserDao userDao;

    @Resource
    private UserClassDao userClassDao;

    @Resource
    private CustomerDao customerDao;

    @Resource
    private ManagerDao managerDao;

    @Resource
    private SmsService smsService;

    @Value("${business.register.template}")
    private String registerTemplate;

    @Value("${business.register.exist.template}")
    private String registerExistTemplate;

    @Value("${random.password.size}")
    private String randPasswordSize;

    @Value("${customer.add.confirm.template}")
    private String customerConfirmTemplate;

    @Value("${employee.add.notify.template}")
    private String employeeNotifyTemplate;

    @Value("${employee.add.exist.notify.template}")
    private String employeeExistNotifyTemplate;

    @Value("${manager.add.notify.template}")
    private String managerNotifyTemplate;

    @Value("${manager.add.exist.notify.template}")
    private String managerExistNotifyTemplate;

    /**
     * 缓存中验证码过期时间
     */
    @Value("${cache.expire.sms.verify.code}")
    String cacheExpireSeconds;

    @Override
    public ApiResult<CustomerDto> addCustomer(LoginInfoDto loginInfoDto, boolean setLoginInfo, CustomerType customerType) {
        IdentityInfoDto identity = loginInfoDto.getIdentityInfoDto();
        ErrorCode err = checkAddCustomer(identity.getLoginName(), identity.getPassword(), identity.getSmsCode());
        if (err != null) {
            return ApiResult.fail(err);
        }

        User existingUser = userDao.getByUserPhone(identity.getLoginName());
        if (existingUser == null) {
            // 用户不存在的情况
            User user = addUser(identity.getLoginName(), identity.getLoginName(),
                    identity.getPassword(), null);
            UserClass userClass = saveUserClass(user, UserType.CUSTOMER, user.getId(), true, setLoginInfo, loginInfoDto);
            Customer customer = saveCustomer(user, customerType);
            CustomerDto customerDto = UserDtoConverter.convert2CustomerDto(user, userClass, customer);

            return ApiResult.succ(customerDto);
        } else {
            // 用户已存在的情况
            // 检查账户是否被删除
            if (existingUser.isDeleted()) {
                return ApiResult.fail(UserErrorCode.ACCOUNT_DELETED);
            }
            return ApiResult.fail(UserErrorCode.USER_EXISTED);
        }
    }

    @Override
    public ApiResult addManager(ManagerDto managerDto, Long createUserId) {
        checkAddManager(managerDto, createUserId);

        String notifyMessage;
        User user = getUserByPhoneOrUserName(managerDto.getUserPhone(), managerDto.getUserName());
        if (user == null) {
            // 用户不存在, 使用随机密码创建User
            String randPassword = RandomStringUtils.randomNumeric(Integer.valueOf(randPasswordSize));
            user = addUser(managerDto.getUserName(),
                    managerDto.getUserPhone(), randPassword, managerDto.getName());
            notifyMessage = String.format(managerNotifyTemplate, user.getUserName(), randPassword);
        } else {
            // 用户已存在, 如果User被删除, 或者已开通C端以外的账号, 则不能注册M端账号
            if (user.isDeleted()) {
                return ApiResult.fail(UserErrorCode.ACCOUNT_DELETED);
            }
            List<UserClass> userClasses = userClassDao.findClasses(user.getId());
            if (userClasses != null && !userClasses.isEmpty()) {
                for (UserClass uc : userClasses) {
                    if (!UserType.CUSTOMER.compare(uc.getUserType())) {
                        return ApiResult.fail(UserErrorCode.USER_EXISTED);
                    }
                }
            }
            user.setName(managerDto.getName());
            user.setUserName(managerDto.getUserName());
            userDao.update(user);
            notifyMessage = String.format(managerExistNotifyTemplate, user.getUserName());
        }

        // 为用户创建M端分类
        UserClass userClass = saveUserClass(user, UserType.MANAGER, createUserId, true, false, null);
        // 为用户创建M端账号
        managerDto.setId(user.getId());
        Manager manager = saveManager(managerDto);

        // 发送创建成功的短信
        sendSmsNotify(UserType.MANAGER, user.getUserPhone(), notifyMessage);

        return ApiResult.succ();
    }

    private User getUserByPhoneOrUserName(String userPhone, String userName) {
        User user = userDao.getByUserPhone(userPhone);
        if (user == null) {
            user = userDao.getByUserName(userName);
        }
        return user;
    }

    private boolean checkMerchantIntegrity(User user) {
        return user.getUserPhone() != null && user.getName() != null
                && user.getUserName() != null && user.getIdImageA() != null
                && user.getIdHoldImage() != null && user.getIdCard() != null;
    }

    private ErrorCode checkAddCustomer(String userPhone, String password, String verifyCode) {
        Validator.checkMobileNumber(userPhone);
        Validator.checkPassword(password);

        if (userPhone.equals(password)) {
            return UserErrorCode.USER_PWD_DUPLICATE;
        }
        if (!smsService.checkVerifyCode(
                userPhone, verifyCode, MemcachedPrefixType.REGISTER).isSucc()) {
            return CommonErrorCode.VERIFY_CODE_ERROR;
        }

        return null;
    }

    private void checkAddManager(ManagerDto managerDto, Long createUserId) {
        FieldChecker.checkEmpty(createUserId, "createUserId");
        Validator.checkUserName(managerDto.getUserName());
        Validator.checkMobileNumber(managerDto.getUserPhone());
        Validator.checkCnName(managerDto.getName());

        if (managerDto.parseUserDetailType() == null) {
            throw new ValidationException("'managerDto.userDetailType'无效值");
        }
    }

    private User addUser(String userName, String userPhone, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setUserName(userName);
        user.setUserPhone(userPhone);
        user.setPassword(MD5Utils.getMD5Code(password));
        userDao.save(user);
        return user;
    }

    /**
     * 新增用户类型信息，并记录相关注册和登录信息
     *
     * @param user 用户信息
     * @param userType 用户类别
     * @param createUserId 创建者userId
     * @param isAvailable 是否可用
     * @param setLoginInfo 是否记录登录信息
     * @param infoDto 注册或登录信息
     * @return 用户类型信息
     */
    private UserClass saveUserClass(User user, UserType userType, Long createUserId, boolean isAvailable,
                                    boolean setLoginInfo, LoginInfoDto infoDto) {
        Date now = Calendar.getInstance().getTime();
        UserClass userClass = new UserClass();
        userClass.setUserId(user.getId());
        userClass.setUserType(userType.getValue());
        userClass.setCreateUserId(createUserId);
        userClass.setAvailable(isAvailable);
        userClass.setCreateTime(now);
        userClass.setUpdateTime(now);
        // 设置用户注册拓展信息
        if (infoDto != null) {
            userClass.setRegisterIp(infoDto.getIp());
            userClass.setRegisterAddress(AddressUtils.getAddresses(infoDto.getIp()));
            if (infoDto.getUserExtensionInfo() != null) {
                userClass.setRegisterInfo(JsonUtils.objectToJson(infoDto.getUserExtensionInfo()));
            }
        }
        // 设置用户最近一次登录信息
        if (setLoginInfo) {
            userClass.setLastLoginTime(now);
            if (infoDto != null) {
                userClass.setLastLoginIp(infoDto.getIp());
                userClass.setLoginAddress(AddressUtils.getAddresses(infoDto.getIp()));
                if (infoDto.getUserExtensionInfo() != null) {
                    userClass.setLastLoginInfo(JsonUtils.objectToJson(infoDto.getUserExtensionInfo()));
                }
            }
        }
        userClassDao.save(userClass);
        return userClass;
    }

    private Customer saveCustomer(User user, CustomerType customerType) {
        Customer customer = new Customer();
        customer.setUserId(user.getId());
        customer.setCustomerType(customerType.getCode());
        customer.setNickName("业主" + user.getUserPhone().substring(
                user.getUserPhone().length() - 4, user.getUserPhone().length())); // 昵称默认为: 业主 + 手机号后4位
        customerDao.save(customer);
        return customer;
    }

    private Manager saveManager(ManagerDto managerDto) {
        Manager manager = new Manager();
        manager.setUserId(managerDto.getId());
        manager.setUserDetailType(managerDto.getUserDetailType());
        manager.setHeadImage(managerDto.getHeadImage());
        manager.setQuitJobs(managerDto.isQuitJobs());
        managerDao.save(manager);
        return manager;
    }

    private void sendSmsNotify(UserType userType, String userPhone, String message) {
        ApiResult sendResult = smsService.sendMessage(userPhone, message);
        if (!sendResult.isSucc()) {
            throw new RuntimeException("新增用户账号(" + userType.getValue() + ")，发送短信通知失败");
        }
    }

}