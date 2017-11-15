/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.user.service_v.impl;

import com.pos.basic.mq.MQMessage;
import com.pos.basic.mq.MQReceiverType;
import com.pos.basic.mq.MQTemplate;
import com.pos.common.sms.constant.MemcachedPrefixType;
import com.pos.common.sms.service.SmsService;
import com.pos.common.util.basic.IpAddressUtils;
import com.pos.common.util.basic.JsonUtils;
import com.pos.common.util.basic.SimpleRegexUtils;
import com.pos.common.util.exception.CommonErrorCode;
import com.pos.common.util.exception.ErrorCode;
import com.pos.common.util.mvc.support.ApiResult;
import com.pos.common.util.security.MD5Utils;
import com.pos.common.util.validation.Validator;
import com.pos.user.constant.CustomerSourceType;
import com.pos.user.constant.UserGender;
import com.pos.user.constant.UserType;
import com.pos.user.dao.v1_0_0.CustomerDao;
import com.pos.user.dao.v1_0_0.UserDao;
import com.pos.user.domain.v1_0_0.Customer;
import com.pos.user.domain.v1_0_0.User;
import com.pos.user.domain.v1_0_0.UserExtension;
import com.pos.user.dto.IdentityInfoDto;
import com.pos.user.dto.UserExtensionInfoDto;
import com.pos.user.dto.mq.CustomerInfoMsg;
import com.pos.user.dto.v1_0_0.CustomerDto;
import com.pos.user.dto.v1_0_0.RegisterInfoDto;
import com.pos.user.dto.v1_0_0.RegisterRecommendDto;
import com.pos.user.exception.UserErrorCode;
import com.pos.user.service_v.RegisterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * 用户注册服务接口类实现
 *
 * @author wangbing
 * @version 1.0, 2017/11/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegisterServiceImpl implements RegisterService {

    private final static Logger LOG = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Resource
    private MQTemplate mqTemplate;

    @Resource
    private SmsService smsService;

    @Resource
    private UserDao userDao;

    @Resource
    private CustomerDao customerDao;

    @Override
    public ApiResult<CustomerDto> addCustomer(RegisterInfoDto registerInfo, boolean setLoginInfo) {
        IdentityInfoDto identity = registerInfo.getIdentityInfoDto();
        ErrorCode err = checkAddCustomer(identity.getLoginName(), identity.getPassword(), identity.getSmsCode());
        if (err != null) {
            return ApiResult.fail(err);
        }

        // 用户不存在的情况
        User existingUser = userDao.getByUserPhone(identity.getLoginName());
        if (existingUser == null) {
            User user = addUser(identity.getLoginName(), identity.getPassword(), UserType.CUSTOMER);
            Customer customer = saveCustomerBase(user);
            UserExtension extension = saveExtension(user, setLoginInfo, registerInfo.getUserExtensionInfo());
            CustomerDto customerDto = buildCustomerDto(user, customer, extension);

            // 发送注册推荐人消息
            sendCustomerRegisterMessage(user.getId(), user.getPhone(), registerInfo.getRegisterRecommend());

            return ApiResult.succ(customerDto);
        } else {
            if (!existingUser.isEnable()) {
                return ApiResult.fail(UserErrorCode.ACCOUNT_DELETED);
            }
            return ApiResult.fail(UserErrorCode.USER_EXISTED);
        }
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

    private User addUser(String loginName, String password, UserType type) {
        User user = new User();

        user.setLoginName(loginName);
        user.setPassword(MD5Utils.getMD5Code(password));
        user.setUserType(type.getValue());
        user.setEnableStatus(Boolean.TRUE);
        user.setPhone(loginName);
        userDao.save(user);

        return user;
    }

    private Customer saveCustomerBase(User user) {

        Customer customer = new Customer();
        customer.setUserId(user.getId());
        customer.setNickName("用户-" + SimpleRegexUtils.hiddenMobile(user.getLoginName()));
        customer.setSourceType(CustomerSourceType.SELF.getCode());
        customer.setGender(Integer.valueOf(UserGender.SECRECY.getCode()));
        customerDao.saveCustomerBase(customer);

        return customer;
    }

    /**
     * 保存用户拓展信息
     *
     * @param user          用户信息
     * @param setLoginInfo  是否记录登录信息
     * @param extensionInfo 拓展信息
     * @return 用户类型信息
     */
    private UserExtension saveExtension(User user, boolean setLoginInfo, UserExtensionInfoDto extensionInfo) {
        Date now = Calendar.getInstance().getTime();

        UserExtension extension = new UserExtension();
        extension.setUserId(user.getId());
        // 查询IP所在地
        if (StringUtils.isNotEmpty(extensionInfo.getIp())) {
            extensionInfo.setIpAddress(IpAddressUtils.getAddresses(extensionInfo.getIp()));
        }
        // 设置拓展信息
        String extensionInfoJson = JsonUtils.objectToJson(extensionInfo);
        extension.setRegisterTime(now);
        extension.setRegisterInfo(extensionInfoJson);
        if (setLoginInfo) {
            extension.setLastLoginTime(now);
            extension.setLastLoginInfo(extensionInfoJson);
        }
        userDao.saveExtension(extension);

        return extension;
    }

    private CustomerDto buildCustomerDto(User user, Customer customer, UserExtension extension) {
        CustomerDto customerDto = new CustomerDto();

        customerDto.setId(user.getId());
        customerDto.setLoginName(user.getLoginName());
        customerDto.setEnableStatus(user.getEnableStatus());
        customerDto.setUserType(user.getUserType());

        customerDto.setRegisterTime(extension.getRegisterTime());
        customerDto.setLastLoginTime(extension.getLastLoginTime());

        customerDto.setMail(customer.getMail());
        customerDto.setNickName(customer.getNickName());
        customerDto.setHeadImage(customer.getHeadImage());
        customerDto.setGender(customer.getGender());
        customerDto.setAge(customer.getAge());
        customerDto.setSourceType(customer.getSourceType());
        customerDto.setUpdateTime(customer.getUpdateTime());

        return customerDto;
    }

    private void sendCustomerRegisterMessage(Long userId, String userPhone, RegisterRecommendDto registerRecommend) {
        CustomerInfoMsg msg = new CustomerInfoMsg(userId, userPhone, registerRecommend);
        mqTemplate.sendDirectMessage(new MQMessage(MQReceiverType.CUSTOMER, "pos.register.route.key", msg));
        LOG.info("发送一条用户注册的消息");
    }
}
