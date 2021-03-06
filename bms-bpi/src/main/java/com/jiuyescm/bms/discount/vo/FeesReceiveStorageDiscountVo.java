/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuyescm.bms.discount.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.jiuyescm.cfm.domain.IEntity;

/**
 * 
 * @author stevenl
 * 
 */
public class FeesReceiveStorageDiscountVo implements IEntity {

    // TODO Change serialVersionUID by eclipse
    private static final long serialVersionUID = -1;
    
	// 自增标识
	private Long id;
	// 任务ID
	private String taskId;
	// 费用科目
	private String subjectCode;
	// 费用编号
	private String feesNo;
	// 运单号
	private String waybillNo;
	// 整单折扣率
	private BigDecimal unitRate;
	// 整单折扣价
	private BigDecimal unitPrice;
	// 首量折扣率
	private BigDecimal firstRate;
	// 首量折扣价
	private BigDecimal firstPrice;
	// 续量折扣率
	private BigDecimal continueRate;
	// 续量折扣价
	private BigDecimal continuePrice;
	// 折扣后金额
	private BigDecimal discountAmount;
	// 减免金额
	private BigDecimal derateAmount;
	// 是否已计算费用 0-未结算 1-已结算 2-结算异常 3-合同不存在 4-不在操作范围 5-其他
	private String isCalculated;
	// 计算时间
	private Timestamp calculateTime;
	// 报价id
	private Long quoteId;
	// 写入时间
	private Timestamp writeTime;
	// 计算说明
	private String remark;

	public FeesReceiveStorageDiscountVo() {

	}
	
	/**
     * 自增标识
     */
	public Long getId() {
		return this.id;
	}

    /**
     * 自增标识
     *
     * @param id
     */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
     * 任务ID
     */
	public String getTaskId() {
		return this.taskId;
	}

    /**
     * 任务ID
     *
     * @param taskId
     */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	/**
     * 费用科目
     */
	public String getSubjectCode() {
		return this.subjectCode;
	}

    /**
     * 费用科目
     *
     * @param subjectCode
     */
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	
	/**
     * 费用编号
     */
	public String getFeesNo() {
		return this.feesNo;
	}

    /**
     * 费用编号
     *
     * @param feesNo
     */
	public void setFeesNo(String feesNo) {
		this.feesNo = feesNo;
	}
	
	/**
     * 运单号
     */
	public String getWaybillNo() {
		return this.waybillNo;
	}

    /**
     * 运单号
     *
     * @param waybillNo
     */
	public void setWaybillNo(String waybillNo) {
		this.waybillNo = waybillNo;
	}
	
	/**
     * 整单折扣率
     */
	public BigDecimal getUnitRate() {
		return this.unitRate;
	}

    /**
     * 整单折扣率
     *
     * @param unitRate
     */
	public void setUnitRate(BigDecimal unitRate) {
		this.unitRate = unitRate;
	}
	
	/**
     * 整单折扣价
     */
	public BigDecimal getUnitPrice() {
		return this.unitPrice;
	}

    /**
     * 整单折扣价
     *
     * @param unitPrice
     */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	/**
     * 首量折扣率
     */
	public BigDecimal getFirstRate() {
		return this.firstRate;
	}

    /**
     * 首量折扣率
     *
     * @param firstRate
     */
	public void setFirstRate(BigDecimal firstRate) {
		this.firstRate = firstRate;
	}
	
	/**
     * 首量折扣价
     */
	public BigDecimal getFirstPrice() {
		return this.firstPrice;
	}

    /**
     * 首量折扣价
     *
     * @param firstPrice
     */
	public void setFirstPrice(BigDecimal firstPrice) {
		this.firstPrice = firstPrice;
	}
	
	/**
     * 续量折扣率
     */
	public BigDecimal getContinueRate() {
		return this.continueRate;
	}

    /**
     * 续量折扣率
     *
     * @param continueRate
     */
	public void setContinueRate(BigDecimal continueRate) {
		this.continueRate = continueRate;
	}
	
	/**
     * 续量折扣价
     */
	public BigDecimal getContinuePrice() {
		return this.continuePrice;
	}

    /**
     * 续量折扣价
     *
     * @param continuePrice
     */
	public void setContinuePrice(BigDecimal continuePrice) {
		this.continuePrice = continuePrice;
	}
	
	/**
     * 折扣后金额
     */
	public BigDecimal getDiscountAmount() {
		return this.discountAmount;
	}

    /**
     * 折扣后金额
     *
     * @param discountAmount
     */
	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
	
	/**
     * 减免金额
     */
	public BigDecimal getDerateAmount() {
		return this.derateAmount;
	}

    /**
     * 减免金额
     *
     * @param derateAmount
     */
	public void setDerateAmount(BigDecimal derateAmount) {
		this.derateAmount = derateAmount;
	}
	
	/**
     * 是否已计算费用 0-未结算 1-已结算 2-结算异常 3-合同不存在 4-不在操作范围 5-其他
     */
	public String getIsCalculated() {
		return this.isCalculated;
	}

    /**
     * 是否已计算费用 0-未结算 1-已结算 2-结算异常 3-合同不存在 4-不在操作范围 5-其他
     *
     * @param isCalculated
     */
	public void setIsCalculated(String isCalculated) {
		this.isCalculated = isCalculated;
	}
	
	/**
     * 计算时间
     */
	public Timestamp getCalculateTime() {
		return this.calculateTime;
	}

    /**
     * 计算时间
     *
     * @param calculateTime
     */
	public void setCalculateTime(Timestamp calculateTime) {
		this.calculateTime = calculateTime;
	}
	
	/**
     * 报价id
     */
	public Long getQuoteId() {
		return this.quoteId;
	}

    /**
     * 报价id
     *
     * @param quoteId
     */
	public void setQuoteId(Long quoteId) {
		this.quoteId = quoteId;
	}
	
	/**
     * 写入时间
     */
	public Timestamp getWriteTime() {
		return this.writeTime;
	}

    /**
     * 写入时间
     *
     * @param writeTime
     */
	public void setWriteTime(Timestamp writeTime) {
		this.writeTime = writeTime;
	}
	
	/**
     * 计算说明
     */
	public String getRemark() {
		return this.remark;
	}

    /**
     * 计算说明
     *
     * @param remark
     */
	public void setRemark(String remark) {
		this.remark = remark;
	}
    
}
