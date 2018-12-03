package com.jiuyescm.bms.billimport.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jiuyescm.bms.base.dict.api.ICustomerDictService;
import com.jiuyescm.bms.base.dict.api.IMaterialDictService;
import com.jiuyescm.bms.base.dict.api.IWarehouseDictService;
import com.jiuyescm.bms.billimport.entity.BillFeesReceiveStorageTempEntity;
import com.jiuyescm.bms.billimport.service.IBillFeesReceiveStorageTempService;
import com.jiuyescm.bms.excel.data.DataColumn;
import com.jiuyescm.bms.excel.data.DataRow;
import com.jiuyescm.common.utils.DateUtil;
import com.jiuyescm.exception.BizException;
import com.jiuyescm.mdm.customer.vo.PubMaterialInfoVo;

/**
 * 耗材使用费
 * @author zhaofeng
 *
 */
@Component("耗材使用费")
public class MaterialUseHandler extends CommonHandler<BillFeesReceiveStorageTempEntity> {
	@Autowired
	private IWarehouseDictService warehouseDictService;
	@Autowired
	private ICustomerDictService customerDictService;
	@Autowired
	private IMaterialDictService materialDictService;
	@Autowired 
	IBillFeesReceiveStorageTempService billFeesReceiveStorageTempService;

	@Override
	public List<BillFeesReceiveStorageTempEntity> transRowToObj(DataRow dr)
			throws Exception {
		//异常信息
		String errorMessage="";
		// TODO Auto-generated method stub
		List<BillFeesReceiveStorageTempEntity> list = new ArrayList<BillFeesReceiveStorageTempEntity>();
		
		DataColumn waybillCo=dr.getColumn("运单号");
		DataColumn customerCo=dr.getColumn("商家名称");
		if(waybillCo!=null && customerCo!=null &&StringUtils.isBlank(waybillCo.getColValue()+customerCo.getColValue())){
			return list;
		}
		
		BillFeesReceiveStorageTempEntity entity = new BillFeesReceiveStorageTempEntity();
		Map<String,Integer> repeatMap=new HashMap<String, Integer>();
		for (DataColumn dc:dr.getColumns()) {
			try {
				switch (dc.getColName()) {
				case "仓库":
					if(StringUtils.isNotBlank(dc.getColValue())){
						entity.setWarehouseName(dc.getColValue());
						String wareId=warehouseDictService.getWarehouseCodeByName(dc.getColValue());
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
		
		//起始列
		int index=1;;
		for (DataColumn dc:dr.getColumns()) {
			if("收件人地址".equals(dc.getColName())){
				index+=dc.getColNo();
			}		
		}
		int count=1;
		BillFeesReceiveStorageTempEntity feeEntity=new BillFeesReceiveStorageTempEntity();
		for(int i=index;i<dr.getColumns().size();i++){
			DataColumn dc=dr.getColumn(i);
			try {				
				if(count==1){
					feeEntity=new BillFeesReceiveStorageTempEntity();
					PropertyUtils.copyProperties(feeEntity, entity);
				}
				switch (dc.getColName()) {
				case "编码":
					if(StringUtils.isNotBlank(dc.getColValue())){
						PubMaterialInfoVo vo=materialDictService.getMaterialByCode(dc.getColValue());
						if(vo!=null){
							feeEntity.setMaterialCode(dc.getColValue());
						}else{
							errorMessage+="列"+dc.getColNo()+"编码不存在;";
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
						PubMaterialInfoVo pubvo=materialDictService.getMaterialByName(dc.getColValue());
						if(pubvo!=null){
							feeEntity.setMaterialName(dc.getColValue());
						}else{
							errorMessage+="列"+dc.getColNo()+"耗材名称不存在;";
						}
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
				errorMessage += "与第"
						+ repeatMap.get(entity.getWaybillNo()) + "行运单号重复;";
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
	public void save() {
		// TODO Auto-generated method stub
		if(null != list && list.size()>0){
			billFeesReceiveStorageTempService.insertBatchTemp(list);
		}
	}



}
