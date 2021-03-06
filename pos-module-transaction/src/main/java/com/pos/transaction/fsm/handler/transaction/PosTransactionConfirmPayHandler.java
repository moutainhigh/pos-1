/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.transaction.fsm.handler.transaction;

import com.pos.basic.sm.action.FSMAction;
import com.pos.transaction.constants.TransactionStatusType;
import com.pos.transaction.fsm.context.TransactionStatusTransferContext;
import com.pos.transaction.service.PosService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * PosTransactionConfirmPayHandler
 *
 * @author wangbing
 * @version 1.0, 2017/10/19
 */
@Component
public class PosTransactionConfirmPayHandler extends FSMAction {

    @Resource
    private PosService posService;

    @Override
    public boolean action(String fromState, String toState, Object context) {
        TransactionStatusTransferContext statusTransfer = (TransactionStatusTransferContext) context;
        TransactionStatusType targetStatus = TransactionStatusType.getEnum(toState);

        return posService.updateTransactionStatus(statusTransfer, targetStatus);
    }
}
