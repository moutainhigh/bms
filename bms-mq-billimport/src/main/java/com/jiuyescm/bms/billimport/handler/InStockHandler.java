package com.jiuyescm.bms.billimport.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jiuyescm.bms.base.dict.api.IWarehouseDictService;
import com.jiuyescm.bms.billimport.entity.BillFeesReceiveStorageTempEntity;
import com.jiuyescm.bms.billimport.service.IBillFeesReceiveStorageTempService;
import com.jiuyescm.bms.excel.data.DataColumn;
import com.jiuyescm.bms.excel.data.DataRow;
import com.jiuyescm.common.utils.DateUtil;
import com.jiuyescm.exception.BizException;

/**
 * 入库
 * @author zhaofeng
 *
 */
@Component("入库")
public class InStockHandler extends CommonHandler<BillFeesReceiveStorageTempEntity> {

	@Autowired IBillFeesReceiveStorageTempService billFeesReceiveStorageTempService;
	@Autowired IWarehouseDictService warehouseDictService;
	
	@Override
	public List<BillFeesReceiveStorageTempEntity> transRowToObj(DataRow dr)
			throws Exception {
		
		//异常信息
		String errorMessage="";
		
		List<BillFeesReceiveStorageTempEntity> list = new ArrayList<BillFeesReceiveStorageTempEntity>();
		
		//判断空白行
		/*DataColumn orderNo=dr.getColumn("入库单号");
		DataColumn customerCo=dr.getColumn("商家名称");
		if(orderNo!=null && customerCo!=null &&StringUtils.isBlank(orderNo.getColValue()+customerCo.getColValue())){
			return list;
		}*/
		
		boolean isOrderNoNull = false;
		boolean isCustomerNull = false;
		
		BillFeesReceiveStorageTempEntity entity = new BillFeesReceiveStorageTempEntity();
		BillFeesReceiveStorageTempEntity entity1 = new BillFeesReceiveStorageTempEntity();
		BillFeesReceiveStorageTempEntity entity2 = new BillFeesReceiveStorageTempEntity();
		for (DataColumn dc:dr.getColumns()) {
			try {
				switch (dc.getTitleName()) {
				case "商家名称":
					if (StringUtils.isBlank(dc.getColValue())) {
						isCustomerNull = true;
					}
					break;
				case "仓库名称":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setWarehouseName(dc.getColValue());
						//如果没找到，报错
						String warehouseCode = warehouseDictService.getWarehouseCodeByName(dc.getColValue());
						if (StringUtils.isNotBlank(warehouseCode)) {
							entity.setWarehouseCode(warehouseCode);
						}else {
							errorMessage+="仓库不存在;";
						}
					}else {
						errorMessage+="仓库必填;";
					}		
					break;
				case "收货确认时间":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setCreateTime(DateUtil.transStringToTimeStamp(dc.getColValue()));
						entity.setCreateMonth(DateUtil.transStringToInteger(dc.getColValue()));
					}else {
						errorMessage+="收货确认时间必填;";
					}
					break;
				case "单据类型":
					entity.setOrderType(dc.getColValue());
					break;
				case "入库单号":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setOrderNo(dc.getColValue());
					}else {
						isOrderNoNull = true;
						errorMessage+="入库单号必填;";
					}
					break;
				case "入库件数":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setTotalQty(Integer.valueOf(dc.getColValue()));
					}	
					break;
				case "入库箱数":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setTotalBox(new BigDecimal(dc.getColValue()));
					}
					break;
				case "入库重量(吨)":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setTotalWeight(new BigDecimal(dc.getColValue()));
					}else {
						errorMessage+="入库重量(吨)必填;";
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				errorMessage+="列【"+ dc.getColName() + "】格式不正确;";
			}
		}
	
		for (DataColumn dc:dr.getColumns()) {
			try {
				switch (dc.getTitleName()) {
				case "入库操作费":
					PropertyUtils.copyProperties(entity1, entity);
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity1.setAmount(new BigDecimal(dc.getColValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
						//list.add(entity1);
					}else{
						entity1.setAmount(new BigDecimal(0));
					}
					entity1.setBillNo(billNo);
					entity1.setCustomerName(customerName);
					entity1.setCustomerId(customerId);
					entity1.setSubjectCode("wh_instock_work");			
					break;
				case "入库卸货费":
					PropertyUtils.copyProperties(entity2, entity);
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity2.setAmount(new BigDecimal(dc.getColValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
					}else{
						entity2.setAmount(new BigDecimal(0));
					}
					entity2.setBillNo(billNo);
					entity2.setCustomerName(customerName);
					entity2.setCustomerId(customerId);
					entity2.setSubjectCode("wh_b2c_handwork");				
					break;
				default:
					break;
				}
			} catch (Exception e) {
				errorMessage+="列【"+ dc.getColName() + "】格式不正确;";
			}	
		}
		
		if(isOrderNoNull && isCustomerNull){
			return list;
		}
		
		//重复性校验
		if(StringUtils.isNotBlank(entity.getOrderNo())){
			if(repeatMap.containsKey(entity.getOrderNo())){
				errorMessage += "与第"
						+ repeatMap.get(entity.getOrderNo()) + "行出库单号重复;";
			}else{
				repeatMap.put(entity.getOrderNo(), dr.getRowNo());
			}
		}
		
		if (StringUtils.isNotBlank(entity1.getOrderNo())) {
			list.add(entity1);
		}
		if (StringUtils.isNotBlank(entity2.getOrderNo())) {
			list.add(entity2);
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
		int result=billFeesReceiveStorageTempService.insertBatchTemp(list);
		return result;
	}

	@Override
	public String validate(List<String> columns) throws Exception {
		// TODO Auto-generated method stub
		String result="";
		String[] str = {"仓库名称", "收货确认时间", "单据类型","入库单号","入库重量(吨)"}; //必填列
		
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
