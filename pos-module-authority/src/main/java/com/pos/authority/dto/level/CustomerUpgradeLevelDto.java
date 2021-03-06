/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.authority.dto.level;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户等级晋升Dto
 *
 * @author wangbing
 * @version 1.0, 2017/12/5
 */
public class CustomerUpgradeLevelDto implements Serializable {

    private static final long serialVersionUID = -8486881837301642126L;
    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户当前等级")
    private Integer currentLevel;

    @ApiModelProperty("用户当前提现费率")
    private BigDecimal withdrawRate;

    @ApiModelProperty("用户当前提现额外手续费")
    private BigDecimal extraServiceCharge;

    @ApiModelProperty("用户直接下级数量")
    private Integer childrenCount;

    @ApiModelProperty("用户晋升等级已支付的费用")
    private BigDecimal paidCharge;

    @ApiModelProperty("用户的收款总金额")
    private BigDecimal totalWithdrawAmount;

    @ApiModelProperty("目标等级")
    private Integer targetLevel;

    @ApiModelProperty("达到目标等级还需支付的金额")
    private BigDecimal levelPriceDifference;

    public CustomerUpgradeLevelDto() {
    }

    public CustomerUpgradeLevelDto(Long userId, Integer targetLevel) {
        this.userId = userId;
        this.currentLevel = 0;
        this.withdrawRate = BigDecimal.ZERO;
        this.extraServiceCharge = BigDecimal.ZERO;
        this.childrenCount = 0;
        this.paidCharge = BigDecimal.ZERO;
        this.totalWithdrawAmount = BigDecimal.ZERO;
        this.targetLevel = targetLevel;
        this.levelPriceDifference = BigDecimal.ZERO;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public BigDecimal getWithdrawRate() {
        return withdrawRate;
    }

    public void setWithdrawRate(BigDecimal withdrawRate) {
        this.withdrawRate = withdrawRate;
    }

    public BigDecimal getExtraServiceCharge() {
        return extraServiceCharge;
    }

    public void setExtraServiceCharge(BigDecimal extraServiceCharge) {
        this.extraServiceCharge = extraServiceCharge;
    }

    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }

    public BigDecimal getPaidCharge() {
        return paidCharge;
    }

    public void setPaidCharge(BigDecimal paidCharge) {
        this.paidCharge = paidCharge;
    }

    public BigDecimal getTotalWithdrawAmount() {
        return totalWithdrawAmount;
    }

    public void setTotalWithdrawAmount(BigDecimal totalWithdrawAmount) {
        this.totalWithdrawAmount = totalWithdrawAmount;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public BigDecimal getLevelPriceDifference() {
        return levelPriceDifference;
    }

    public void setLevelPriceDifference(BigDecimal levelPriceDifference) {
        this.levelPriceDifference = levelPriceDifference;
    }
}
