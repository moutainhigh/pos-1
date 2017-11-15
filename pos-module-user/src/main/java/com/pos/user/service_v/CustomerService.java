/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.user.service_v;

import com.pos.user.dto.v1_0_0.CustomerDto;

import java.util.List;
import java.util.Map;

/**
 * 客户相关Service
 *
 * @author wangbing
 * @version 1.0, 2017/11/14
 */
public interface CustomerService {

    /**
     * 根据用户ID查询客户信息.
     *
     * @param userId  用户ID
     * @param disable 是否返回被禁用的账号(true：需要返回被禁用的账号，false：不限)
     */
    CustomerDto findById(Long userId, boolean disable);

    /**
     * 查询客户信息 TODO
     *
     * @param userIds
     * @return
     */
    Map<Long, CustomerDto> getCustomerDtoMapById(List<Long> userIds);

    /**
     * 查询指定的一组客户信息. TODO
     *
     * @param userIds 一组用户ID
     * @param disable 是否返回被禁用的账号
     * @param deleted 是否返回被删除的账号
     */
    List<CustomerDto> findInUserIds(List<Long> userIds, boolean disable, boolean deleted);
}
