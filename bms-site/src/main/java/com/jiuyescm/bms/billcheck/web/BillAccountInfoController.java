package com.jiuyescm.bms.billcheck.web;
import org.springframework.stereotype.Component;

import com.bstek.dorado.annotation.Expose;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.provider.Page;
import com.github.pagehelper.PageInfo;
import com.jiuyescm.bms.base.airport.entity.PubAirportEntity;
import com.jiuyescm.bms.base.airport.service.IPubAirportService;
import com.jiuyescm.bms.billcheck.BillAccountInEntity;
import com.jiuyescm.bms.billcheck.BillAccountInfoEntity;
import com.jiuyescm.bms.billcheck.service.IBmsAccountInfoService;
import com.jiuyescm.bms.common.sequence.service.SequenceService;
import com.jiuyescm.cfm.common.JAppContext;

import org.springframework.stereotype.Component;

import com.bstek.dorado.annotation.Expose;

@Component
@Controller("billCheckPR")
public class BillAccountInfoController {
	private static final Logger logger = Logger.getLogger(BillAccountInfoController.class.getName());
	@Resource private IBmsAccountInfoService billAccountInfoService;

	@Expose
	public BillAccountInfoEntity findByCustomerId(String customerId) throws Exception {
		BillAccountInfoEntity entity = null;
		entity = billAccountInfoService.findByCustomerId(customerId);
		return entity;
	}
	@DataProvider
	public void queryAll(Page<BillAccountInfoEntity> page,Map<String,Object> parameter){
		
		PageInfo<BillAccountInfoEntity> tmpPageInfo = billAccountInfoService.query(parameter, page.getPageNo(),page.getPageSize());
		if (tmpPageInfo != null) {
			page.setEntities(tmpPageInfo.getList());
			page.setEntityCount((int) tmpPageInfo.getTotal());
		}
		
	}
	
	@DataResolver
	public void save(BillAccountInfoEntity entity) {
			 if (null != entity) {
			      entity.setCreatorId(JAppContext.currentUserID());
			      entity.setCreateTime(JAppContext.currentTimestamp());
			      entity.setCreator(JAppContext.currentUserName());
			      entity.setDelFlag("0");
			      billAccountInfoService.save(entity);
			 }
	}
	
	@DataResolver
	public BillAccountInfoEntity update(BillAccountInfoEntity entity){
		BillAccountInfoEntity res = null;
		 if (null != entity && null != entity.getAmount()) {
			 res =  billAccountInfoService.update(entity);
		 }		
		 return res;
		 
}


}