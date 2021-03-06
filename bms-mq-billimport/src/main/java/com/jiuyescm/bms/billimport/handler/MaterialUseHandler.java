package com.jiuyescm.bms.billimport.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jiuyescm.bms.base.dict.api.ICustomerDictService;
import com.jiuyescm.bms.billimport.entity.BillFeesReceiveStorageTempEntity;
import com.jiuyescm.bms.billimport.service.IBillFeesReceiveStorageTempService;
import com.jiuyescm.bms.excel.data.DataColumn;
import com.jiuyescm.bms.excel.data.DataRow;
import com.jiuyescm.common.utils.DateUtil;
import com.jiuyescm.exception.BizException;

/**
 * 耗材使用费
 * @author zhaofeng
 *
 */
@Component("耗材使用费")
public class MaterialUseHandler extends CommonHandler<BillFeesReceiveStorageTempEntity> {
	
	private static final Logger logger = LoggerFactory.getLogger(MaterialUseHandler.class);

	@Autowired
	private ICustomerDictService customerDictService;
	@Autowired 
	IBillFeesReceiveStorageTempService billFeesReceiveStorageTempService;

	@Override
	public List<BillFeesReceiveStorageTempEntity> transRowToObj(DataRow dr)
			throws Exception {
		//异常信息
		String errorMessage="";
		List<BillFeesReceiveStorageTempEntity> list = new ArrayList<BillFeesReceiveStorageTempEntity>();
		
		boolean isWaybillNull = false; //运单号是否为空  true-空  false-非空
		
		BillFeesReceiveStorageTempEntity entity = new BillFeesReceiveStorageTempEntity();
		Map<String,Integer> repeatMap=new HashMap<String, Integer>();
		for (DataColumn dc:dr.getColumns()) {
			try {
				switch (dc.getTitleName()) {
				case "商家名称":
					if (StringUtils.isBlank(dc.getColValue())) {
						//isCustomerNull = true;
					}
					break;
				case "仓库":
					if(StringUtils.isNotBlank(dc.getColValue())){
						entity.setWarehouseName(dc.getColValue());
						String wareId=warehouseMap.get(dc.getColValue());
						if(StringUtils.isNotBlank(wareId)){
							entity.setWarehouseCode(wareId);
						}else{
							errorMessage+="仓库不存在;";
						}
					}else{
						errorMessage+="仓库不能为空;";
					}					
					break;
				case "运单号":
					if(StringUtils.isNotBlank(dc.getColValue())){
						entity.setWaybillNo(dc.getColValue());
					}else{
						isWaybillNull = true;
						errorMessage+="运单号不能为空;";
					}
					break;
				case "出库单号":
					entity.setOrderNo(dc.getColValue());
					break;
				case "商品总数":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setTotalQty(Integer.parseInt(dc.getColValue()));
					}
					break;
				case "接单时间":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setCreateTime(DateUtil.transStringToTimeStamp(dc.getColValue()));
						entity.setCreateMonth(DateUtil.transStringToInteger(dc.getColValue()));
					}else{
						errorMessage+="接单时间不能为空;";
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				errorMessage+="列【"+ dc.getColName() + "】格式不正确;";
			}
		}
		
		if(isWaybillNull){
			return list;
		}
		
		//起始列
		int index=0;
		for (DataColumn dc:dr.getColumns()) {
			if("编码".equals(dc.getColName())){
				index=dc.getColNo()-1;
				break;
			}
		}
		int count=1;
		BillFeesReceiveStorageTempEntity feeEntity=new BillFeesReceiveStorageTempEntity();
		for (DataColumn dc : dr.getColumns()) {
			try {				
				if(count==1){
					feeEntity=new BillFeesReceiveStorageTempEntity();
					PropertyUtils.copyProperties(feeEntity, entity);
				}
				switch (dc.getTitleName()) {
				case "编码":
					if(StringUtils.isNotBlank(dc.getColValue())){
						
						String barCode=materialMap.get(dc.getColValue());
						if(StringUtils.isNotBlank(barCode)){
							feeEntity.setMaterialCode(barCode);
						}else{
							errorMessage+="列【"+dc.getColValue()+"】编码不存在;";
						}
					}
					break;
				case "数量":
					if(StringUtils.isNotBlank(dc.getColValue())){
						feeEntity.setTotalQty(Integer.valueOf(dc.getColValue()));
					}
					break;
				case "重量":
					if(StringUtils.isNotBlank(dc.getColValue())){
						feeEntity.setTotalWeight(new BigDecimal(dc.getColValue()));
					}
					break;
				case "金额":
					if(StringUtils.isNotBlank(dc.getColValue())){
						feeEntity.setAmount(new BigDecimal(dc.getColValue()));
					}
					break;
				case "规格":
					break;
				case "单价":
					break;
				default:
					if(StringUtils.isNotBlank(dc.getColValue())){						
						feeEntity.setMaterialName(dc.getColValue());						
					}			
					break;
				}
	
				count++;
				if(count>6){
					if(StringUtils.isNotBlank(feeEntity.getMaterialCode())){
						feeEntity.setBillNo(billNo);
						feeEntity.setCustomerName(customerName);
						feeEntity.setCustomerId(customerId);
						feeEntity.setSubjectCode("wh_material_use");
						list.add(feeEntity);
					}
					count=1;
				}
			} catch (Exception e) {
				errorMessage+="列【"+ dc.getColName() + "】格式不正确;";
			}
		}

		//重复性校验
		if(StringUtils.isNotBlank(entity.getWaybillNo())){
			if(repeatMap.containsKey(entity.getWaybillNo())){
				errorMessage += "与第" + repeatMap.get(entity.getWaybillNo()) + "行运单号重复;";
			}else{
				repeatMap.put(entity.getWaybillNo(), dr.getRowNo());
			}
		}
		
		if(StringUtils.isNotBlank(errorMessage)){
			throw new BizException(errorMessage);
		}
		return list;
	}

	@Override
	public void transErr(DataRow dr) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int save() {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();// 系统开始时间
		int result=billFeesReceiveStorageTempService.insertBatchTemp(list);
		logger.info("账单【{}】 保存行数【{}】耗材使用费到仓储临时表耗时【{}】",billNo,list.size(),(System.currentTimeMillis()-start));
		return result;

	}

	@Override
	public String validate(List<String> columns) throws Exception {
		// TODO Auto-generated method stub
		String result="";
		String[] str = {"仓库", "运单号", "接单时间"}; //必填列
		
		for (String s : str) {
			if(!columns.contains(s)){
				result+=s+"必须存在;";
			}
		} 
		
		if(StringUtils.isNotBlank(result)){
			result="【"+sheetName+"】表头:"+result;
			return result;
		}
		
		return "SUCC";
	}



}
