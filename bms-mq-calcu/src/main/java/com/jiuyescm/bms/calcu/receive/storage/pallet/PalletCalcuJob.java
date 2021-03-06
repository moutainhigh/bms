package com.jiuyescm.bms.calcu.receive.storage.pallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.jiuyescm.bms.asyn.service.IBmsCalcuTaskService;
import com.jiuyescm.bms.asyn.vo.BmsCalcuTaskVo;
import com.jiuyescm.bms.base.group.service.IBmsGroupCustomerService;
import com.jiuyescm.bms.base.group.service.IBmsGroupService;
import com.jiuyescm.bms.base.group.service.IBmsGroupSubjectService;
import com.jiuyescm.bms.base.group.vo.BmsGroupVo;
import com.jiuyescm.bms.calcu.base.ICalcuService;
import com.jiuyescm.bms.calcu.receive.BmsContractBase;
import com.jiuyescm.bms.calcu.receive.CommonService;
import com.jiuyescm.bms.calcu.receive.ContractCalcuService;
import com.jiuyescm.bms.calculate.api.IBmsCalcuService;
import com.jiuyescm.bms.calculate.vo.BmsFeesQtyVo;
import com.jiuyescm.bms.calculate.vo.CalcuContractVo;
import com.jiuyescm.bms.common.enumtype.CalculateState;
import com.jiuyescm.bms.common.enumtype.TemplateTypeEnum;
import com.jiuyescm.bms.drools.IFeesCalcuService;
import com.jiuyescm.bms.general.entity.BizPalletInfoEntity;
import com.jiuyescm.bms.general.entity.FeesReceiveStorageEntity;
import com.jiuyescm.bms.general.service.IBizPalletInfoRepository;
import com.jiuyescm.bms.general.service.IFeesReceiveStorageService;
import com.jiuyescm.bms.general.service.IPriceContractInfoService;
import com.jiuyescm.bms.general.service.IStorageQuoteFilterService;
import com.jiuyescm.bms.general.service.ISystemCodeService;
import com.jiuyescm.bms.quotation.contract.repository.imp.IPriceContractItemRepository;
import com.jiuyescm.bms.quotation.storage.entity.PriceGeneralQuotationEntity;
import com.jiuyescm.bms.quotation.storage.entity.PriceStepQuotationEntity;
import com.jiuyescm.bms.quotation.storage.repository.IPriceGeneralQuotationRepository;
import com.jiuyescm.bms.quotation.storage.repository.IPriceStepQuotationRepository;
import com.jiuyescm.bms.rule.receiveRule.repository.IReceiveRuleRepository;
import com.jiuyescm.cfm.common.JAppContext;
import com.jiuyescm.common.utils.DoubleUtil;
import com.jiuyescm.contract.quote.api.IContractQuoteInfoService;
import com.jiuyescm.contract.quote.vo.ContractBizTypeEnum;
import com.jiuyescm.contract.quote.vo.ContractQuoteQueryInfoVo;

@Component("palletCalcuJob")
@Scope("prototype")
public class PalletCalcuJob extends BmsContractBase implements ICalcuService<BizPalletInfoEntity,FeesReceiveStorageEntity> {

	private Logger logger = LoggerFactory.getLogger(PalletCalcuJob.class);
	
	@Autowired private IPriceContractInfoService jobPriceContractInfoService;
	@Autowired private IFeesReceiveStorageService feesReceiveStorageService;
	@Autowired private IPriceGeneralQuotationRepository priceGeneralQuotationRepository;
	@Autowired private IPriceStepQuotationRepository  repository;
	@Autowired private IReceiveRuleRepository receiveRuleRepository;
	@Autowired private IFeesCalcuService feesCalcuService;
	@Autowired private IPriceContractItemRepository priceContractItemRepository;
	@Autowired private ISystemCodeService systemCodeService;
	@Autowired private IBmsGroupService bmsGroupService;
	@Autowired private IBmsGroupCustomerService bmsGroupCustomerService;
	@Autowired private IStorageQuoteFilterService storageQuoteFilterService;
	@Autowired private IContractQuoteInfoService contractQuoteInfoService;
	@Autowired private IBmsGroupSubjectService bmsGroupSubjectService;
	@Autowired private IBizPalletInfoRepository bizPalletInfoService;
	@Autowired private ContractCalcuService contractCalcuService;
	@Autowired private CommonService commonService;
	@Autowired private IBmsCalcuTaskService bmsCalcuTaskService;
	@Autowired private IBmsCalcuService bmsCalcuService;
	

	
	private PriceGeneralQuotationEntity quoTemplete = null;
	private Map<String, Object> errorMap = null;
/*	private List<String> cusList=null;         
	private List<String> cusNames = null;            //使用导入商品托数的商家
	private List<String> materialCusNames=null;      //使用导入耗材托数的商家
	private List<String> instockCusNames=null;       //使用导入入库托数的商家
	private List<String> outstockCusNames=null;       //使用导入出库托数的商家
*/	private List<BizPalletInfoEntity> bizList=null;
	private List<FeesReceiveStorageEntity> fees=null;

	public void process(BmsCalcuTaskVo taskVo,String contractAttr){
		super.process(taskVo, contractAttr);
		serviceSubjectCode = subjectCode;
		getQuoTemplete();
		initConf();
	}
	
	@Override
	public void getQuoTemplete() {
		/*Map<String, Object> map = new HashMap<>();
		if(quoTempleteCode!=null){
			map.put("subjectId",serviceSubjectCode);
			map.put("quotationNo", quoTempleteCode);
			quoTemplete = priceGeneralQuotationRepository.query(map);
		}*/
	}

	@Override
	public void calcu(Map<String, Object> map) {
	    int count=(int) map.get("num");
        //原始进来的数量
        int recount=count;
        while(count == recount){
          count = calcuDetail(map);
        }
        calcuDetail(map);   
	}
	
	private int calcuDetail(Map<String, Object> map){
	    bizList = bizPalletInfoService.querybizPallet(map);
        fees = new ArrayList<>();
        if(bizList == null || bizList.size() == 0){
            commonService.taskCountReport(taskVo, "STORAGE");
            return 0;
        }
        logger.info("taskId={} 查询行数【{}】",taskVo.getTaskId(),bizList.size());
        for (BizPalletInfoEntity entity : bizList) {
            errorMap = new HashMap<String, Object>();
            FeesReceiveStorageEntity fee = initFee(entity);
            fees.add(fee);
            try {
                if(isNoExe(entity, fee)){
                    continue; //如果不计算费用,后面的逻辑不在执行，只是在最后更新数据库状态
                }
        
                //优先合同在线计算
                calcuForContract(entity,fee);
                //如果返回的是合同缺失，则继续BMS计算
                if("CONTRACT_LIST_NULL".equals(errorMap.get("code"))){
                    calcuForBms(entity,fee);
                    //报价缺失或者未订购服务的走标准报价
                    if("4".equals(fee.getIsCalculated()) || "7".equals(fee.getIsCalculated())){
                        calcuForStand(entity, fee);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                fee.setIsCalculated(CalculateState.Sys_Error.getCode());
                fee.setCalcuMsg("系统异常");
                logger.error("计算异常",e);
            }
        }
        updateBatch(bizList,fees);
        calceCount += bizList.size();
        //更新任务计算各字段
        updateTask(taskVo,calceCount);  
        int taskRate = (int)Math.floor((calceCount*100)/unCalcuCount);
        try {
            if(unCalcuCount!=0){
                bmsCalcuTaskService.updateRate(taskVo.getTaskId(), taskRate);
            }
        } catch (Exception e) {
            logger.error("更新任务进度异常",e);
        }
        return bizList.size();
	}

	private void updateTask(BmsCalcuTaskVo taskVo,int calcuCount){
		try {
			BmsFeesQtyVo feesQtyVo = bmsCalcuService.queryFeesQtyForStoPallet(taskVo.getCustomerId(), taskVo.getSubjectCode(), taskVo.getCreMonth());
			taskVo.setUncalcuCount(feesQtyVo.getUncalcuCount()==null?0:feesQtyVo.getUncalcuCount());//本次待计算的费用数
			taskVo.setCalcuCount(calcuCount);
			taskVo.setBeginCount(feesQtyVo.getBeginCount()==null?0:feesQtyVo.getBeginCount());//未计算费用总数
			taskVo.setFinishCount(feesQtyVo.getFinishCount()==null?0:feesQtyVo.getFinishCount());//计算成功总数
			taskVo.setSysErrorCount(feesQtyVo.getSysErrorCount()==null?0:feesQtyVo.getSysErrorCount());//系统错误用总数
			taskVo.setContractMissCount(feesQtyVo.getContractMissCount()==null?0:feesQtyVo.getContractMissCount());//合同缺失总数
			taskVo.setQuoteMissCount(feesQtyVo.getQuoteMissCount()==null?0:feesQtyVo.getQuoteMissCount());//报价缺失总数
			taskVo.setNoExeCount(feesQtyVo.getNoExeCount()==null?0:feesQtyVo.getNoExeCount());//不计算费用总数
			taskVo.setCalcuStatus(feesQtyVo.getCalcuStatus());
			bmsCalcuTaskService.update(taskVo);
		} catch (Exception e) {
			logger.error("更新任务统计信息异常",e);
		}
	}
	
	@Override
	public void initConf() {
		/*//使用商品导入托数的商家
       cusNames = ObtainBussiness("Product_Pallet","group_customer");
		
		//指定的商家
       cusList=ObtainBussiness("customer_unit","group_customer");
  
		//使用导入耗材托数的商家
       materialCusNames=ObtainBussiness("Material_Pallet","group_customer");
		
       //使用导入入库托数的商家
       instockCusNames=ObtainBussiness("instock_pallet","group_customer");
       
       //使用导入出库托数的商家
       outstockCusNames=ObtainBussiness("outtock_pallet","group_customer");*/
        
	}
	
	
	/**
	 * <功能描述>
	 * 获取配置过按导入计费的商家信息
	 * @author 
	 * @date 2019年7月30日 下午3:30:43
	 * @param groupCode
	 * @param bizType
	 * @return List<String>
	 */
	private List<String> ObtainBussiness(String groupCode,String bizType){
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("groupCode", groupCode);
		map.put("bizType", bizType);
		BmsGroupVo bmsGroup=bmsGroupService.queryOne(map);
		return bmsGroup==null?null:bmsGroupCustomerService.queryCustomerByGroupId(bmsGroup.getId());
	}
	
	public boolean ObtainBussinessSe(String groupCode,
			String bizType, String customerId){
		return CollectionUtils.isEmpty(
				bmsGroupCustomerService.queryCustomerByGroupCodeAndCustomerId(groupCode,bizType,customerId));
	}
	

	@Override
	public FeesReceiveStorageEntity initFee(BizPalletInfoEntity entity) {
		FeesReceiveStorageEntity fee = new FeesReceiveStorageEntity();	
		//如果托数类型是商品托数，如果商家不在《使用导入商品托数的商家》, 更新计费来源是系统, 同时使用系统托数计费
		//如果托数类型是商品托数，如果商家在《使用导入商品托数的商家》,更新计费来源是导入,同时使用导入托数计费
		//**** 以上逻辑可以放在定时任务中
		double num = 0d;
		if ("product".equals(entity.getBizType()) && !ObtainBussinessSe("Product_Pallet","group_customer",entity.getCustomerId())) {
		    entity.setChargeSource("system");	
		}else if ("material".equals(entity.getBizType())&&!ObtainBussinessSe("Material_Pallet","group_customer",entity.getCustomerId())){
            entity.setChargeSource("system");   
        }else if("instock".equals(entity.getBizType())&&!ObtainBussinessSe("instock_pallet","group_customer",entity.getCustomerId())) { 
        	entity.setChargeSource("system");  
	    }else if("outstock".equals(entity.getBizType()) &&!ObtainBussinessSe("outstock_pallet","group_customer",entity.getCustomerId())){ 
	    	entity.setChargeSource("system");  
		}else{
		    entity.setChargeSource("import");
		}
		//调整托数优先级最高
		//如果托数类型是商品托数，商家在《使用导入商品托数的商家》，计费数量用导入托数
		//如果托数类型是商品托数，商家不在《使用导入商品托数的商家》，计费数量用系统托数
		//托数类型为耗材托数，入库托数等，都使用导入托数
		if (DoubleUtil.isBlank(entity.getAdjustPalletNum())) {
			num="system".equals(entity.getChargeSource())?
					entity.getSysPalletNum():entity.getPalletNum();			
		}else {
			num = entity.getAdjustPalletNum();
		}
		fee.setQuantity(num);		
		fee.setUnit("PALLETS");
		fee.setTempretureType(entity.getTemperatureTypeCode());//设置温度类型
		fee.setCost(new BigDecimal(0));	//入仓金额
		fee.setUnitPrice(0d);
		//托数类型
		fee.setBizType(entity.getBizType());
		fee.setFeesNo(entity.getFeesNo());
		fee.setSubjectCode(subjectCode);
		fee.setParam1(TemplateTypeEnum.COMMON.getCode());
		fee.setCalcuMsg("");
		fee.setCalculateTime(JAppContext.currentTimestamp());
		fee.setCreateTime(entity.getCreateTime());
		return fee;
	}

	@Override
	public boolean isNoExe(BizPalletInfoEntity entity,FeesReceiveStorageEntity fee) {
		//1.出库托数不计算费用
		if("outstock".equals(entity.getBizType())){
			fee.setIsCalculated(CalculateState.No_Exe.getCode());
			fee.setCalcuMsg("出库托数不计算费用;");
			return true;
		}
		//2.商家已经按件收取存储费,按托存储不计费
		if ("product".equals(entity.getBizType())) {
			//如果商家已经按件收取存储费，则按托存储不计费  
			if(ObtainBussinessSe("customer_unit","group_customer",entity.getCustomerId())){
				fee.setIsCalculated(CalculateState.No_Exe.getCode());
				fee.setCalcuMsg("商家已经按件收取存储费,按托存储不计费");
				return true;
			}
		}
		return false;		
	}

	@Override
	public void calcuForBms(BizPalletInfoEntity entity,FeesReceiveStorageEntity fee) {
	    fee.setContractAttr("1");
		//合同校验
		if(contractList.size()<=0){
			fee.setIsCalculated(CalculateState.Contract_Miss.getCode());
			fee.setCalcuMsg("bms合同缺失");
			return;
		}
		
		//业务时间和合同时间进行匹配
        //合同
        CalcuContractVo contract=null;
        for(CalcuContractVo con:contractList){
            if(con.getStartDate().getTime()<=entity.getCreateTime().getTime() && entity.getCreateTime().getTime()<=con.getExpireDate().getTime()){
                contract=con;
                break;
            }
        }
		
        if(contract==null){
            fee.setIsCalculated(CalculateState.Contract_Miss.getCode());
            fee.setCalcuMsg("bms合同缺失");
            return;
        }
        
		logger.info("合同信息{}",contract.getContractNo());
		
		 //模板编号
        String quoTempleteCode=contract.getModelNo();
		
		if("fail".equals(quoTempleteCode)){
			fee.setIsCalculated(CalculateState.No_Dinggou.getCode());
			fee.setCalcuMsg("商家【"+taskVo.getCustomerName()+"】未订购科目【"+taskVo.getSubjectName()+"】的服务项!");
			return;
		}
		
		  //查询报价模板
        Map<String, Object> con = new HashMap<>();
        con.put("subjectId",serviceSubjectCode);
        con.put("quotationNo", quoTempleteCode);
        quoTemplete = priceGeneralQuotationRepository.query(con);
		
		if(quoTemplete == null){
			fee.setIsCalculated(CalculateState.Quote_Miss.getCode());
			fee.setCalcuMsg("报价模板缺失");
			return;
		}
		
		String priceType = quoTemplete.getPriceType();
		
		try{
			double amount=0d;
			switch(priceType){
			case "PRICE_TYPE_NORMAL"://一口价				
	            // -> 费用 = 托数*模板单价
				amount=fee.getQuantity()*quoTemplete.getUnitPrice();					
				fee.setUnitPrice(quoTemplete.getUnitPrice());
				fee.setParam3(quoTemplete.getId()+"");
				break;
			case "PRICE_TYPE_STEP"://阶梯价
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("quotationId", quoTemplete.getId());
				map.put("num", fee.getQuantity());//根据报价单位判断	
				List<PriceStepQuotationEntity> list=repository.queryPriceStepByQuatationId(map);
				
				
				map.clear();
				map.put("warehouse_code", entity.getWarehouseCode());
				map.put("temperature_code", entity.getTemperatureTypeCode());
				PriceStepQuotationEntity stepQuoEntity=storageQuoteFilterService.quoteFilter(list, map);
				
				if(stepQuoEntity==null){
					logger.info("阶梯报价未配置");
					fee.setIsCalculated(CalculateState.Quote_Miss.getCode());
					fee.setCalcuMsg("阶梯报价未配置");
					return;
				}
				
				if(!DoubleUtil.isBlank(stepQuoEntity.getUnitPrice())){
					amount=fee.getQuantity()*stepQuoEntity.getUnitPrice();
					fee.setUnitPrice(stepQuoEntity.getUnitPrice());
				}else{
					amount=stepQuoEntity.getFirstNum()<fee.getQuantity()?stepQuoEntity.getFirstPrice()+(fee.getQuantity()-stepQuoEntity.getFirstNum())/stepQuoEntity.getContinuedItem()*stepQuoEntity.getContinuedPrice():stepQuoEntity.getFirstPrice();
				}
				//判断封顶价
				if(!DoubleUtil.isBlank(stepQuoEntity.getCapPrice())){
					if(stepQuoEntity.getCapPrice()<amount){
						amount=stepQuoEntity.getCapPrice();
					}
				}
				fee.setParam3(stepQuoEntity.getId()+"");
				break;
			default:
				break;
			}
			fee.setCost(BigDecimal.valueOf(amount));
			fee.setParam4(priceType);
			fee.setIsCalculated(CalculateState.Finish.getCode());
			
			if(fee.getCost().compareTo(BigDecimal.ZERO) == 1){
                fee.setCalcuMsg("计算成功");
                logger.info("计算成功，费用【{}】",fee.getCost());
            }else{
                logger.info("计算不成功，费用【{}】",fee.getCost());
                fee.setCalcuMsg("单价配置为0或者计费数量/重量为0");
            }
		}catch(Exception ex){
			fee.setIsCalculated(CalculateState.Sys_Error.getCode());
			fee.setCalcuMsg("系统异常");
			logger.error("系统异常，费用【0】",ex);
		}
	}

	@Override
	public void calcuForContract(BizPalletInfoEntity entity,FeesReceiveStorageEntity fee) {
		fee.setContractAttr("2");
	    ContractQuoteQueryInfoVo queryVo = getCtConditon(entity);
		contractCalcuService.calcuForContract(entity, fee, taskVo, errorMap, queryVo,cbiVo,fee.getFeesNo());
		if("succ".equals(errorMap.get("success").toString())){
            fee.setIsCalculated(CalculateState.Finish.getCode());
			if(fee.getCost().compareTo(BigDecimal.ZERO) == 1){
				fee.setCalcuMsg("计算成功");
				logger.info("计算成功，费用【{}】",fee.getCost());
			}else{
			    logger.info("计算不成功，费用【{}】",fee.getCost());
                fee.setCalcuMsg("单价配置为0或者计费数量/重量为0");
			}
		}
		else{
			fee.setIsCalculated(errorMap.get("is_calculated").toString());
			fee.setCalcuMsg(errorMap.get("msg").toString());
		}
		if(errorMap.get("isStandard")!=null){
            fee.setContractAttr("3");
            if("succ".equals(errorMap.get("success").toString()) && fee.getCost().compareTo(BigDecimal.ZERO) == 1){     
                fee.setCalcuMsg("采用标准报价计费");
                logger.info("采用标准报价计费计算成功，费用【{}】",fee.getCost());                 
            }
		}
	}
	
	@Override
    public void calcuForStand(BizPalletInfoEntity entity,FeesReceiveStorageEntity fee) {
        fee.setContractAttr("1");
        ContractQuoteQueryInfoVo queryVo = getCtConditon(entity);
        contractCalcuService.calcuForStand(entity, fee, taskVo, errorMap, queryVo,cbiVo,fee.getFeesNo());
        if("succ".equals(errorMap.get("success").toString())){
            fee.setIsCalculated(CalculateState.Finish.getCode());
            if(fee.getCost().compareTo(BigDecimal.ZERO) == 1){
                fee.setCalcuMsg("标准报价计算成功");
                logger.info("标准报价计算成功，费用【{}】",fee.getCost());
            }else{
                logger.info("计算不成功，费用【{}】",fee.getCost());
                fee.setCalcuMsg("单价配置为0或者计费数量/重量为0");
            }
        }
        else{
            fee.setIsCalculated(errorMap.get("is_calculated").toString());
            fee.setCalcuMsg(errorMap.get("msg").toString());
        }
    }

	@Override
	public ContractQuoteQueryInfoVo getCtConditon(BizPalletInfoEntity entity) {
		ContractQuoteQueryInfoVo queryVo = new ContractQuoteQueryInfoVo();
		queryVo.setCustomerId(entity.getCustomerId());
		queryVo.setBizTypeCode(ContractBizTypeEnum.STORAGE.getCode());
		queryVo.setSubjectCode(subjectCode);
		queryVo.setCurrentTime(entity.getCreateTime());
		queryVo.setWarehouseCode(entity.getWarehouseCode());
		return queryVo;
	}

	@Override
	public void updateBatch(List<BizPalletInfoEntity> bizList,List<FeesReceiveStorageEntity> feeList) {
		//业务表更新计费来源
		
		StopWatch sw = new StopWatch();
		
		sw.start();
		bizPalletInfoService.updatebizPallet(bizList);
		sw.stop();
		logger.info("taskId={} 更新托数业务数据行数【{}】 耗时【{}】",taskVo.getTaskId(),bizList.size(),sw.getLastTaskTimeMillis());
		
		sw.start();
		feesReceiveStorageService.updateBatch(feeList);
		sw.stop();
		logger.info("taskId={} 更新仓储费用行数【{}】 耗时【{}】",taskVo.getTaskId(),feeList.size(),sw.getLastTaskTimeMillis());
		
	}
	
	

}
