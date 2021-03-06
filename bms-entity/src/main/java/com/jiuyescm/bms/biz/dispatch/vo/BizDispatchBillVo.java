package com.jiuyescm.bms.biz.dispatch.vo;

import java.sql.Timestamp;

import com.jiuyescm.cfm.domain.IEntity;

/**
 * 
 * @author stevenl
 * 
 */
public class BizDispatchBillVo implements IEntity {

	private static final long serialVersionUID = 9003402865262204163L;
	
	// id
	private Long id;
	// 出库单号
	private String outstockNo;
	// 外部单号
	private String externalNo;
	// 运单号
	private String waybillNo;
	// 费用编号
	private String feesNo;
	// WaybillNum
	private Double waybillNum;
	// 运单列表
	private String waybillList;
	// 仓库id
	private String warehouseCode;
	// 仓库名称
	private String warehouseName;
	// 商家id
	private String customerid;
	// 商家名称
	private String customerName;
	// 物流商ID
	private String carrierId;
	// 物流商名称
	private String carrierName;
	// 宅配商id
	private String deliverid;
	// 宅配商名称
	private String deliverName;
	// TotalWeight
	private Double totalWeight;
	// AdjustWeight
	private Double adjustWeight;
	// TotalVolume
	private Double totalVolume;
	// 服务类型
	private String serviceTypeCode;
	// 配送类型
	private String dispatchTypeCode;
	// CollectMoney
	private Double collectMoney;
	// 月结账号
	private String monthFeeCount;
	// 寄件人
	private String sendName;
	// 寄件人省份
	private String sendProvinceId;
	// 寄件人城市
	private String sendCityId;
	// 寄件人地区
	private String sendDistrictId;
	// 寄件人街道
	private String sendStreet;
	// 寄件人详细地址
	private String sendDetailAddress;
	// 收件人
	private String receiveName;
	// 收件人省份
	private String receiveProvinceId;
	// 收件人城市
	private String receiveCityId;
	// 收件人地区
	private String receiveDistrictId;
	// 收件人街道
	private String receiveStreet;
	// 收件人详细地址
	private String receiveDetailAddress;
	// 结算状态
	private String accountState;
	// 签收时间
	private Timestamp signTime;
	// 揽收时间
	private Timestamp acceptTime;
	// 收件时间
	private Timestamp receiveTime;
	// 是否已计算费用 0-未结算 1-已结算 2-结算异常 3-合同不存在 4-不在操作范围 5-其他
	private String isCalculated;
	// 创建者
	private String creator;
	// 单据创建时间
	private Timestamp createTime;
	// 修改者
	private String lastModifier;
	// 修改时间
	private Timestamp lastModifyTime;
	// 写入BMS时间
	private Timestamp writeTime;
	// 费用计算时间
	private Timestamp calculateTime;
	// 删除标志
	private String delFlag;
	// 温度类型
	private String temperatureTypeCode;
	// 温度类型名称
	private String temperatureTypeName;
	// remark
	private String remark;
	// 转寄后运单号
	private String zexpressnum;
	// 大状态
	private String bigstatus;
	// 小状态
	private String smallstatus;
	// SystemWeight
	private Double systemWeight;
	// 抛重
	private Double throwWeight;
	// totalqty
	private Integer totalqty;
	// 商品明细
	private String productDetail;
	// 快递业务类型
	private String expresstype;
	// sku数
	private Integer skuNum;
	// extattr1
	private String extattr1;
	// extattr2
	private String extattr2;
	// extattr3
	private String extattr3;
	// extattr4
	private String extattr4;
	// extattr5
	private String extattr5;
	// 调整宅配商id
	private String adjustDeliverId;
	// 调整宅配商名称
	private String adjustDeliverName;
	// 调整物流商ID
	private String adjustCarrierId;
	// 调整物流商名称
	private String adjustCarrierName;
	// BizType
	private String bizType;
	// 调整省
	private String adjustProvinceId;
	// 调整市
	private String adjustCityId;
	// 调整区
	private String adjustDistrictId;
	// 订单物流商名称
	private String originCarrierName;
	// 订单物流商ID
	private String originCarrierId;
	// 发件省
	private String senderProvince;
	// 发件市
	private String senderCity;
	// 发件区
	private String senderDistrict;

	//责任方
	private String dutyType;
	//原因详情
	private String updateReasonDetail;
	//计费重量
	private Double chargeWeight;
	//计费物流商
	private String chargeCarrierName;
	
	//运费
	private Double dsAmount;
	//折扣后运费
	private Double discountAmount;
	//折扣后操作费
	private Double operateAmount;
	//运费计算状态
	private String dsIsCalculated;
	//运费计算备注
	private String dsRemark;
	//运费
	private Double orderAmount;
	//操作费计算状态
	private String orderIsCalculated;
	//操作费计算备注
	private String orderRemark;
	// B2B标识
	private String b2bFlag;
  	//调整数量
  	private Double resizeNum;
	// 总品种数
	private Double totalVarieties;	
	//箱数
	private Integer boxnum;	
	//调整箱数
	private Integer adjustBoxnum;
	// 首重价格
	private Double headPrice;
	// 续重价格
	private Double continuedPrice;
	//纠正重量
	private Double correctWeight;
	//物流产品
	private String servicecode;
	private String servicename;
	// 订单状态
	private String orderStatus;
	//原始重量
	private Double originWeight;
	
	private Double correctThrowWeight;
	
	//调整物流产品类型
	private String adjustServiceTypeCode;
	private String adjustServiceTypeName;
	
	//店铺名称
	private String shopName;
	
	//物流商重量
	private Double carrierWeight;
	
	//运输方式
	private String transportType;
	
	public Double getCarrierWeight() {
		return carrierWeight;
	}

	public void setCarrierWeight(Double carrierWeight) {
		this.carrierWeight = carrierWeight;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	public BizDispatchBillVo() {

	}
	
	/**
     * id
     */
	public Long getId() {
		return this.id;
	}

    /**
     * id
     *
     * @param id
     */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
     * 出库单号
     */
	public String getOutstockNo() {
		return this.outstockNo;
	}

    /**
     * 出库单号
     *
     * @param outstockNo
     */
	public void setOutstockNo(String outstockNo) {
		this.outstockNo = outstockNo;
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
     * 运单数量
     */
	public Double getWaybillNum() {
		return this.waybillNum;
	}

    /**
     * 运单数量
     *
     * @param waybillNum
     */
	public void setWaybillNum(Double waybillNum) {
		this.waybillNum = waybillNum;
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
     * 仓库id
     */
	public String getWarehouseCode() {
		return this.warehouseCode;
	}

    /**
     * 仓库id
     *
     * @param warehouseCode
     */
	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
	
	/**
     * 仓库名称
     */
	public String getWarehouseName() {
		return this.warehouseName;
	}

    /**
     * 仓库名称
     *
     * @param warehouseName
     */
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	
	/**
     * 商家id
     */
	public String getCustomerid() {
		return this.customerid;
	}

    /**
     * 商家id
     *
     * @param customerid
     */
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	
	/**
     * 商家名称
     */
	public String getCustomerName() {
		return this.customerName;
	}

    /**
     * 商家名称
     *
     * @param customerName
     */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	/**
     * 物流商ID
     */
	public String getCarrierId() {
		return this.carrierId;
	}

    /**
     * 物流商ID
     *
     * @param carrierId
     */
	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}
	
	/**
     * 物流商名称
     */
	public String getCarrierName() {
		return this.carrierName;
	}

    /**
     * 物流商名称
     *
     * @param carrierName
     */
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	
	/**
     * 宅配商id
     */
	public String getDeliverid() {
		return this.deliverid;
	}

    /**
     * 宅配商id
     *
     * @param deliverid
     */
	public void setDeliverid(String deliverid) {
		this.deliverid = deliverid;
	}
	
	/**
     * 宅配商名称
     */
	public String getDeliverName() {
		return this.deliverName;
	}

    /**
     * 宅配商名称
     *
     * @param deliverName
     */
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	
	/**
     * 重量
     */
	public Double getTotalWeight() {
		return this.totalWeight;
	}

    /**
     * 重量
     *
     * @param totalWeight
     */
	public void setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
	}
	
	/**
     * 调整重量
     */
	public Double getAdjustWeight() {
		return this.adjustWeight;
	}

    /**
     * 调整重量
     *
     * @param adjustWeight
     */
	public void setAdjustWeight(Double adjustWeight) {
		this.adjustWeight = adjustWeight;
	}
	
	/**
     * 体积
     */
	public Double getTotalVolume() {
		return this.totalVolume;
	}

    /**
     * 体积
     *
     * @param totalVolume
     */
	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}
	
	/**
     * 服务类型
     */
	public String getServiceTypeCode() {
		return this.serviceTypeCode;
	}

    /**
     * 服务类型
     *
     * @param serviceTypeCode
     */
	public void setServiceTypeCode(String serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
	}
	
	/**
     * 配送类型
     */
	public String getDispatchTypeCode() {
		return this.dispatchTypeCode;
	}

    /**
     * 配送类型
     *
     * @param dispatchTypeCode
     */
	public void setDispatchTypeCode(String dispatchTypeCode) {
		this.dispatchTypeCode = dispatchTypeCode;
	}
	
	/**
     * 代收金额
     */
	public Double getCollectMoney() {
		return this.collectMoney;
	}

    /**
     * 代收金额
     *
     * @param collectMoney
     */
	public void setCollectMoney(Double collectMoney) {
		this.collectMoney = collectMoney;
	}
	
	/**
     * 月结账号
     */
	public String getMonthFeeCount() {
		return this.monthFeeCount;
	}

    /**
     * 月结账号
     *
     * @param monthFeeCount
     */
	public void setMonthFeeCount(String monthFeeCount) {
		this.monthFeeCount = monthFeeCount;
	}
	
	/**
     * 寄件人
     */
	public String getSendName() {
		return this.sendName;
	}

    /**
     * 寄件人
     *
     * @param sendName
     */
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	
	/**
     * 寄件人省份
     */
	public String getSendProvinceId() {
		return this.sendProvinceId;
	}

    /**
     * 寄件人省份
     *
     * @param sendProvinceId
     */
	public void setSendProvinceId(String sendProvinceId) {
		this.sendProvinceId = sendProvinceId;
	}
	
	/**
     * 寄件人城市
     */
	public String getSendCityId() {
		return this.sendCityId;
	}

    /**
     * 寄件人城市
     *
     * @param sendCityId
     */
	public void setSendCityId(String sendCityId) {
		this.sendCityId = sendCityId;
	}
	
	/**
     * 寄件人地区
     */
	public String getSendDistrictId() {
		return this.sendDistrictId;
	}

    /**
     * 寄件人地区
     *
     * @param sendDistrictId
     */
	public void setSendDistrictId(String sendDistrictId) {
		this.sendDistrictId = sendDistrictId;
	}
	
	/**
     * 寄件人街道
     */
	public String getSendStreet() {
		return this.sendStreet;
	}

    /**
     * 寄件人街道
     *
     * @param sendStreet
     */
	public void setSendStreet(String sendStreet) {
		this.sendStreet = sendStreet;
	}
	
	/**
     * 寄件人详细地址
     */
	public String getSendDetailAddress() {
		return this.sendDetailAddress;
	}

    /**
     * 寄件人详细地址
     *
     * @param sendDetailAddress
     */
	public void setSendDetailAddress(String sendDetailAddress) {
		this.sendDetailAddress = sendDetailAddress;
	}
	
	/**
     * 收件人
     */
	public String getReceiveName() {
		return this.receiveName;
	}

    /**
     * 收件人
     *
     * @param receiveName
     */
	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}
	
	/**
     * 收件人省份
     */
	public String getReceiveProvinceId() {
		return this.receiveProvinceId;
	}

    /**
     * 收件人省份
     *
     * @param receiveProvinceId
     */
	public void setReceiveProvinceId(String receiveProvinceId) {
		this.receiveProvinceId = receiveProvinceId;
	}
	
	/**
     * 收件人城市
     */
	public String getReceiveCityId() {
		return this.receiveCityId;
	}

    /**
     * 收件人城市
     *
     * @param receiveCityId
     */
	public void setReceiveCityId(String receiveCityId) {
		this.receiveCityId = receiveCityId;
	}
	
	/**
     * 收件人地区
     */
	public String getReceiveDistrictId() {
		return this.receiveDistrictId;
	}

    /**
     * 收件人地区
     *
     * @param receiveDistrictId
     */
	public void setReceiveDistrictId(String receiveDistrictId) {
		this.receiveDistrictId = receiveDistrictId;
	}
	
	/**
     * 收件人街道
     */
	public String getReceiveStreet() {
		return this.receiveStreet;
	}

    /**
     * 收件人街道
     *
     * @param receiveStreet
     */
	public void setReceiveStreet(String receiveStreet) {
		this.receiveStreet = receiveStreet;
	}
	
	/**
     * 收件人详细地址
     */
	public String getReceiveDetailAddress() {
		return this.receiveDetailAddress;
	}

    /**
     * 收件人详细地址
     *
     * @param receiveDetailAddress
     */
	public void setReceiveDetailAddress(String receiveDetailAddress) {
		this.receiveDetailAddress = receiveDetailAddress;
	}
	
	/**
     * 收件时间
     */
	public Timestamp getReceiveTime() {
		return this.receiveTime;
	}

    /**
     * 收件时间
     *
     * @param receiveTime
     */
	public void setReceiveTime(Timestamp receiveTime) {
		this.receiveTime = receiveTime;
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
     * 结算状态
     */
	public String getAccountState() {
		return this.accountState;
	}

    /**
     * 结算状态
     *
     * @param accountState
     */
	public void setAccountState(String accountState) {
		this.accountState = accountState;
	}
	
	/**
     * 外部单号
     */
	public String getExternalNo() {
		return this.externalNo;
	}

    /**
     * 外部单号
     *
     * @param externalNo
     */
	public void setExternalNo(String externalNo) {
		this.externalNo = externalNo;
	}
	
	/**
     * 签收时间
     */
	public Timestamp getSignTime() {
		return this.signTime;
	}

    /**
     * 签收时间
     *
     * @param signTime
     */
	public void setSignTime(Timestamp signTime) {
		this.signTime = signTime;
	}
	
	/**
     * 揽收时间
     */
	public Timestamp getAcceptTime() {
		return this.acceptTime;
	}

    /**
     * 揽收时间
     *
     * @param acceptTime
     */
	public void setAcceptTime(Timestamp acceptTime) {
		this.acceptTime = acceptTime;
	}
	
	/**
     * 创建者
     */
	public String getCreator() {
		return this.creator;
	}

    /**
     * 创建者
     *
     * @param creator
     */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	/**
     * 创建时间
     */
	public Timestamp getCreateTime() {
		return this.createTime;
	}

    /**
     * 创建时间
     *
     * @param createTime
     */
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	/**
     * 修改者
     */
	public String getLastModifier() {
		return this.lastModifier;
	}

    /**
     * 修改者
     *
     * @param lastModifier
     */
	public void setLastModifier(String lastModifier) {
		this.lastModifier = lastModifier;
	}
	
	/**
     * 修改时间
     */
	public Timestamp getLastModifyTime() {
		return this.lastModifyTime;
	}

    /**
     * 修改时间
     *
     * @param lastModifyTime
     */
	public void setLastModifyTime(Timestamp lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	
	/**
     * 删除标志
     */
	public String getDelFlag() {
		return this.delFlag;
	}

    /**
     * 删除标志
     *
     * @param delFlag
     */
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Timestamp getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(Timestamp writeTime) {
		this.writeTime = writeTime;
	}

	public Timestamp getCalculateTime() {
		return calculateTime;
	}

	public void setCalculateTime(Timestamp calculateTime) {
		this.calculateTime = calculateTime;
	}

	public String getDutyType() {
		return dutyType;
	}

	public void setDutyType(String dutyType) {
		this.dutyType = dutyType;
	}

	public String getUpdateReasonDetail() {
		return updateReasonDetail;
	}

	public void setUpdateReasonDetail(String updateReasonDetail) {
		this.updateReasonDetail = updateReasonDetail;
	}

	public Double getChargeWeight() {
		return chargeWeight;
	}

	public void setChargeWeight(Double chargeWeight) {
		this.chargeWeight = chargeWeight;
	}

	public String getChargeCarrierName() {
		return chargeCarrierName;
	}

	public void setChargeCarrierName(String chargeCarrierName) {
		this.chargeCarrierName = chargeCarrierName;
	}

	public Double getDsAmount() {
		return dsAmount;
	}

	public void setDsAmount(Double dsAmount) {
		this.dsAmount = dsAmount;
	}

	public String getDsIsCalculated() {
		return dsIsCalculated;
	}

	public void setDsIsCalculated(String dsIsCalculated) {
		this.dsIsCalculated = dsIsCalculated;
	}

	public String getDsRemark() {
		return dsRemark;
	}

	public void setDsRemark(String dsRemark) {
		this.dsRemark = dsRemark;
	}

	public Double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getOrderIsCalculated() {
		return orderIsCalculated;
	}

	public void setOrderIsCalculated(String orderIsCalculated) {
		this.orderIsCalculated = orderIsCalculated;
	}

	public String getOrderRemark() {
		return orderRemark;
	}

	public void setOrderRemark(String orderRemark) {
		this.orderRemark = orderRemark;
	}

	public String getB2bFlag() {
		return b2bFlag;
	}

	public void setB2bFlag(String b2bFlag) {
		this.b2bFlag = b2bFlag;
	}

	public Double getResizeNum() {
		return resizeNum;
	}

	public void setResizeNum(Double resizeNum) {
		this.resizeNum = resizeNum;
	}

	public Double getTotalVarieties() {
		return totalVarieties;
	}

	public void setTotalVarieties(Double totalVarieties) {
		this.totalVarieties = totalVarieties;
	}

	public Integer getBoxnum() {
		return boxnum;
	}

	public void setBoxnum(Integer boxnum) {
		this.boxnum = boxnum;
	}

	public Integer getAdjustBoxnum() {
		return adjustBoxnum;
	}

	public void setAdjustBoxnum(Integer adjustBoxnum) {
		this.adjustBoxnum = adjustBoxnum;
	}

	public String getWaybillList() {
		return waybillList;
	}

	public void setWaybillList(String waybillList) {
		this.waybillList = waybillList;
	}

	public String getTemperatureTypeCode() {
		return temperatureTypeCode;
	}

	public void setTemperatureTypeCode(String temperatureTypeCode) {
		this.temperatureTypeCode = temperatureTypeCode;
	}

	public String getTemperatureTypeName() {
		return temperatureTypeName;
	}

	public void setTemperatureTypeName(String temperatureTypeName) {
		this.temperatureTypeName = temperatureTypeName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getZexpressnum() {
		return zexpressnum;
	}

	public void setZexpressnum(String zexpressnum) {
		this.zexpressnum = zexpressnum;
	}

	public String getBigstatus() {
		return bigstatus;
	}

	public void setBigstatus(String bigstatus) {
		this.bigstatus = bigstatus;
	}

	public String getSmallstatus() {
		return smallstatus;
	}

	public void setSmallstatus(String smallstatus) {
		this.smallstatus = smallstatus;
	}

	public Double getSystemWeight() {
		return systemWeight;
	}

	public void setSystemWeight(Double systemWeight) {
		this.systemWeight = systemWeight;
	}

	public Double getThrowWeight() {
		return throwWeight;
	}

	public void setThrowWeight(Double throwWeight) {
		this.throwWeight = throwWeight;
	}

	public Integer getTotalqty() {
		return totalqty;
	}

	public void setTotalqty(Integer totalqty) {
		this.totalqty = totalqty;
	}

	public String getProductDetail() {
		return productDetail;
	}

	public void setProductDetail(String productDetail) {
		this.productDetail = productDetail;
	}

	public String getExpresstype() {
		return expresstype;
	}

	public void setExpresstype(String expresstype) {
		this.expresstype = expresstype;
	}

	public Integer getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(Integer skuNum) {
		this.skuNum = skuNum;
	}

	public String getExtattr1() {
		return extattr1;
	}

	public void setExtattr1(String extattr1) {
		this.extattr1 = extattr1;
	}

	public String getExtattr2() {
		return extattr2;
	}

	public void setExtattr2(String extattr2) {
		this.extattr2 = extattr2;
	}

	public String getExtattr3() {
		return extattr3;
	}

	public void setExtattr3(String extattr3) {
		this.extattr3 = extattr3;
	}

	public String getExtattr4() {
		return extattr4;
	}

	public void setExtattr4(String extattr4) {
		this.extattr4 = extattr4;
	}

	public String getExtattr5() {
		return extattr5;
	}

	public void setExtattr5(String extattr5) {
		this.extattr5 = extattr5;
	}

	public String getAdjustDeliverId() {
		return adjustDeliverId;
	}

	public void setAdjustDeliverId(String adjustDeliverId) {
		this.adjustDeliverId = adjustDeliverId;
	}

	public String getAdjustDeliverName() {
		return adjustDeliverName;
	}

	public void setAdjustDeliverName(String adjustDeliverName) {
		this.adjustDeliverName = adjustDeliverName;
	}

	public String getAdjustCarrierId() {
		return adjustCarrierId;
	}

	public void setAdjustCarrierId(String adjustCarrierId) {
		this.adjustCarrierId = adjustCarrierId;
	}

	public String getAdjustCarrierName() {
		return adjustCarrierName;
	}

	public void setAdjustCarrierName(String adjustCarrierName) {
		this.adjustCarrierName = adjustCarrierName;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getAdjustProvinceId() {
		return adjustProvinceId;
	}

	public void setAdjustProvinceId(String adjustProvinceId) {
		this.adjustProvinceId = adjustProvinceId;
	}

	public String getAdjustCityId() {
		return adjustCityId;
	}

	public void setAdjustCityId(String adjustCityId) {
		this.adjustCityId = adjustCityId;
	}

	public String getAdjustDistrictId() {
		return adjustDistrictId;
	}

	public void setAdjustDistrictId(String adjustDistrictId) {
		this.adjustDistrictId = adjustDistrictId;
	}

	public String getOriginCarrierName() {
		return originCarrierName;
	}

	public void setOriginCarrierName(String originCarrierName) {
		this.originCarrierName = originCarrierName;
	}

	public String getOriginCarrierId() {
		return originCarrierId;
	}

	public void setOriginCarrierId(String originCarrierId) {
		this.originCarrierId = originCarrierId;
	}

	public String getSenderProvince() {
		return senderProvince;
	}

	public void setSenderProvince(String senderProvince) {
		this.senderProvince = senderProvince;
	}

	public String getSenderCity() {
		return senderCity;
	}

	public void setSenderCity(String senderCity) {
		this.senderCity = senderCity;
	}

	public String getSenderDistrict() {
		return senderDistrict;
	}

	public void setSenderDistrict(String senderDistrict) {
		this.senderDistrict = senderDistrict;
	}

	public Double getHeadPrice() {
		return headPrice;
	}

	public void setHeadPrice(Double headPrice) {
		this.headPrice = headPrice;
	}

	public Double getContinuedPrice() {
		return continuedPrice;
	}

	public void setContinuedPrice(Double continuedPrice) {
		this.continuedPrice = continuedPrice;
	}

	public Double getCorrectWeight() {
		return correctWeight;
	}

	public void setCorrectWeight(Double correctWeight) {
		this.correctWeight = correctWeight;
	}

	public String getServicecode() {
		return servicecode;
	}

	public void setServicecode(String servicecode) {
		this.servicecode = servicecode;
	}

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public Double getOriginWeight() {
		return originWeight;
	}

	public void setOriginWeight(Double originWeight) {
		this.originWeight = originWeight;
	}


	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Double getCorrectThrowWeight() {
		return correctThrowWeight;
	}

	public void setCorrectThrowWeight(Double correctThrowWeight) {
		this.correctThrowWeight = correctThrowWeight;
	}

	public String getAdjustServiceTypeCode() {
		return adjustServiceTypeCode;
	}

	public void setAdjustServiceTypeCode(String adjustServiceTypeCode) {
		this.adjustServiceTypeCode = adjustServiceTypeCode;
	}

	public String getAdjustServiceTypeName() {
		return adjustServiceTypeName;
	}

	public void setAdjustServiceTypeName(String adjustServiceTypeName) {
		this.adjustServiceTypeName = adjustServiceTypeName;
	}

	public Double getOperateAmount() {
		return operateAmount;
	}

	public void setOperateAmount(Double operateAmount) {
		this.operateAmount = operateAmount;
	}

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    
	
}
