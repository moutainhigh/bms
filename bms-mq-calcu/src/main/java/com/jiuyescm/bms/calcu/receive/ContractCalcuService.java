package com.jiuyescm.bms.calcu.receive;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.json.JSONObject;

import com.jiuyescm.bms.asyn.vo.BmsCalcuTaskVo;
import com.jiuyescm.bms.calculate.vo.CalcuBaseInfoVo;
import com.jiuyescm.bms.chargerule.receiverule.entity.BillRuleReceiveEntity;
import com.jiuyescm.bms.common.enumtype.CalculateState;
import com.jiuyescm.bms.drools.IFeesCalcuService;
import com.jiuyescm.bms.rule.receiveRule.repository.IReceiveRuleRepository;
import com.jiuyescm.bs.util.StringUtil;
import com.jiuyescm.contract.quote.api.IContractQuoteInfoService;
import com.jiuyescm.contract.quote.vo.ContractQuoteInfoVo;
import com.jiuyescm.contract.quote.vo.ContractQuoteQueryInfoVo;
import com.jiuyescm.exception.BizException;

/**
 * 合同在线计算服务
 * @author caojianwei
 *
 */
@Component("contractCalcuService")
public class ContractCalcuService {

	private Logger logger = LoggerFactory.getLogger(ContractCalcuService.class);
	
	@Autowired private IContractQuoteInfoService contractQuoteInfoService;
	@Autowired private IReceiveRuleRepository receiveRuleRepository;
	@Autowired private IFeesCalcuService feesCalcuService;
	
	public void calcuForContract(Object entity, Object fee, BmsCalcuTaskVo vo, Map<String, Object> errorMap,ContractQuoteQueryInfoVo queryVo,CalcuBaseInfoVo cbiVo){
		//logger.info("taskId={} 合同在线查询参数：",vo.getTaskId(),JSONObject.fromObject(queryVo));
		queryCtForContract(vo,entity,fee,queryVo,errorMap,cbiVo);
	}
	
	private void queryCtForContract(BmsCalcuTaskVo vo,Object entity, Object fee,ContractQuoteQueryInfoVo queryVo,Map<String, Object> errorMap,CalcuBaseInfoVo cbiVo){
		ContractQuoteInfoVo cqVo = null;
		try{
			cqVo = contractQuoteInfoService.queryUniqueColumns(queryVo);
		}
		catch(BizException ex){
			//logger.info("taskId={} 合同在线合同缺失 {}",vo.getTaskId(),ex.getMessage());
			errorMap.put("success", "fail");
			errorMap.put("is_calculated", CalculateState.Contract_Miss.getCode());
			errorMap.put("msg", "合同在线合同缺失");
			return;
		}
		if(cqVo == null){
			return;
		}
		
		Map<String, String> contractMap = new HashMap<>();
		contractMap.put("contractNo", cqVo.getQuoteVo().getContractNo());//合同编号
		contractMap.put("serviceOrderNo", cqVo.getQuoteVo().getServiceOrderNo());//订购号
		contractMap.put("version", cqVo.getQuoteVo().getVersion());//合同版本
		contractMap.put("ruleCode", cqVo.getQuoteVo().getRuleCode());//规则编号
		
		logger.info("taskId={} contractInfo={}",vo.getTaskId(),contractMap);
		if(StringUtil.isEmpty(cqVo.getRuleCode())){
			logger.info("taskId={} 合同在线规则未绑定",vo.getTaskId());
			errorMap.put("success", "fail");
			errorMap.put("is_calculated", CalculateState.Quote_Miss.getCode());
			errorMap.put("msg", "合同在线规则未绑定");
			return;
		}
		try{
			Map<String, Object> con = new HashMap<>();
			con.put("quotationNo", cqVo.getRuleCode());
			BillRuleReceiveEntity ruleEntity = receiveRuleRepository.queryOne(con);
			if (null == ruleEntity) {
				String msg = "规则【"+cqVo.getRuleCode()+"】不存在";
				logger.info("taskId={} {}",vo.getTaskId(),msg);
				errorMap.put("success", "fail");
				errorMap.put("is_calculated", CalculateState.Sys_Error.getCode());
				errorMap.put("msg", msg);
				return;
			}
			//获取合同在线查询条件
			Map<String, Object> cond = new HashMap<String, Object>();
			feesCalcuService.ContractCalcuService(entity, cond, ruleEntity.getRule(), ruleEntity.getQuotationNo());
			logger.info("taskId={} 获取合同在线报价参数：{}",vo.getTaskId(),cond);
			ContractQuoteInfoVo rtnQuoteInfoVo = null;
			
			try {
			    if(cond == null || cond.size() == 0){
			    	logger.info("taskId={} 规则引擎拼接条件异常",vo.getTaskId());
					errorMap.put("success", "fail");
					errorMap.put("is_calculated", CalculateState.Sys_Error.getCode());
					errorMap.put("msg", "规则引擎拼接条件异常");
					return;
				}
				rtnQuoteInfoVo = contractQuoteInfoService.queryQuotes(cqVo, cond);
			} catch (BizException e) {
				String msg = "获取合同在线报价异常:"+e.getMessage();
				logger.info("taskId={} {}",vo.getTaskId(),msg);
				errorMap.put("success", "fail");
				errorMap.put("is_calculated", CalculateState.Quote_Miss.getCode());
				errorMap.put("msg", msg);
				return;
			}
			
			/*logger.info("获取合同在线报价结果"+JSONObject.fromObject(rtnQuoteInfoVo));*/
			for (Map<String, String> map : rtnQuoteInfoVo.getQuoteMaps()) {
				int i = rtnQuoteInfoVo.getQuoteMaps().indexOf(map);
			}
			//调用规则计算费用
			feesCalcuService.ContractCalcuService(fee, rtnQuoteInfoVo.getQuoteMaps(), ruleEntity.getRule(), ruleEntity.getQuotationNo());
			
		}
		catch(Exception ex){
			logger.info("系统异常",ex);
			errorMap.put("success", "fail");
			errorMap.put("is_calculated", CalculateState.Sys_Error.getCode());
			errorMap.put("msg", "系统异常");
			return;
		}
	}
	
}