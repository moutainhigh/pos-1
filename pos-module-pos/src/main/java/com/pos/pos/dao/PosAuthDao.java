/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.pos.dao;

import com.pos.common.util.mvc.support.LimitHelper;
import com.pos.common.util.mvc.support.OrderHelper;
import com.pos.pos.condition.query.PosUserCondition;
import com.pos.pos.domain.UserPosAuth;
import com.pos.pos.dto.auth.BaseAuthDto;
import com.pos.pos.dto.auth.PosUserAuthDetailDto;
import com.pos.pos.dto.auth.PosUserAuthDto;
import com.pos.pos.dto.user.PosUserIntegrateDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 快捷收款用户Dao
 *
 * @author wangbing
 * @version 1.0, 2017/8/25
 */
@Repository
public interface PosAuthDao {

    /**
     * 新增快捷收款用户
     *
     * @param userPosAuth 快捷收款用户信息
     */
    void addPosAuth(@Param("posAuth") UserPosAuth userPosAuth);

    /**
     * 获取快捷收款用户详细信息
     *
     * @param userId 用户userId
     * @return 快捷收款用户详细信息
     */
    PosUserAuthDetailDto findAuthDetail(@Param("userId") Long userId);

    /**
     * 通过主键id查询快捷收款用户详细信息
     *
     * @param posAuthId 主键id
     * @return 快捷收款用户详细信息
     */
    PosUserAuthDetailDto findAuthDetailById(@Param("posAuthId") Long posAuthId);

    /**
     * 获取快捷收款用户信息
     *
     * @param userId 用户userId
     * @return 快捷收款用户信息
     */
    PosUserAuthDto findAuth(@Param("userId") Long userId);

    /**
     * 获取指定用户的权限信息
     *
     * @param posId 主键id
     * @return 权限信息
     */
    BaseAuthDto findBaseAuthById(@Param("posId") Long posId);

    /**
     * 更新用户身份认证信息
     *
     * @param identity 身份认证信息
     */
    void updateIdentityInfo(@Param("identity") PosUserAuthDto identity);

    /**
     * 更新用户绑定的收款银行卡信息
     *
     * @param posCardInfo 绑卡信息
     */
    void updatePosCardInfo(@Param("posCardInfo") PosUserAuthDto posCardInfo);

    /**
     * 更新用户身份认证信息审核状态
     *
     * @param posAuthId    user_pos_auth主键id
     * @param status       状态code值
     * @param rejectReason 未通过审核时传入拒绝原因
     * @param updateUserId 更新操作人userId
     */
    void updateAuditStatus(
            @Param("posAuthId") Long posAuthId,
            @Param("status") Integer status,
            @Param("rejectReason") String rejectReason,
            @Param("updateUserId") Long updateUserId);

    /**
     * 查询符合条件的快捷收款用户数量
     *
     * @param condition 查询条件
     * @return 符合条件的快捷收款用户数量
     */
    int getPosUserCount(@Param("condition") PosUserCondition condition);

    /**
     * 查询符合条件快捷收款用户列表
     *
     * @param condition   查询条件
     * @param orderHelper 排序参数
     * @param limitHelper 分页参数
     * @return 查询结果
     */
    List<PosUserIntegrateDto> queryPosUsers(
            @Param("condition") PosUserCondition condition,
            @Param("orderHelper") OrderHelper orderHelper,
            @Param("limitHelper") LimitHelper limitHelper);

    /**
     * 更新快捷收款用户的权限信息
     *
     * @param posId 被更新用户posId
     * @param baseAuth 新权限信息
     */
    void updateAuth(
            @Param("posId") Long posId,
            @Param("baseAuth") BaseAuthDto baseAuth,
            @Param("updateUserId") Long updateUserId);
}