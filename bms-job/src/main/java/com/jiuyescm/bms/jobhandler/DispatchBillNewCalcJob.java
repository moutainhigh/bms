package com.jiuyescm.bms.jobhandler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyescm.bms.base.calcu.vo.CalcuReqVo;
import com.jiuyescm.bms.base.dictionary.entity.SystemCodeEntity;
import com.jiuyescm.bms.base.group.service.IBmsGroupCustomerService;
import com.jiuyescm.bms.base.group.service.IBmsGroupService;
import com.jiuyescm.bms.base.group.service.IBmsGroupSubjectService;
import com.jiuyescm.bms.base.group.vo.BmsGroupCustomerVo;
import com.jiuyescm.bms.base.group.vo.BmsGroupVo;
import com.jiuyescm.bms.base.monthFeeCount.service.IPubMonthFeeCountService;
import com.jiuyescm.bms.base.monthFeeCount.vo.PubMonthFeeCountVo;
import com.jiuyescm.bms.biz.dispatch.entity.BizDispatchBillEntity;
import com.jiuyescm.bms.biz.storage.repository.IBizOutstockPackmaterialRepository;
import com.jiuyescm.bms.chargerule.receiverule.entity.BillRuleReceiveEntity;
import com.jiuyescm.bms.common.enumtype.CalculateState;
import com.jiuyescm.bms.common.enumtype.TemplateTypeEnum;
import com.jiuyescm.bms.correct.BmsMarkingProductsEntity;
import com.jiuyescm.bms.correct.repository.IBmsProductsWeightRepository;
import com.jiuyescm.bms.drools.IFeesCalcuService;
import com.jiuyescm.bms.general.entity.BizDispatchCarrierChangeEntity;
import com.jiuyescm.bms.general.entity.FeesReceiveDispatchEntity;
import com.jiuyescm.bms.general.service.IFeesReceiveDispatchService;
import com.jiuyescm.bms.general.service.IFeesReceiveStorageService;
import com.jiuyescm.bms.general.service.IPriceContractInfoService;
import com.jiuyescm.bms.general.service.ISystemCodeService;
import com.jiuyescm.bms.general.service.SequenceService;
import com.jiuyescm.bms.quotation.contract.entity.PriceContractInfoEntity;
import com.jiuyescm.bms.quotation.contract.entity.PriceContractItemEntity;
import com.jiuyescm.bms.quotation.contract.repository.imp.IPriceContractItemRepository;
import com.jiuyescm.bms.quotation.dispatch.entity.vo.BmsQuoteDispatchDetailVo;
import com.jiuyescm.bms.quotation.dispatch.repository.IPriceDispatchDao;
import com.jiuyescm.bms.receivable.dispatch.service.IBizDispatchBillService;
import com.jiuyescm.bms.rule.receiveRule.repository.IReceiveRuleRepository;
import com.jiuyescm.bs.util.StringUtil;
import com.jiuyescm.cfm.common.JAppContext;
import com.jiuyescm.common.utils.DoubleUtil;
import com.jiuyescm.contract.quote.api.IContractQuoteInfoService;
import com.jiuyescm.contract.quote.vo.ContractBizTypeEnum;
import com.jiuyescm.contract.quote.vo.ContractQuoteInfoVo;
import com.jiuyescm.contract.quote.vo.ContractQuoteQueryInfoVo;
import com.jiuyescm.exception.BizException;
import com.jiuyescm.mdm.carrier.api.ICarrierService;
import com.jiuyescm.mdm.carrier.vo.CarrierVo;
import com.jiuyescm.mdm.customer.api.IAddressService;
import com.jiuyescm.mdm.customer.api.ICustomerService;
import com.jiuyescm.mdm.customer.api.IPubMaterialInfoService;
import com.jiuyescm.mdm.customer.vo.PubMaterialInfoVo;
import com.jiuyescm.mdm.customer.vo.RegionVo;
import com.jiuyescm.mdm.warehouse.api.IWarehouseService;
import com.jiuyescm.mdm.warehouse.vo.WarehouseVo;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;

@JobHander(value="dispatchBillNewCalcJob")
@Service
public class DispatchBillNewCalcJob extends CommonJobHandler<BizDispatchBillEntity,FeesReceiveDispatchEntity> {

	@Autowired private IBizDispatchBillService bizDispatchBillService;
	@Autowired private SequenceService sequenceService;
	@Autowired private IFeesReceiveDispatchService feesReceiveDispatchService;
	@Autowired private IPriceContractInfoService jobPriceContractInfoService;
	@Autowired private ISystemCodeService systemCodeService;
	@Autowired private IAddressService omsAddressService;
	@Autowired private IPriceContractItemRepository priceContractItemRepository;
	@Autowired private IPriceDispatchDao iPriceDispatchDao;
	@Autowired private IReceiveRuleRepository receiveRuleRepository;
	@Autowired private IFeesCalcuService feesCalcuService;
	@Autowired private ICarrierService carrierService;
	@Autowired private IBmsGroupService bmsGroupService;
	@Autowired private IBmsGroupCustomerService bmsGroupCustomerService;
	@Autowired private ICustomerService customerService;
	@Autowired private IWarehouseService warehouseService;
	@Autowired private IBmsProductsWeightRepository bmsProductsWeightRepository;
	@Autowired private IBizOutstockPackmaterialRepository bizOutstockPackmaterialRepository;
	@Autowired private IPubMaterialInfoService pubMaterialInfoService;
	@Autowired private IContractQuoteInfoService contractQuoteInfoService;
	@Autowired private IBmsGroupSubjectService bmsGroupSubjectService;
	@Autowired private IFeesReceiveStorageService feesReceiveStorageService;
	@Autowired private IPubMonthFeeCountService pubMonthFeeCountService;
	
	private String BizTypeCode = "DISPATCH"; //配送费编码
	private String contractTypeCode="CUSTOMER_CONTRACT";
	
	private String _subjectCode = "de_delivery_amount";
	
	//private String subject="";
	
	List<SystemCodeEntity> scList = null;
	List<SystemCodeEntity> no_fees_delivers = null;
	List<SystemCodeEntity> monthFeeList=null;
	List<String> throwWeightList=null;
	Map<String,PriceContractInfoEntity> mapContact=null;
	List<String> changeCusList=null;
	Map<String, String> carrierMap=null;
	List<String> cancelCusList=null;
	Map<String,WarehouseVo> wareMap=null;
	List<String> monthCountList=null;
	
	@Override
	protected List<BizDispatchBillEntity> queryBillList(Map<String, Object> map) {
		XxlJobLogger.log("dispatchBillNewCalcJob查询条件map:【{0}】  ",map);
		Long current = System.currentTimeMillis();
		List<BizDispatchBillEntity> billList = bizDispatchBillService.query(map);
		//有待计算的数据时初始化配置
		if(billList!=null && billList.size()>0){
			XxlJobLogger.log("【配送运单】查询行数【{0}】耗时【{1}】",billList.size(),(System.currentTimeMillis()-current));
			initConf();
		}
		return billList;
	}

	@Override
	protected String[] initSubjects() {  
		//这里的科目应该在科目组中配置,动态查询
		//de_delivery_amount(运费 )
		Map<String,String> map=bmsGroupSubjectService.getSubject("job_subject_dispatch");
		if(map.size() == 0){
			String[] strs = {"de_delivery_amount"};
			return strs;
		}else{
			int i=0;
			String[] strs=new String[map.size()];
			for(String value:map.keySet()){
				strs[i]=value;	
				i++;
			}
			return strs;
		}
	}
	
	@Override
	public boolean isJoin(BizDispatchBillEntity entity) {
		return true;
	}
	
	@Override
	public void calcu(BizDispatchBillEntity entity, FeesReceiveDispatchEntity feeEntity) {
		ContractQuoteInfoVo modelEntity = getContractForWhat(entity);
		if(modelEntity == null || StringUtil.isEmpty(modelEntity.getTemplateCode())){
			calcuForBms(entity, feeEntity);
		}
		else{
			XxlJobLogger.log("-->"+entity.getId()+"规则编号：  "+modelEntity.getRuleCode().trim());
			calcuForContract(entity,feeEntity,modelEntity);
		}
		
	}
	
	protected void initConf(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("typeCode", "DISPATCH_COMPANY");
		scList = systemCodeService.querySysCodes(map);
		map = new HashMap<String, Object>();
		map.put("typeCode", "NO_FEES_DELIVER");
		no_fees_delivers = systemCodeService.querySysCodes(map);//查询不计算费用的宅配商列表，如客户自提，商家自提等
		
		map= new HashMap<String, Object>();
		map.put("typeCode", "MONTH_FEE_COUNT");
		monthFeeList = systemCodeService.querySysCodes(map);//月结账号
		
		map= new HashMap<String, Object>();
		throwWeightList=new ArrayList<String>();
		map.put("typeCode", "THROW_WEIGHT_CARRIER");
		List<SystemCodeEntity> throwList = systemCodeService.querySysCodes(map);//计抛的物流商
		for(SystemCodeEntity s:throwList){
			throwWeightList.add(s.getCode());
		}
		
		mapContact=new ConcurrentHashMap<String,PriceContractInfoEntity>();
		
		//指定的商家
		map= new HashMap<String, Object>();
		map.put("groupCode", "jiuye_to_shunfeng");
		map.put("bizType", "group_customer");
		BmsGroupVo bmsGroup=bmsGroupService.queryOne(map);
		if(bmsGroup!=null){
			changeCusList=bmsGroupCustomerService.queryCustomerByGroupId(bmsGroup.getId());
		}
		//指定需要计算取消状态单子的商家
		map= new HashMap<String, Object>();
		map.put("groupCode", "calculate_cancel_customer");
		map.put("bizType", "group_customer");
		BmsGroupVo bmsCancelCus=bmsGroupService.queryOne(map);
		if(bmsCancelCus!=null){
			cancelCusList=bmsGroupCustomerService.queryCustomerByGroupId(bmsCancelCus.getId());
		}
			
		//物流商
		carrierMap=getCarrier();
		
		//仓库
		wareMap=new HashMap<String,WarehouseVo>();
		List<WarehouseVo> wareList=warehouseService.queryAllWarehouse();
		for(WarehouseVo vo:wareList){
			wareMap.put(vo.getWarehouseid(), vo);
		}
		
		//月结账号
		map= new HashMap<String, Object>();
		map.put("ownflag", "0");
		map.put("delflag", "0");
		List<PubMonthFeeCountVo> monthFeeList=pubMonthFeeCountService.query(map);
		monthCountList=new ArrayList<>();
		if(monthFeeList.size()>0){
			for(PubMonthFeeCountVo en:monthFeeList){
				monthCountList.add(en.getCarrierId()+"&"+en.getMonthFeeCount());
			}
		}
	}
	
	@Override
	public Integer deleteFeesBatch(List<BizDispatchBillEntity> list) {
		Map<String, Object> feesMap = new HashMap<String, Object>();
		List<String> feesNos = new ArrayList<String>();
		for (BizDispatchBillEntity entity : list) {
			if(StringUtils.isNotEmpty(entity.getFeesNo())){
				feesNos.add(entity.getFeesNo());
			}
			else{
				entity.setFeesNo(sequenceService.getBillNoOne(BizDispatchBillEntity.class.getName(), "STO", "0000000000"));
			}
		}
		try{
			if(feesNos.size()>0){
				feesMap.put("feesNos", feesNos);
				feesReceiveDispatchService.deleteBatch(feesMap);
			}
		}
		catch(Exception ex){
			XxlJobLogger.log("批量删除费用失败-- {1}",ex.getMessage());
		}
		return null;
	}
	
	
	@Override
	public FeesReceiveDispatchEntity initFeeEntity(BizDispatchBillEntity entity){
		entity.setRemark("");
		long start = System.currentTimeMillis();// 系统开始时间
		long current = 0L;// 当前系统时间
		
		FeesReceiveDispatchEntity feeEntity = new FeesReceiveDispatchEntity();
		//计费参数获取
        //1)***********************获取计费物流商******************************
		String subject=getSubjectId(getCarrierId(entity));
		String carrierId=entity.getChargeCarrierId();
		XxlJobLogger.log("-->"+entity.getId()+"验证数据时物流商-- 【{0}】  ",entity.getChargeCarrierId());
		current = System.currentTimeMillis();
		XxlJobLogger.log("-->"+entity.getId()+"验证物流商  耗时【{0}】毫秒 ",(current - start));	
		
		//2)***********************获取新泡重************************
		start = System.currentTimeMillis();// 操作开始时间
		//新增逻辑，若要按抛重计算，此时的泡重需要我们自己去计算运单中所有耗材中最大的体积/6000
		double newThrowWeight=getNewThrowWeight(entity);
		//将新泡重赋值
		//entity.setThrowWeight(newThrowWeight);
		entity.setCorrectThrowWeight(newThrowWeight);	
		current = System.currentTimeMillis();
		XxlJobLogger.log("-->"+entity.getId()+"泡重获取  耗时【{0}】毫秒 ",(current - start));
		
		//3)***********************获取省市区和计费重量***********************
		start = System.currentTimeMillis();// 操作开始时间
		String provinceId="";
		String cityId="";
		String districtId="";
		//调整省市区不为空 优先取调整省市区
		if(StringUtils.isNotBlank(entity.getAdjustProvinceId())||StringUtils.isNotBlank(entity.getAdjustCityId())||StringUtils.isNotBlank(entity.getAdjustDistrictId())){
			provinceId=entity.getAdjustProvinceId();
			cityId=entity.getAdjustCityId();
			districtId=entity.getAdjustDistrictId();
		}else{
			provinceId=entity.getReceiveProvinceId();
			cityId=entity.getReceiveCityId();
			districtId=entity.getReceiveDistrictId();
		}
		
		//根据仓库获取发件人省市
	    /*WarehouseVo wareVo=wareMap.get(entity.getWarehouseCode());
	    if(wareVo!=null){
	    	entity.setSendProvinceId(wareVo.getProvince());
	    	entity.setSendCityId(wareVo.getCity());
	    }*/
		
		
		//如果是顺丰配送 匹配标准地址
		if(throwWeightList.contains(carrierId)){
			RegionVo vo = new RegionVo(ReplaceChar(provinceId), ReplaceChar(cityId),ReplaceChar(districtId));
			RegionVo matchVo = omsAddressService.queryNameByAlias(vo);
			if ((StringUtils.isNotBlank(provinceId) && StringUtils.isBlank(matchVo.getProvince())) ||
					StringUtils.isNotBlank(cityId) && StringUtils.isBlank(matchVo.getCity()) ||
					StringUtils.isNotBlank(districtId) && StringUtils.isBlank(matchVo.getDistrict())) 
			{
				XxlJobLogger.log("-->"+entity.getId()+"收件人地址存在空值  省【{0}】市【{1}】区【{2}】 运单号【{3}】:"+ provinceId,cityId,districtId,entity.getWaybillNo());
			}else{
				entity.setReceiveProvinceId(matchVo.getProvince());
				entity.setReceiveCityId(matchVo.getCity());
				entity.setReceiveDistrictId(matchVo.getDistrict());
				
				feeEntity.setToProvinceName(matchVo.getProvince());
				feeEntity.setToCityName(matchVo.getCity());
				feeEntity.setToDistrictName(matchVo.getDistrict());
			}
			
			//如果存在调整重量，优先取
			if(!DoubleUtil.isBlank(entity.getAdjustWeight())){
				double dd = getResult(entity.getAdjustWeight(),carrierId);
				entity.setWeight(dd);
				entity.setTotalWeight(entity.getAdjustWeight());
			}
			else{
				//新增逻辑判断，判断是否有修正重量，用运单号去修正表里去查询
				boolean hasCorrect = false;
				double correctWeight = 0.0;
				Map<String,Object> condition=new HashMap<String,Object>();
				condition.put("waybillNo", entity.getWaybillNo());
				BmsMarkingProductsEntity markEntity=bmsProductsWeightRepository.queryMarkVo(condition);
				if(markEntity!=null && markEntity.getCorrectWeight()!=null && !DoubleUtil.isBlank(markEntity.getCorrectWeight().doubleValue())){
					hasCorrect = true;
					correctWeight = markEntity.getCorrectWeight().doubleValue();
				}
						
				//纠正泡重不存在
				if(DoubleUtil.isBlank(entity.getCorrectThrowWeight())){
					if (hasCorrect) {
						//有纠正重量时，直接比较纠正重量和物流商重量
						if(entity.getCarrierWeight()>=correctWeight){
							//物流商重量大于等于纠正重量
							double dd = getResult(entity.getCarrierWeight(),carrierId);
							entity.setWeight(dd);
							entity.setTotalWeight(entity.getCarrierWeight());
						}else{
							//物流商重量小于纠正重量
							double dd = getResult(correctWeight,carrierId);
							entity.setWeight(dd);
							double resultWeight = compareWeight(entity.getTotalWeight(), 
									getResult(entity.getTotalWeight(),carrierId), correctWeight);
							entity.setTotalWeight(resultWeight);
						}
						
						
					/*	double dd = getResult(correctWeight);
						entity.setWeight(dd);
						// 有纠正重量，比较纠正重量和运单重量是否相等
						double resultWeight = compareWeight(entity.getTotalWeight(), 
								getResult(entity.getTotalWeight()), correctWeight);
						entity.setTotalWeight(resultWeight);*/
					}else {
						//没有纠正重量，物流商重量和运单重量比较
						if(entity.getCarrierWeight()>=entity.getTotalWeight()){
							//物流商重量大于等于运单重量
							double dd = getResult(entity.getCarrierWeight(),carrierId);
							entity.setWeight(dd);
							entity.setTotalWeight(entity.getCarrierWeight());
						}else{
							//物流商重量小于重量
							double dd = getResult(entity.getTotalWeight(),carrierId);
							entity.setWeight(dd);
							entity.setTotalWeight(entity.getTotalWeight());
						}
						/*if(!DoubleUtil.isBlank(entity.getTotalWeight())){
							double dd = getResult(entity.getTotalWeight());
							entity.setWeight(dd);
							//实际重量存在时取实际重量
							entity.setTotalWeight(entity.getTotalWeight());
						}else{
							XxlJobLogger.log("-->"+entity.getId()+"--------顺丰非同城，纠正重量，泡重和实际重量都不存在，无法计算--------");
							entity.setIsCalculated(CalculateState.Sys_Error.getCode());
							feeEntity.setIsCalculated(CalculateState.Sys_Error.getCode());
							entity.setRemark(entity.getRemark()+"顺丰非同城，泡重和实际重量都不存在，无法计算;");
						}*/
					}
					
				}else{
					//泡重存在时
					if (hasCorrect) {
						// 有纠正重量时比较 泡重和 纠正重量
						if(correctWeight >= entity.getCorrectThrowWeight()){
							// 纠正重量大于抛重重量，按纠正重量算
							double dd = getResult(correctWeight,carrierId);
							entity.setWeight(dd);
							// 有纠正重量，比较纠正重量和运单重量是否相等
							double resultWeight = compareWeight(entity.getTotalWeight(), 
									getResult(entity.getTotalWeight(),carrierId), correctWeight);
							entity.setTotalWeight(resultWeight);
						}else if(correctWeight < entity.getCorrectThrowWeight()){
							// 纠正重量小于抛重时，按抛重算
							double dd = getResult(entity.getCorrectThrowWeight(),carrierId);
							entity.setWeight(dd);//计费重量
							entity.setTotalWeight(entity.getCorrectThrowWeight()); //实际重量 eg:5.1
						}
					}else {
						if(!DoubleUtil.isBlank(entity.getTotalWeight())){
							// 没有纠正重量时比较 泡重和 实际重量
							if(entity.getTotalWeight() >= entity.getCorrectThrowWeight()){
								// 运单重量大于抛重时，按运单重量算
								double dd = getResult(entity.getTotalWeight(),carrierId);
								entity.setWeight(dd);
								entity.setTotalWeight(entity.getTotalWeight());
							}else if(entity.getTotalWeight() < entity.getCorrectThrowWeight()){
								// 运单重量小于抛重时，按抛重算
								double dd = getResult(entity.getCorrectThrowWeight(),carrierId);
								entity.setWeight(dd);//计费重量
								entity.setTotalWeight(entity.getCorrectThrowWeight()); //实际重量 eg:5.1
							}
						}else{
							//实际重量为空时，直接取泡重
							double dd = getResult(entity.getCorrectThrowWeight(),carrierId);
							entity.setWeight(dd);
							entity.setTotalWeight(entity.getCorrectThrowWeight());
						}
					}
				}	
			}
		}else{
			RegionVo vo = new RegionVo(ReplaceChar(provinceId), ReplaceChar(cityId),ReplaceChar(districtId));
			RegionVo matchVo = omsAddressService.queryNameByAlias(vo);
			if ((StringUtils.isNotBlank(provinceId) && StringUtils.isBlank(matchVo.getProvince())) ||
					StringUtils.isNotBlank(cityId) && StringUtils.isBlank(matchVo.getCity())) {
				XxlJobLogger.log("-->"+entity.getId()+"收件人地址存在空值  省【{0}】市【{1}】区【{2}】 运单号【{3}】:"+ provinceId,cityId,districtId,entity.getWaybillNo());
			}else{
				if(StringUtils.isNotBlank(entity.getAdjustProvinceId())||
						StringUtils.isNotBlank(entity.getAdjustCityId())||
						StringUtils.isNotBlank(entity.getAdjustDistrictId())){
					entity.setAdjustProvinceId(matchVo.getProvince());
					entity.setAdjustCityId(matchVo.getCity());
					entity.setReceiveProvinceId(matchVo.getProvince());
					entity.setReceiveCityId(matchVo.getCity());
				}else{
					entity.setReceiveProvinceId(matchVo.getProvince());
					entity.setReceiveCityId(matchVo.getCity());
				}
				feeEntity.setToProvinceName(matchVo.getProvince());
				feeEntity.setToCityName(matchVo.getCity());
			}
			
			if(!DoubleUtil.isBlank(entity.getAdjustWeight())){
				entity.setWeight(Math.ceil(entity.getAdjustWeight())); //计费重量 : 6
				entity.setTotalWeight(entity.getAdjustWeight()); //实际重量 eg:5.1
			}
			else{
				//新增逻辑判断，判断是否有修正重量，用运单号去修正表里去查询
				Map<String,Object> condition=new HashMap<String,Object>();
				condition.put("waybillNo", entity.getWaybillNo());
				BmsMarkingProductsEntity markEntity=bmsProductsWeightRepository.queryMarkVo(condition);
				if(markEntity!=null && markEntity.getCorrectWeight()!=null && !DoubleUtil.isBlank(markEntity.getCorrectWeight().doubleValue())){
					double weight=markEntity.getCorrectWeight().doubleValue();
					entity.setWeight(Math.ceil(weight));//计费重量 : 6
					// 比较重量
					double resultWeight = compareWeight(entity.getTotalWeight(), 
							Math.ceil(entity.getTotalWeight()), weight);
					entity.setTotalWeight(resultWeight);//实际重量 eg:5.1
				}else{
					entity.setWeight(Math.ceil(entity.getTotalWeight()));//计费重量 : 6
					entity.setTotalWeight(entity.getTotalWeight());//实际重量 eg:5.1
				}

			}
		}
		
		//此时费用写入计费重量
		feeEntity.setChargedWeight(entity.getWeight());
		
		//费用初始化
		feeEntity.setCreator("system");
		feeEntity.setCreateTime(entity.getCreateTime());//费用表的创建时间应为业务表的创建时间
		feeEntity.setDeliveryDate(entity.getCreateTime());
		feeEntity.setOutstockNo(entity.getOutstockNo());		// 出库单号
		feeEntity.setWarehouseCode(entity.getWarehouseCode());	// 仓库ID
		feeEntity.setWarehouseName(entity.getWarehouseName());  // 仓库名称
		feeEntity.setCustomerid(entity.getCustomerid());		// 商家ID
		feeEntity.setCustomerName(entity.getCustomerName());	// 商家名称
		feeEntity.setDeliveryid(entity.getDeliverid());         // 物流商ID
		feeEntity.setDeliverName(entity.getDeliverName());		// 物流商名称
		feeEntity.setCarrierid(entity.getChargeCarrierId());
		feeEntity.setCarrierName(carrierMap.get(entity.getChargeCarrierId()));
        feeEntity.setWaybillNo(entity.getWaybillNo());			// 运单号
		feeEntity.setTotalWeight(entity.getTotalWeight());//实际重量
		feeEntity.setSubjectCode(_subjectCode);
		feeEntity.setOtherSubjectCode(_subjectCode);
		feeEntity.setToProvinceName(provinceId);// 目的省
		feeEntity.setToCityName(cityId);			// 目的市			
		feeEntity.setToDistrictName(districtId);// 目的区	
		feeEntity.setExternalNo(entity.getExternalNo());        //外部单号
		feeEntity.setAcceptTime(entity.getAcceptTime());        //揽收时间
		feeEntity.setSignTime(entity.getSignTime());
		feeEntity.setStatus("0");
		feeEntity.setDelFlag("0");
		feeEntity.setAmount(0.0d);					//入仓金额
		feeEntity.setTemperatureType(entity.getTemperatureTypeCode());//温度
		feeEntity.setFeesNo(entity.getFeesNo());
		feeEntity.setParam1(TemplateTypeEnum.COMMON.getCode());
		feeEntity.setBigstatus(entity.getBigstatus());
		feeEntity.setSmallstatus(entity.getSmallstatus());
		feeEntity.setChargedWeight(entity.getWeight());   //从weight中取出计费重量
		feeEntity.setWeightLimit(0.0d);
		feeEntity.setUnitPrice(0.0d);
		feeEntity.setHeadWeight(0.0d);
		feeEntity.setHeadPrice(0.0d);
		feeEntity.setContinuedWeight(0.0d);
		feeEntity.setContinuedPrice(0.0d);
		feeEntity.setPriceId("");
		feeEntity.setServiceTypeCode(StringUtils.isEmpty(entity.getAdjustServiceTypeCode())?entity.getServiceTypeCode():entity.getAdjustServiceTypeCode());
		return feeEntity;
		
	}
	
	protected boolean validateData(BizDispatchBillEntity entity,FeesReceiveDispatchEntity feeEntity) {
		XxlJobLogger.log("-->"+entity.getId()+"数据主键ID:【{0}】  ",entity.getId());
		Timestamp time=JAppContext.currentTimestamp();
		entity.setCalculateTime(time);
		Map<String,Object> map=new HashMap<String,Object>();
		long start = System.currentTimeMillis();// 系统开始时间
		long current = 0L;// 当前系统时间
		String customerId=entity.getCustomerid();
		String subject=getSubjectId(entity.getChargeCarrierId());
		start = System.currentTimeMillis();

		feeEntity.setCalculateTime(time);
		
		if(StringUtils.isEmpty(subject)){
			XxlJobLogger.log("-->"+entity.getId()+String.format("运单号【%s】执行失败--原因：物流商【%s】未在数据字段中配置", entity.getWaybillNo(),entity.getChargeCarrierId()));
			entity.setIsCalculated(CalculateState.Sys_Error.getCode());
			feeEntity.setIsCalculated(CalculateState.Sys_Error.getCode());
			//entity.setRemark(String.format("运单号【%s】执行失败--原因：物流商【%s】未在数据字段中配置", entity.getWaybillNo(),entity.getChargeCarrierId()));
			entity.setRemark(entity.getRemark()+String.format("bms运单号【%s】执行失败--原因：物流商【%s】未在数据字段中配置", entity.getWaybillNo(),entity.getChargeCarrierId()));
			return false;
		}
		XxlJobLogger.log("-->"+entity.getId()+"费用科目编号:"+ subject);
		
		start = System.currentTimeMillis();// 操作开始时间
		//新增逻辑，若要按抛重计算，此时的泡重需要我们自己去计算运单中所有耗材中最大的体积/6000
		double newThrowWeight=getNewThrowWeight(entity);
		//将新泡重赋值
		//entity.setThrowWeight(newThrowWeight);
		entity.setCorrectThrowWeight(newThrowWeight);
		
		current = System.currentTimeMillis();
		XxlJobLogger.log("-->"+entity.getId()+"泡重获取  耗时【{0}】毫秒 ",(current - start));	
		
		start = System.currentTimeMillis();// 操作开始时间
		//先进行判断是否是不计算的运单
		//**********************************如果是不计算配送费用的宅配商,费用表照常写入，费用表中的计费重量置为空，实际重量为运单表中的但金额至0*****
	
		if(DoubleUtil.isBlank(entity.getWeight())){
			XxlJobLogger.log("-->"+entity.getId()+"--------未获取到重量 计算失败--------");
			entity.setIsCalculated(CalculateState.Sys_Error.getCode());
			feeEntity.setIsCalculated(CalculateState.Sys_Error.getCode());
			entity.setRemark(entity.getRemark()+"bms未获取到重量 计算失败");
			return false;
		}
			
		current = System.currentTimeMillis();
		XxlJobLogger.log("-->"+entity.getId()+"地址匹配   耗时【{0}】毫秒 ",(current - start));	
		
		//********************获合同编号********************//
		PriceContractInfoEntity contractEntity = null;
		if(mapContact.containsKey(customerId)){
			contractEntity=mapContact.get(customerId);
		}else{
			map.clear();
			map.put("customerid",customerId);
			map.put("contractTypeCode", contractTypeCode);
			contractEntity = jobPriceContractInfoService.queryContractByCustomer(map);
			if(contractEntity!=null){
				mapContact.put(customerId,contractEntity);
			}
		}
	
		if(contractEntity == null || StringUtils.isEmpty(contractEntity.getContractCode())){
			XxlJobLogger.log("-->"+entity.getId()+String.format("未查询到合同  运单号【%s】--商家【%s】", entity.getWaybillNo(),entity.getCustomerid()));
			entity.setIsCalculated(CalculateState.Contract_Miss.getCode());
			feeEntity.setIsCalculated(CalculateState.Contract_Miss.getCode());
			entity.setRemark(entity.getRemark()+String.format("bms未查询到合同  运单号【%s】--商家【%s】", entity.getWaybillNo(),entity.getCustomerid()));
			return false;
		}
		current = System.currentTimeMillis();
		XxlJobLogger.log("验证合同   耗时【{0}】毫秒  合同编号 【{1}】",(current - start),contractEntity.getContractCode());
		start = System.currentTimeMillis();// 系统开始时间
		
		//验证是否存在签约服务
		start = System.currentTimeMillis();// 操作开始时间
		map.clear();
		map.put("contractCode", contractEntity.getContractCode());
		map.put("bizTypeCode", BizTypeCode);
		map.put("subjectId", subject);
		List<PriceContractItemEntity> contractItemEntities = priceContractItemRepository.query(map);
	    if(contractItemEntities == null || contractItemEntities.size()==0){
	    	XxlJobLogger.log(String.format("-->"+entity.getId()+"合同【%s】未签约服务", contractEntity.getContractCode()));
			entity.setIsCalculated(CalculateState.Quote_Miss.getCode());
			feeEntity.setIsCalculated(CalculateState.Quote_Miss.getCode());
			entity.setRemark(entity.getRemark()+String.format("bms合同【%s】未签约服务", contractEntity.getContractCode()));
			return false;
	    }
	    current = System.currentTimeMillis();
		XxlJobLogger.log("-->"+entity.getId()+"验证签签约服务   耗时【{1}】毫秒 ",(current - start));	
		return true;
	}
	
	
	@Override
	public boolean isNoExe(BizDispatchBillEntity entity,FeesReceiveDispatchEntity feeEntity) {
		//如果初始化的时候已经更新计算状态了，则直接返回
		if(StringUtils.isNotBlank(feeEntity.getIsCalculated())){
			return true;
		}
		
		long start = System.currentTimeMillis();// 系统开始时间
		long current = 0L;// 当前系统时间
		String subjectId=getSubjectId(getCarrierId(entity));
		
		Map<String, Object> map = new HashMap<String, Object>();
		//*********************************判断不计算的宅配商*********************************
        boolean isExe = true; //默认计算
		for (SystemCodeEntity scEntity : no_fees_delivers) {
			if(scEntity.getExtattr1().equals(entity.getDeliverid())){
				isExe = false;//false-此单不参与计算费用
				entity.setRemark("该运单配送类型是【"+scEntity.getCodeName()+"】,金额置0");
				XxlJobLogger.log("-->"+entity.getId()+entity.getRemark());
				break;
			}
		}
		if(!isExe){
			feeEntity.setAmount(0.0d);
			feeEntity.setTotalWeight(getBizTotalWeight(entity));
			feeEntity.setChargedWeight(0d);
			entity.setIsCalculated(CalculateState.No_Exe.getCode());
			feeEntity.setIsCalculated(CalculateState.No_Exe.getCode());	
			feeEntity.setOtherSubjectCode(_subjectCode);
			feeEntity.setSubjectCode(_subjectCode);
			return true;
		}
	
		//********************************九曳自己的顺丰月结账号才计算费用*********************************
		if(StringUtils.isNotBlank(entity.getMonthFeeCount())){
			boolean ismouthCount= false;
			String feeCountKey=feeEntity.getCarrierid()+"&"+entity.getMonthFeeCount();//获取月结账号
			
			if(monthCountList.contains(feeCountKey)){
				ismouthCount = true;//false-此单参与计算费用
				XxlJobLogger.log("-->"+entity.getId()+entity.getRemark());
			}
			
			if(!ismouthCount){
				entity.setRemark("该运单不是九曳的月结账号，金额置0");
				feeEntity.setAmount(0.0d);
				feeEntity.setTotalWeight(getBizTotalWeight(entity));
				feeEntity.setChargedWeight(0d);
				entity.setIsCalculated(CalculateState.No_Exe.getCode());
				feeEntity.setIsCalculated(CalculateState.No_Exe.getCode());
				feeEntity.setOtherSubjectCode(_subjectCode);
				feeEntity.setSubjectCode(_subjectCode);
				return true;
			}
		}
		
		
		//=*******************************判断取消的单子是否继续计算*******************************
		//指定需要计算取消状态单子的商家
		if(StringUtils.isNotBlank(entity.getOrderStatus()) && "CLOSE".equals(entity.getOrderStatus())){
			if(cancelCusList.contains(entity.getCustomerid())){ // 在cancelCusList中存在，不计费
				entity.setRemark("该运单是不需要计算的商家取消运单，金额置0");
				feeEntity.setAmount(0.0d);
				feeEntity.setTotalWeight(getBizTotalWeight(entity));
				feeEntity.setChargedWeight(0d);
				entity.setIsCalculated(CalculateState.No_Exe.getCode());
				feeEntity.setIsCalculated(CalculateState.No_Exe.getCode());
				feeEntity.setOtherSubjectCode(_subjectCode);
				feeEntity.setSubjectCode(_subjectCode);
				return true;
			}
		}
		
		current = System.currentTimeMillis();
		XxlJobLogger.log("-->"+entity.getId()+"不计算判断  耗时【{0}】毫秒 ",(current - start));

		return false;
	}
	@Override
	public ContractQuoteInfoVo getContractForWhat(BizDispatchBillEntity entity) {
			
		ContractQuoteQueryInfoVo queryVo = new ContractQuoteQueryInfoVo();
		queryVo.setCustomerId(entity.getCustomerid());
		queryVo.setBizTypeCode(ContractBizTypeEnum.DISTRIBUTION.getCode());
		queryVo.setCurrentTime(entity.getCreateTime());
		queryVo.setWarehouseCode(entity.getWarehouseCode());
		queryVo.setCarrierId(entity.getCarrierId());
		queryVo.setSubjectCode("de_delivery_amount");
		queryVo.setCarrierServiceType(StringUtils.isBlank(entity.getAdjustServiceTypeCode())?entity.getServiceTypeCode():entity.getAdjustServiceTypeCode());
		
		ContractQuoteInfoVo modelEntity = new ContractQuoteInfoVo();
		XxlJobLogger.log("-->"+entity.getId()+"查询合同在线参数【{0}】",JSONObject.fromObject(queryVo));
			
		try{
			modelEntity = contractQuoteInfoService.queryUniqueColumns(queryVo);
			
			XxlJobLogger.log("-->"+entity.getId()+"查询出的合同在线结果【{0}】",JSONObject.fromObject(modelEntity));
		}
		catch(BizException ex){
			XxlJobLogger.log("-->"+entity.getId()+"合同在线无此合同:"+ex.getMessage());
			entity.setRemark("合同在线"+ex.getMessage()+";");
		}
		return modelEntity;
	}
	
	@Override
	public void calcuForBms(BizDispatchBillEntity entity,FeesReceiveDispatchEntity feeEntity){
		//合同报价校验  false-不通过  true-通过
		XxlJobLogger.log("-->"+entity.getId()+"bms计算");
		try{
			if(validateData(entity, feeEntity)){
				long start = System.currentTimeMillis();// 系统开始时间
				long current = 0L;// 当前系统时间
				entity.setCalculateTime(JAppContext.currentTimestamp());
				feeEntity.setCalculateTime(entity.getCalculateTime());
				String subjectId=getSubjectId(entity.getChargeCarrierId());
				XxlJobLogger.log("-->"+entity.getId()+"计算时获取到的计费物流商-- 【{0}】",entity.getChargeCarrierId());
		
				BmsQuoteDispatchDetailVo price=new BmsQuoteDispatchDetailVo();
				
				//获取报价
				List<BmsQuoteDispatchDetailVo> priceList=getQuoEntites(entity,subjectId);		
				if(priceList == null || priceList.size()==0){ //
					XxlJobLogger.log("-->"+entity.getId()+"商家【{0}】,仓库【{1}】,省份【{2}】报价未配置",entity.getCustomerid(),entity.getWarehouseName(),entity.getReceiveProvinceId());
					entity.setRemark(String.format("商家【%s】,仓库【%s】,省份【%s】报价未配置",entity.getCustomerid(),entity.getWarehouseName(),entity.getReceiveProvinceId()));			
					entity.setIsCalculated(CalculateState.Quote_Miss.getCode());
					feeEntity.setIsCalculated(CalculateState.Quote_Miss.getCode());
					return;
				}
				
				if(priceList.size()>1){ //			
					XxlJobLogger.log("-->"+entity.getId()+"商家【{0}】,仓库【{1}】,省份【{2}】报价未配置",entity.getCustomerid(),entity.getWarehouseName(),entity.getReceiveProvinceId());
					entity.setRemark(String.format("商家【%s】,仓库【%s】,省份【%s】报价存在多条",entity.getCustomerid(),entity.getWarehouseName(),entity.getReceiveProvinceId()));				
					entity.setIsCalculated(CalculateState.Quote_Miss.getCode());
					feeEntity.setIsCalculated(CalculateState.Quote_Miss.getCode());
					return;
				}
				
				if(priceList.size()==1){
					price=priceList.get(0);
				}
				
				CalcuReqVo<BmsQuoteDispatchDetailVo> reqVo = new CalcuReqVo<BmsQuoteDispatchDetailVo>();
				reqVo.setBizData(entity);
				
			/*	handBizDispatch(priceList,entity);*/
				current = System.currentTimeMillis();
				XxlJobLogger.log("-->"+entity.getId()+"验证报价耗时：【{0}】毫秒  ",(current - start));
				start = System.currentTimeMillis();// 系统开始时间
			
				//开始进行计算
				if(!DoubleUtil.isBlank(price.getUnitPrice())){
					feeEntity.setAmount(price.getUnitPrice());
					feeEntity.setParam2(price.getUnitPrice()+"");
				}else{
					feeEntity.setAmount(price.getFirstWeight()<entity.getWeight()?price.getFirstWeightPrice()+price.getContinuedPrice()*((entity.getWeight()-price.getFirstWeight())/price.getContinuedWeight()):price.getFirstWeightPrice());	        
					if(price.getFirstWeight()<entity.getWeight()){
						feeEntity.setParam2(price.getFirstWeightPrice()+"+"+price.getContinuedPrice()+"*(("+((entity.getWeight()+"-"+price.getFirstWeight())+")/"+price.getContinuedWeight())+")");
					}else{
						feeEntity.setParam2(price.getFirstWeightPrice()+"");
					}
				}
				feeEntity.setWeightLimit(price.getWeightLimit());   	  //重量界限
				feeEntity.setUnitPrice(price.getUnitPrice());			  //单价
				feeEntity.setHeadWeight(price.getFirstWeight());    	  //首重
				feeEntity.setHeadPrice(price.getFirstWeightPrice());	  //首重价格
				feeEntity.setContinuedWeight(price.getContinuedWeight()); //续重
				feeEntity.setContinuedPrice(price.getContinuedPrice());   //续重价格	
				feeEntity.setPriceId(price.getId()+"");
				feeEntity.setBizType(entity.getExtattr1());//判断是否是遗漏数据
				feeEntity.setServiceTypeCode(StringUtils.isEmpty(entity.getAdjustServiceTypeCode())?entity.getServiceTypeCode():entity.getAdjustServiceTypeCode());
				feeEntity.setIsCalculated(CalculateState.Finish.getCode());
				entity.setIsCalculated(CalculateState.Finish.getCode());
				entity.setRemark("计算成功");
				XxlJobLogger.log("-->"+entity.getId()+"调用规则引擎   耗时【{0}】毫秒  费用【{1}】 ",(current - start),feeEntity.getAmount());	
			}
		}
		catch(Exception ex){
			feeEntity.setIsCalculated(CalculateState.Sys_Error.getCode());
			entity.setIsCalculated(CalculateState.Sys_Error.getCode());
			XxlJobLogger.log("-->"+entity.getId()+"系统异常，费用【0】",ex);
		}
	}
	
	public void calcuForContract(BizDispatchBillEntity entity,FeesReceiveDispatchEntity feeEntity,ContractQuoteInfoVo contractQuoteInfoVo){
		XxlJobLogger.log("-->"+entity.getId()+"合同在线计算");
		XxlJobLogger.log("-->"+entity.getId()+"计算总重量为"+feeEntity.getChargedWeight());
		try{
			Map<String, Object> con = new HashMap<String, Object>();
			con.put("quotationNo", contractQuoteInfoVo.getRuleCode().trim());
			BillRuleReceiveEntity ruleEntity = receiveRuleRepository.queryOne(con);
			if (null == ruleEntity) {
				entity.setRemark("合同在线规则未配置");
				feeEntity.setIsCalculated(CalculateState.Quote_Miss.getCode());
				entity.setIsCalculated(CalculateState.Quote_Miss.getCode());
				XxlJobLogger.log("-->"+entity.getId()+"计算不成功，合同在线规则未配置");
			}
			//获取合同在线查询条件
			Map<String, Object> cond = new HashMap<String, Object>();
			feesCalcuService.ContractCalcuService(entity, cond, ruleEntity.getRule(), ruleEntity.getQuotationNo());
			XxlJobLogger.log("-->"+entity.getId()+"获取报价参数"+cond);
			
			ContractQuoteInfoVo rtnQuoteInfoVo=null;
			try {
				rtnQuoteInfoVo = contractQuoteInfoService.queryQuotes(contractQuoteInfoVo, cond);
			} catch (BizException e) {
				// TODO: handle exception
				feeEntity.setIsCalculated(CalculateState.Quote_Miss.getCode());
				entity.setIsCalculated(CalculateState.Quote_Miss.getCode());
				XxlJobLogger.log("-->"+entity.getId()+"获取合同在线报价异常:"+e.getMessage());
				entity.setRemark("获取合同在线报价异常:"+e.getMessage());
				return;
			}
			
			XxlJobLogger.log("获取合同在线报价结果【{0}】",JSONObject.fromObject(rtnQuoteInfoVo));
			
			for (Map<String, String> map : rtnQuoteInfoVo.getQuoteMaps()) {
				XxlJobLogger.log("-->"+entity.getId()+"报价信息 -- "+map);
			}
			//调用规则计算费用
			Map<String, Object> feesMap = feesCalcuService.ContractCalcuService(feeEntity, rtnQuoteInfoVo.getQuoteMaps(), ruleEntity.getRule(), ruleEntity.getQuotationNo());
			
			if(feeEntity.getAmount()>0){
				feeEntity.setIsCalculated(CalculateState.Finish.getCode());
				entity.setIsCalculated(CalculateState.Finish.getCode());
				entity.setRemark("计算成功");
				XxlJobLogger.log("-->"+entity.getId()+"计算成功，费用【{0}】",feeEntity.getAmount());
			}
			else{
				feeEntity.setIsCalculated(CalculateState.Quote_Miss.getCode());
				entity.setIsCalculated(CalculateState.Quote_Miss.getCode());
				XxlJobLogger.log("-->"+entity.getId()+"计算不成功，费用【0】");
				entity.setRemark("计算不成功，费用【0】");
			}
		}
		catch(Exception ex){
			feeEntity.setIsCalculated(CalculateState.Sys_Error.getCode());
			entity.setIsCalculated(CalculateState.Sys_Error.getCode());
			XxlJobLogger.log("-->"+entity.getId()+"计算不成功，费用【0】{0}",ex.getMessage());
			entity.setRemark("计算不成功:"+ex.getMessage());
		}
		
	}
	
	
	/**
	 * 顺丰以外物流商的报价
	 * @param entity
	 * @return
	 */
	private List<BmsQuoteDispatchDetailVo> queryPriceByCustomer(BizDispatchBillEntity entity,String subjectId) {
		PriceContractInfoEntity contractEntity=mapContact.get(entity.getCustomerid());
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("contractCode",contractEntity.getContractCode());
		map.put("subjectId",subjectId);
		map.put("wareHouseId", entity.getWarehouseCode());
		map.put("province", entity.getReceiveProvinceId());
		map.put("weight", entity.getWeight());
		List<BmsQuoteDispatchDetailVo> price = jobPriceContractInfoService.queryAllByTemplateId(map);
		if(price==null || price.size() ==0){
			XxlJobLogger.log("-->"+entity.getId()+"数据库未查询到报价 查询条件{0}",map);
		}
		else{
			XxlJobLogger.log("-->"+entity.getId()+"报价条数【{0}】 查询条件{1}",price.size(),map);
		}
		return price;
	}
	
	/**
	 * 顺丰对应的报价
	 * @return
	 */
	private List<BmsQuoteDispatchDetailVo> queryShunfengPrice(BizDispatchBillEntity entity,String subjectId,String customerId){
		//获取顺丰标准商家
		//指定的商家
		try {
			Map<String, Object> map= new HashMap<String, Object>();
			if(StringUtils.isNotBlank(customerId)){
				map.put("customerId", customerId);
				map.put("subjectId", subjectId);
				map.put("wareHouseId", entity.getWarehouseCode());
				map.put("province", entity.getReceiveProvinceId());
				map.put("weight", entity.getWeight());
				List<BmsQuoteDispatchDetailVo> price =jobPriceContractInfoService.queryShunfengDispatch(map);
				return price;
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * 获取报价
	 * @param entity
	 * @param subjectId
	 * @return
	 */
	private List<BmsQuoteDispatchDetailVo> getQuoEntites(BizDispatchBillEntity entity,String subjectId){
		//查询报价
		List<BmsQuoteDispatchDetailVo> list=queryPriceByCustomer(entity,subjectId);

		//如果有多条,走筛选规则
		if(list.size()>0){
			//1.走物流产品类型筛选  2.走温度筛选 3.走地址筛选
			XxlJobLogger.log("-->"+entity.getId()+"走筛选 筛选前报价条数【{0}】",list.size());
			list=handNewBizDispatch(list, entity);
			if(null==list||list.size()==0){
				return list;
			}
			XxlJobLogger.log("-->"+entity.getId()+"筛选后报价条数【{0}】",list.size());
			for (BmsQuoteDispatchDetailVo bmsQuoteDispatchDetailVo : list) {
				XxlJobLogger.log("-->"+entity.getId()+"筛选后报价明细【{0}】",JSONObject.fromObject(bmsQuoteDispatchDetailVo));
			}		
			return list;
		}	
		return list;
	}
	
	@Override
	public void updateBatch(List<BizDispatchBillEntity> billList,List<FeesReceiveDispatchEntity> feesList) {
		try {
			long start = System.currentTimeMillis();// 系统开始时间
			long current = 0L;// 当前系统时间
			bizDispatchBillService.newUpdateBatch(billList);
			current = System.currentTimeMillis();
			XxlJobLogger.log("更新业务数据耗时：【{0}】毫秒  ",(current - start));
			start = System.currentTimeMillis();// 系统开始时间
			feesReceiveDispatchService.InsertBatch(feesList);
			current = System.currentTimeMillis();
			XxlJobLogger.log("更新费用数据耗时：【{0}】毫秒 ",(current - start));
		} catch (Exception e) {
			// TODO: handle exception
			XxlJobLogger.log("-->批量保存异常"+e.getMessage());
		}
		
	}

	

	/*public double getNewTotalWeight(Double originWeight){
			
		//1、宅配重量如果前2位小数是0,存在第三位小数的，直接抹掉；
	    //   比如:3.0056, 那么变成 3
		//2、对于第一位小数位是0的，第二位不是0,在原重量上加0.1；
	    //   比如3.015, 那么变成3.115
		//	   比如3.01，    那么变成3.11
		double totalWeight=0d;
		if(!DoubleUtil.isBlank(originWeight)){
			String weightString=originWeight+"";
			if(weightString.contains(".0")){
				String value=weightString.substring(weightString.indexOf("."));
				if(value.length()>2){
					String v=value.substring(value.indexOf(".")+2,value.indexOf(".")+3);
					Double val=Double.valueOf(v);
					if(val==0){
						totalWeight=Math.floor(originWeight);
					}else{
						BigDecimal   a1   =BigDecimal.valueOf(originWeight);
						BigDecimal   a2  =new BigDecimal(0.1);
						totalWeight=a1.add(a2).doubleValue();
					}
				}else{
					totalWeight=originWeight;
				}		
			}else{
				totalWeight=originWeight;
			}	
		}
		return totalWeight;	
		
	}*/
	
	public double getNewThrowWeight(BizDispatchBillEntity entity){
		double throwWeight=0d;
		try{
			//验证是否存在签约服务
			Map<String,Object> condition=new HashMap<String,Object>();
			condition.put("waybillNo", entity.getWaybillNo());
			Double volumn=bizOutstockPackmaterialRepository.getMaxVolumByMap(condition);
			if(!DoubleUtil.isBlank(volumn)){
				throwWeight=(double)volumn/6000;
				throwWeight=(double)Math.round(throwWeight*100)/100;
			}			
		}catch(Exception ex){ 
			XxlJobLogger.log("-->"+entity.getId()+"获取泡重失败:{1}",ex.getMessage());
		}
		return throwWeight;
	}
	
	/**
	 * 若 进位重量 = 纠正重量，则返回 运单重量
	 * 若 进位重量 != 纠正重量，则返回纠正重量
	 * @param waybillWeight	运单重量
	 * @param carryWeight	进位重量
	 * @param corrWeight	纠正重量
	 * @return
	 */
	private double compareWeight(double waybillWeight, double carryWeight, double corrWeight){
		if (DoubleUtil.isBlank(waybillWeight)) {
			// 如果运单重量没有，当做0处理
			waybillWeight = 0.0;
		}
		return carryWeight == corrWeight ? waybillWeight : corrWeight;
	}
	

	/**
	 * 获取计算的物流商id
	 * @param carrierId
	 * @return
	 */
	 private String getCarrierId(BizDispatchBillEntity entity){
		 	String carrierId="";
			//物流商的判断
			//1.有调整物流商时直接按调整物流商计算
			//2.无调整物流商时判断是否是指定的商家  获取 此时的实际物流商是否为顺丰，如果是顺丰则需去物流商调整查询该运单，判断其修改的原因
			//根据责任方判断计费物流商:商家的责任就按实际物流商顺丰计费;我们的责任就按订单物流商九曳计费
			//不为顺丰的继续按实际物流商计算
		 	if(StringUtils.isBlank(entity.getAdjustCarrierId())){
		 		//调整物流商为空时 判断实际物流商是否是顺丰
		 		if("1500000015".equals(entity.getCarrierId()) && changeCusList.size()>0 && changeCusList.contains(entity.getCustomerid())){
		 			//判断是否是指定的商家
 					//根据运单号和物流商顺丰去物流商修改表里查询
 					Map<String, Object> param=new HashMap<String, Object>();
 					param.put("carrierId", entity.getCarrierId());
 					param.put("waybillNo", entity.getWaybillNo());	
 					param.put("warehouseCode", entity.getWarehouseCode());
 					param.put("customerid", entity.getCustomerid());
 					BizDispatchCarrierChangeEntity changeEntity=bizDispatchBillService.queryCarrierChangeEntity(param);
 					if(changeEntity!=null){
 						//判断其修改的原因
 						if("customer_reason".equals(changeEntity.getUpdateReasonTypeCode())){
 							//商家原因 按实际物流商顺丰计费
 							carrierId=entity.getCarrierId();
 							changeEntity.setDutyType("商家原因");
 						}else{							
 							//获取商家仓库对应的省配送范围
 							List<String> proviceList=getProviceList(entity);	
 							//在九曳配送范围内的按九曳
 							if(proviceList.contains(entity.getReceiveProvinceId())){
 								//配送范围内的按原始物流商算，且更新为商家原因
 	 							carrierId=changeEntity.getOldCarrierId();
 	 							changeEntity.setDutyType("我司原因");
 							}else{
 								//不在九曳范围内的按顺丰
 								carrierId=entity.getCarrierId();					
 								//第一次为空时保存原修改原因，后面不修改
 								if(StringUtils.isBlank(changeEntity.getOldUpdateReason())){
 	 								changeEntity.setOldUpdateReason(changeEntity.getUpdateReasonTypeCode()+","+changeEntity.getUpdateReasonType()+","+changeEntity.getUpdateReasonDetailCode()+","+changeEntity.getUpdateReasonDetail());
 								}
 								//修改原因
 	 							changeEntity.setDutyType("商家原因");
 	 							changeEntity.setUpdateReasonTypeCode("customer_reason");
 	 							changeEntity.setUpdateReasonType("商家原因");
 	 							changeEntity.setUpdateReasonDetailCode("order_out_of_range");
 	 							changeEntity.setUpdateReasonDetail("订单地址超配送范围"); 							
 							}							
 						}	
 						//更新责任原因至物流商修改表
 						bizDispatchBillService.updateCarrierChange(changeEntity);	
 						
 						//修改原始物流商
 						entity.setOriginCarrierId("1500000016");
 						entity.setOriginCarrierName("JY");
 					}else{
 						carrierId=entity.getCarrierId();
 					}	 		
		 		}else{
		 			carrierId=entity.getCarrierId();
		 		}
		 		
			}else{
				carrierId=entity.getAdjustCarrierId();
			}
		 	
		 	//chargeCarrierId=carrierId;
		 	entity.setChargeCarrierId(carrierId);
		 	XxlJobLogger.log("-->"+entity.getId()+"获取到计费物流商-- 【{0}】 ",entity.getChargeCarrierId());
		 	return entity.getChargeCarrierId();
		}
	 
	 private List<String> getProviceList(BizDispatchBillEntity entity){
		 
		 	List<String> reList=new ArrayList<String>();	 	

		    Map<String, Object> map= new HashMap<String, Object>();
			map.put("groupCode", "bms_jy_area");
			map.put("bizType", "group_customer");
			BmsGroupVo bmsGroup=bmsGroupService.queryOne(map);
			if(bmsGroup!=null){			
				List<BmsGroupCustomerVo> list;
				try {
					list = bmsGroupCustomerService.queryAllByGroupId(bmsGroup.getId());
					if(list.size()>0){
						BmsGroupCustomerVo vo=list.get(0);
						if(vo!=null){
							//查询九曳配送范围
							map.put("customerId", vo.getCustomerid());
							map.put("subjectId", "JIUYE_DISPATCH");
							map.put("wareHouseId", entity.getWarehouseCode());
							reList =iPriceDispatchDao.queryJiuYeArea(map);
						}
					}			
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		    }	
			return reList;
	 }
	 
	 /**
	  * 根据物流商id获取到物流商名字
	  * @param carrierId
	  * @return
	  */
 	private String getSubjectId(String carrierId){
		String subjectID="";
		for (SystemCodeEntity scEntity : scList) {
			if(scEntity.getExtattr1().equals(carrierId)){
				subjectID = scEntity.getCode();
				break;
			}
		}
		return subjectID;
	}
	
 	public double getBizTotalWeight(BizDispatchBillEntity entity){	
 		
 		double totalWeight=0;
 		
		if(!DoubleUtil.isBlank(entity.getAdjustWeight())){
			totalWeight=entity.getAdjustWeight(); //实际重量 eg:5.1
		}
		else{
			//新增逻辑判断，判断是否有修正重量，用运单号去修正表里去查询
			Map<String,Object> condition=new HashMap<String,Object>();
			condition.put("waybillNo", entity.getWaybillNo());
			BmsMarkingProductsEntity markEntity=bmsProductsWeightRepository.queryMarkVo(condition);
			if(markEntity!=null && markEntity.getCorrectWeight()!=null && !DoubleUtil.isBlank(markEntity.getCorrectWeight().doubleValue())){
				double weight=markEntity.getCorrectWeight().doubleValue();
				// 比较重量
				double resultWeight = compareWeight(entity.getTotalWeight(), 
						getResult(entity.getTotalWeight(),entity.getChargeCarrierId()), weight);
				totalWeight=resultWeight;//实际重量 eg:5.1
			}else{
				totalWeight=entity.getTotalWeight();//实际重量 eg:5.1
			}

		}
		
		return totalWeight;
 	}
 	
 	
 	/**
	 * 获取顺丰标准报价对应的商家id
	 * @return
	 */
	private String queryShunfengCustomer(){
		//获取顺丰标准商家
		//指定的商家
		try {
			Map<String, Object> map= new HashMap<String, Object>();
			map.put("groupCode", "shunfeng_biaozhun");
			map.put("bizType", "group_customer");
			BmsGroupVo bmsGroup=bmsGroupService.queryOne(map);
			if(bmsGroup!=null){			
				List<BmsGroupCustomerVo> list=bmsGroupCustomerService.queryAllByGroupId(bmsGroup.getId());
				if(list.size()>0){
					BmsGroupCustomerVo vo=list.get(0);
					if(vo!=null){				
						return vo.getCustomerid();				
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
 	
	private String ReplaceChar(String str){
		if(StringUtils.isNotBlank(str)){
			String[] arr={" ","\\","/",",",".","-"};
			for(String a:arr){
				str=str.replace(a, "");
			}
		}
		return str;
	}
	
	
	public void handBizDispatch(List<BmsQuoteDispatchDetailVo> priceList,BizDispatchBillEntity bizEntity){
		//判断该地址是否存在于报价中，为空时则为空
		//获取此时业务数据的市区
		String bizCity=bizEntity.getReceiveCityId();
		String bizArea=bizEntity.getReceiveDistrictId();
		
		boolean city=false;
		boolean area=false;
		for(BmsQuoteDispatchDetailVo mainDispatchEntity : priceList){				
			
			//获取报价此时的市ID
			String dispatchCityId=mainDispatchEntity.getCityId();
			//获取报价此时的区ID
			String dispatchAreaId=mainDispatchEntity.getAreaId();

			//业务数据市县区都不为空情况
			if(StringUtils.isNotBlank(bizCity) && StringUtils.isNotBlank(bizArea)){
				//取此时报价中市区不为空的情况
				if(StringUtils.isNotBlank(dispatchCityId) && StringUtils.isNotBlank(dispatchAreaId)){
					//此时先判断区
					if(dispatchAreaId.trim().equals(bizArea) && dispatchCityId.trim().equals(bizCity)){
						area=true;	
						city=true;
						break;					
					}
				}
				
				//再判断市
				//此时取出报价区不为空的
				if(StringUtils.isNotBlank(dispatchCityId) && StringUtils.isBlank(dispatchAreaId)){
					if(dispatchCityId.trim().equals(bizCity)){
						city=true;
					}
				}
			}
								
			//业务数据中市不为空，县为空的情况
			if(StringUtils.isNotBlank(bizCity) && StringUtils.isBlank(bizArea)){
				//此时取报价中市不为空，县为空的情况
				if(StringUtils.isNotBlank(dispatchCityId) && StringUtils.isBlank(dispatchAreaId)){
					if(dispatchCityId.trim().equals(bizEntity.getReceiveCityId().trim())){
						city=true;
						break;
					}
				}
			}
		}
		
		if(city==false){
			bizEntity.setReceiveCityId("");
		}
		if(area==false){
			bizEntity.setReceiveDistrictId("");
		}
	}
	
	/**
	 * 根据省市区获取报价
	 * @param priceList
	 * @param bizEntity
	 * @return
	 */
	public List<BmsQuoteDispatchDetailVo> handNewBizDispatch(List<BmsQuoteDispatchDetailVo> priceList,BizDispatchBillEntity bizEntity){
		
		List<BmsQuoteDispatchDetailVo> list=new ArrayList<BmsQuoteDispatchDetailVo>();
		
		//获取此时业务数据的市区
		String bizCity=bizEntity.getReceiveCityId();
		String bizArea=bizEntity.getReceiveDistrictId();
		
		//物流产品类型
		String bizServiceCode=StringUtils.isNotBlank(bizEntity.getAdjustServiceTypeCode())?bizEntity.getAdjustServiceTypeCode():bizEntity.getServiceTypeCode();
		bizServiceCode=StringUtils.isNotBlank(bizServiceCode)?bizServiceCode:"";
		
		//温度
		String bizTemperatureCode = StringUtil.isEmpty(bizEntity.getTemperatureTypeCode())?"":bizEntity.getTemperatureTypeCode();
		Map<Long,BmsQuoteDispatchDetailVo> map=new HashMap<>();
		Map<Integer,String> newPrice=new HashMap<Integer,String>();
		
		//报价形式
		//匹配上          1
		//空值             2
		//匹配不上      3
		for(BmsQuoteDispatchDetailVo mainDispatchEntity : priceList){				
			map.put(mainDispatchEntity.getId(), mainDispatchEntity);
			String biaoshi="";
			
			//物流产品类型
			String dispatchServiceCode=mainDispatchEntity.getServiceTypeCode();
			//温度类型
			String dispatchTemetureCode=mainDispatchEntity.getTemperatureTypeCode();
			//获取报价此时的市ID
			String dispatchCityId=mainDispatchEntity.getCityId();
			//获取报价此时的区ID
			String dispatchAreaId=mainDispatchEntity.getAreaId();
			//判断物流产品类型
			if(StringUtils.isNotBlank(dispatchServiceCode)){
				if(dispatchServiceCode.equals(bizServiceCode)){
					biaoshi+="1";
				}else{
					biaoshi+="3";
				}
			}else{
				biaoshi+="2";
			}
			
			//判断区
			if(StringUtils.isNotBlank(dispatchAreaId)){
				if(dispatchAreaId.equals(bizArea)){
					biaoshi+="1";
				}else{
					biaoshi+="3";
				}
			}else{
				biaoshi+="2";
			}
			
			
			//判断市
			if(StringUtils.isNotBlank(dispatchCityId)){
				if(dispatchCityId.equals(bizCity)){
					biaoshi+="1";
				}else{
					biaoshi+="3";
				}
			}else{
				biaoshi+="2";
			}
			
			//判断温度
			if(StringUtils.isNotBlank(dispatchTemetureCode)){
				if(dispatchTemetureCode.equals(bizTemperatureCode)){
					biaoshi+="1";
				}else{
					biaoshi+="3"; 
				}
			}else{
				biaoshi+="2";
			}
			
			if(!biaoshi.contains("3")){
				int b=Integer.parseInt(biaoshi);
				if(newPrice.containsKey(b)){
					newPrice.put(b, newPrice.get(b)+","+mainDispatchEntity.getId());
				}else{
					newPrice.put(b, mainDispatchEntity.getId().toString());
				}	
			}
		}
		
		Integer minValue=new Integer(0);
		for(Integer num:newPrice.keySet()){
			if(minValue==0 || num<minValue){
				minValue=num;
			}	
		}
		
		BmsQuoteDispatchDetailVo vo=new BmsQuoteDispatchDetailVo();
		
		if(!(minValue+"").contains("3")){
			String result=newPrice.get(minValue);
			if(StringUtils.isEmpty(result)){
				return null;
			}
			if(result.contains(",")){
				String[] array=result.split(",");
				for (int a = 0; a < array.length; a++) {		
					Long value=Long.valueOf(array[a]);
					vo=map.get(value);
					list.add(vo);				
			     }		
			}else{
				Long value=Long.valueOf(result);
				vo=map.get(value);
				list.add(vo);			
			}
		}
		return list;	
	}
	
	/**
	 * 根据物流商判断进位规则
	 * 1.除顺丰外 1.1kg或者1.6kg，重量进位为2
	 * 2.顺丰运费计算规则（超重1.4kg时 重量进位为1.5, ;超重1.6kg时 重量进位为*2计算）
	 * @param weight
	 * @return
	 */
	public double getResult(double weight,String carrierId){
		//原重
		double a=weight;
		//现重
		double c=0.0;
		
		if("1500000015".equals(carrierId)){
			//顺丰				
			int b=(int)a;
			double min=a-b;

			if(0<min && min <0.5){
				c=b+0.5;
			}
			
			if(0.5<min && min<1){
				c=b+1;
			}
			if(min==0 || min==0.5){
				c=a;
			}
		}else{
			//除顺丰外
			c=Math.ceil(a);
		}
			
		return c;
	}

	/**
	 * 获取物流商的id和名称
	 * @return
	 */
	public Map<String, String> getCarrier(){
		Map<String, String> map=new HashMap<String, String>();
		//获取所有的物流商信息
		List<CarrierVo> carrierList=carrierService.queryAllCarrier();
		for(CarrierVo vo:carrierList){
			map.put(vo.getCarrierid(), vo.getName());
		}
		
		return map;
	}
	
	/**
	 * 获取耗材对应的外体积
	 * @return
	 */
	public Map<String,BigDecimal> getMetrialVolum(){
		Map<String, Object> condition=new HashMap<String, Object>();

		//获取所有的耗材
		Map<String,BigDecimal> mMap=new HashMap<String,BigDecimal>();
		List<PubMaterialInfoVo> materialList=pubMaterialInfoService.queryList(condition);
		for(PubMaterialInfoVo vo:materialList){
			mMap.put(vo.getBarcode(), vo.getOutVolume());
		}
		
		return mMap;
	}
	
	/**
	 * 报价帅选
	 * @param entity 业务数据
	 * @param list   报价列表
	 * @param priorities 优先级
	 * @return
	 */
	public String QuoteFilter(BizDispatchBillEntity entity,List<BmsQuoteDispatchDetailVo> list,List<String> priorities){
		
		if(list==null || list.size() == 0){
			return null;
		}

		Integer level = 33;
		
		String id="";
		
		String temperature_code = StringUtil.isEmpty(entity.getTemperatureTypeCode())?"":entity.getTemperatureTypeCode();
		String service_type_code = StringUtil.isEmpty(entity.getServiceTypeCode())?"":entity.getServiceTypeCode();
		String adjust_service_type_code = StringUtils.isEmpty(entity.getAdjustServiceTypeCode())?service_type_code:entity.getAdjustServiceTypeCode();
		for (BmsQuoteDispatchDetailVo vo : list) {
			//=====================================温度判断=================================
			String temperature_quote = StringUtil.isEmpty(vo.getTemperatureTypeCode())?"":vo.getTemperatureTypeCode();
			String service_type_quote = StringUtil.isEmpty(vo.getServiceTypeCode())?"":vo.getServiceTypeCode();
			
			if(!temperature_code.equals(temperature_quote) && StringUtils.isNotEmpty(temperature_quote)){
				continue;//温度不匹配
			}
			if(!adjust_service_type_code.equals(service_type_quote) && StringUtils.isNotEmpty(service_type_quote)){
				continue;//仓库不匹配
			}
			Integer temperaturelevel = temperature_code.equals(temperature_quote)?1:2; //温度优先级
			Integer serviceTypelevel = adjust_service_type_code.equals(service_type_quote)?1:2;		//仓库优先级
			
			Integer temLevel = Integer.valueOf(temperaturelevel.toString()+serviceTypelevel.toString());
			if(temLevel<level){
				level = temLevel;
				id = vo.getId()+"";
			}
		}
		
		if(level == 33){
			return null;
		}
		else{
			return id;
		}
	}

}