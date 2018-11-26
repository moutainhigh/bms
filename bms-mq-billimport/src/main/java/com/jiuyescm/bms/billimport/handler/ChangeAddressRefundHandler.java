package com.jiuyescm.bms.billimport.handler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.jiuyescm.bms.billimport.entity.BillFeesReceiveStorageTempEntity;
import com.jiuyescm.bms.excel.data.DataColumn;
import com.jiuyescm.bms.excel.data.DataRow;
import com.jiuyescm.exception.BizException;

/**
 * 改地址退件费
 * @author wangchen870
 *
 */

@Component("改地址退件费")
public class ChangeAddressRefundHandler extends CommonHandler<BillFeesReceiveStorageTempEntity>{

	@Override
	public List<BillFeesReceiveStorageTempEntity> transRowToObj(DataRow dr) throws Exception {
		List<BillFeesReceiveStorageTempEntity> list = new ArrayList<BillFeesReceiveStorageTempEntity>();
		BillFeesReceiveStorageTempEntity entity = new BillFeesReceiveStorageTempEntity();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		for (DataColumn dc:dr.getColumns()) {
			try {
				switch (dc.getColName()) {
				case "发货仓库":
					entity.setWarehouseName(dc.getColValue());
					break;
				case "运单日期":
					Timestamp time = null;
					if (StringUtils.isNotBlank(dc.getColValue())) {
						time = new Timestamp(sdf.parse(dc.getColValue()).getTime());
					}
					entity.setCreateTime(time);
					break;
				case "运单号":
					entity.setWaybillNo(dc.getColValue());
					break;
				case "金额":
					if (StringUtils.isNotBlank(dc.getColValue())) {
						entity.setAmount(Double.valueOf(dc.getColValue()));
					}			
					break;
				default:
					break;
				}
			} catch (Exception e) {
				throw new BizException("行【"+dr.getRowNo()+"】，列【"+dc.getColName()+"】格式不正确");
			}
		}
		//B2B订单操作费，出库装车费
		list.add(entity);
		return list;
	}

	@Override
	public void transErr(DataRow dr) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
	
}