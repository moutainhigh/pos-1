/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.user.dao.v1_0_0;

import com.pos.user.domain.v1_0_0.Customer;
import com.pos.user.dto.v1_0_0.CustomerDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * C端用户相关DAO
 *
 * @author wangbing
 * @version 1.0, 2017/11/13
 */
@Repository
public interface CustomerDao {

    /**
     * 保存客户基本信息
     *
     * @param customer 客户基本信息
     */
    void saveCustomerBase(@Param("customer") Customer customer);

    /**
     * 根据UserId获取用户信息
     *
     * @param userId 用户UserId
     * @return 用户信息
     */
    CustomerDto getByUserId(@Param("userId") Long userId);

    /**
     * 根据UserId和启禁用状态获取用户信息
     *
     * @param userId 用户id
     * @param enable true：只返回启用，false：只返回禁用，null：不限
     * @return 用户信息
     */
    CustomerDto findByUserIdAndEnable(
            @Param("userId") Long userId,
            @Param("enable") Boolean enable);

    List<CustomerDto> findCustomersInUserIds(
            @Param("userIds") List<Long> userIds,
            @Param("deleted") Boolean deleted, @Param("available") Boolean available);
}
