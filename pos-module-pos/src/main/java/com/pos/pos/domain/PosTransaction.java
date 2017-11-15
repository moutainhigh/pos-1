/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.pos.domain;

import com.pos.pos.dto.PosOutCardInfoDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户pos的交易记录
 * @author 睿智
 * @version 1.0, 2017/8/22
 */
public class PosTransaction implements Serializable {

    private static final long serialVersionUID = 195628922523210462L;

    private Long id;

    private String recordNum;//流水号

    private Long inCardId;//转入的卡ID

    @Deprecated
    private long outCardId;//转出的卡ID

    /** @see PosOutCardInfoDto */
    private String outCardInfo; // 转出卡信息 JSON

    private Long userId;//提现的人的userId

    @Deprecated
    private long agentId;//提取佣金的人的userId

    private BigDecimal amount;//提现的金额

    private BigDecimal serviceCharge;//手续费，用户提现时所有需要扣除的金额

    private BigDecimal arrivalAmount;//实际到账金额

    private BigDecimal payCharge;//支付手续费（用户发起支付，支付公司到平台扣除的）

    private BigDecimal posCharge;//提现手续费（平台支付给用户时，支付公司扣除的）

    @Deprecated
    private BigDecimal agentCharge;//用户提现时，平台支付给用户上级的佣金

    private Long companyId;//公司ID

    private Byte costType;//支付的费用类型

    private Integer status;//状态

    private Date createDate;//创建时间

    private Date payDate;//用户支付的时间

    private Date completeDate;//公司支付给用户的时间

    private String helibaoZhifuNum;//合利宝支付的流水号

    private String helibaoTixianNum;//合利宝提现的流水号

    @Deprecated
    private byte getAgent;//渠道商提取佣金的状态 0=未提取 1=已申请 2=已提取

    @Deprecated
    public long getAgentId() {
        return agentId;
    }

    @Deprecated
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    @Deprecated
    public byte getGetAgent() {
        return getAgent;
    }

    @Deprecated
    public void setGetAgent(byte getAgent) {
        this.getAgent = getAgent;
    }

    @Deprecated
    public BigDecimal getAgentCharge() {
        return agentCharge;
    }

    @Deprecated
    public void setAgentCharge(BigDecimal agentCharge) {
        this.agentCharge = agentCharge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public Long getInCardId() {
        return inCardId;
    }

    public void setInCardId(Long inCardId) {
        this.inCardId = inCardId;
    }

    public long getOutCardId() {
        return outCardId;
    }

    public void setOutCardId(long outCardId) {
        this.outCardId = outCardId;
    }

    public String getOutCardInfo() {
        return outCardInfo;
    }

    public void setOutCardInfo(String outCardInfo) {
        this.outCardInfo = outCardInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getArrivalAmount() {
        return arrivalAmount;
    }

    public void setArrivalAmount(BigDecimal arrivalAmount) {
        this.arrivalAmount = arrivalAmount;
    }

    public BigDecimal getPayCharge() {
        return payCharge;
    }

    public void setPayCharge(BigDecimal payCharge) {
        this.payCharge = payCharge;
    }

    public BigDecimal getPosCharge() {
        return posCharge;
    }

    public void setPosCharge(BigDecimal posCharge) {
        this.posCharge = posCharge;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Byte getCostType() {
        return costType;
    }

    public void setCostType(Byte costType) {
        this.costType = costType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public String getHelibaoZhifuNum() {
        return helibaoZhifuNum;
    }

    public void setHelibaoZhifuNum(String helibaoZhifuNum) {
        this.helibaoZhifuNum = helibaoZhifuNum;
    }

    public String getHelibaoTixianNum() {
        return helibaoTixianNum;
    }

    public void setHelibaoTixianNum(String helibaoTixianNum) {
        this.helibaoTixianNum = helibaoTixianNum;
    }
}
