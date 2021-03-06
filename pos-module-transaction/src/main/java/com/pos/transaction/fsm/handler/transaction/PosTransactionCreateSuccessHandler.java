/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.transaction.fsm.handler.transaction;

import com.pos.basic.sm.action.FSMAction;
import com.pos.transaction.constants.TransactionStatusType;
import com.pos.transaction.fsm.context.TransactionStatusTransferContext;
import com.pos.transaction.service.PosService;

import javax.annotation.Resource;

/**
 * PosTransactionCreateSuccessHandler
 *
 * @author wangbing
 * @version 1.0, 2017/12/6
 */
public class PosTransactionCreateSuccessHandler  extends FSMAction {

    @Resource
    private PosService posService;

    @Override
    public boolean action(String fromState, String toState, Object context) {
        TransactionStatusTransferContext statusTransfer = (TransactionStatusTransferContext) context;
        TransactionStatusType targetStatus = TransactionStatusType.getEnum(toState);

        return posService.updateTransactionStatus(statusTransfer, targetStatus);
    }
}
