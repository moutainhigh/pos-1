/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.transaction.exception;

import com.pos.common.util.exception.ErrorCode;

/**
 * POS交易服务相关的错误码定义（code：1100 - 1199）
 *
 * @author wangbing
 * @version 1.0, 2017/12/5
 */
public enum TransactionErrorCode implements ErrorCode {

    POS_ERROR_AUTHORITY_AUDIT_STATUS_NOT_SUBMIT(1100, "身份认证未提交，不能进行收款操作！"),

    POS_ERROR_AUTHORITY_AUDIT_STATUS_REJECTED(1101, "身份认证未通过，不能进行收款操作！"),

    POS_ERROR_OUT_BANK_CARD_NOT_EXISTED(1102, "支付银行卡不存在，不能进行收款操作！"),

    POS_ERROR_CARD_USER_DIFFERENCE(1103, "付款银行卡必须跟收款银行卡是同一个持卡人"),

    POS_ERROR_TRANSACTION_NOT_EXISTED(1104, "交易记录不存在"),

    POS_ERROR_STATUS_FOR_SMS_CODE(1105, "交易已处理，请勿重新获取短信验证码"),

    BIND_CARD_ERROR_AUTHORITY_AUDIT_STATUS_AUDITED(1110, "身份认证已通过，不能重复绑定收款银行卡！"),

    BIND_CARD_ERROR_AUTHORITY_AUDIT_STATUS_NOT_AUDIT(1111, "身份认证审核中，不能重复绑定收款银行卡！"),

    BIND_CARD_ERROR_IDENTITY_NOT_EXISTED(1112, "身份认证信息不存在，无法绑定收款银行卡！"),

    ALTER_BIND_CARD_ERROR_AUTHORITY_AUDIT_STATUS_AUDITED(1112, "身份认证信息通过审核后，才能更换收款银行卡！");


    private final int code;

    private final String message;

    TransactionErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}