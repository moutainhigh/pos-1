/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.transaction.service;

import com.pos.common.util.mvc.support.ApiResult;
import com.pos.common.util.mvc.support.LimitHelper;
import com.pos.common.util.mvc.support.OrderHelper;
import com.pos.common.util.mvc.support.Pagination;
import com.pos.transaction.condition.query.PosTransactionCondition;
import com.pos.transaction.dto.failure.TransactionFailureRecordDto;
import com.pos.transaction.dto.transaction.TransactionRecordDto;

import java.util.List;

/**
 * Pos交易Service
 *
 * @author wangbing
 * @version 1.0, 2017/8/26
 */
public interface PosUserTransactionRecordService {

    /**
     * 查询用户的交易数量
     *
     * @param userId 用户userId
     * @return 交易数量
     */
    Integer queryUserTransactionCount(Long userId);

    /**
     * 查询用户的交易记录
     *
     * @param condition   查询条件，可以为空
     * @param orderHelper 排序参数
     * @param limitHelper 分页参数
     * @return 交易记录
     */
    ApiResult<Pagination<TransactionRecordDto>> queryUserTransactionRecord(
            PosTransactionCondition condition, OrderHelper orderHelper, LimitHelper limitHelper);

    /**
     * 交易的失败记录列表
     *
     * @param transactionId 交易id
     * @return 失败记录列表
     */
    ApiResult<List<TransactionFailureRecordDto>> queryFailureRecords(Long transactionId);
}
