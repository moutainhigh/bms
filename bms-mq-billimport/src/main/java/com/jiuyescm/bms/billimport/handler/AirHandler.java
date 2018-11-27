package com.jiuyescm.bms.billimport.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jiuyescm.bms.billimport.IFeesHandler;
import com.jiuyescm.bms.billimport.entity.BillFeesReceiveAirTempEntity;
import com.jiuyescm.bms.billimport.service.IBillFeesReceiveAirTempService;
import com.jiuyescm.bms.excel.ExcelXlsxReader;
import com.jiuyescm.bms.excel.callback.SheetReadCallBack;
import com.jiuyescm.bms.excel.data.DataColumn;
import com.jiuyescm.bms.excel.data.DataRow;
import com.jiuyescm.bms.excel.opc.OpcSheet;
import com.jiuyescm.common.utils.DateUtil;
import com.jiuyescm.common.utils.excel.POISXSSUtil;
import com.jiuyescm.exception.BizException;
import com.jiuyescm.framework.fastdfs.model.StorePath;

/**
 * 航空
 * 
 * @author liuzhicheng
 * 
 */
@Component("航空")
public class AirHandler extends CommonHandler<BillFeesReceiveAirTempEntity> {
	
	@Autowired
	private IBillFeesReceiveAirTempService billFeesReceiveAirTempService;

	@Override
	public List<BillFeesReceiveAirTempEntity> transRowToObj(DataRow dr)
			throws Exception {
		List<BillFeesReceiveAirTempEntity> listEntity = new ArrayList<BillFeesReceiveAirTempEntity>();
		BillFeesReceiveAirTempEntity entity = new BillFeesReceiveAirTempEntity();
		BillFeesReceiveAirTempEntity entity1 = new BillFeesReceiveAirTempEntity();
		BillFeesReceiveAirTempEntity entity2 = new BillFeesReceiveAirTempEntity();
		BillFeesReceiveAirTempEntity entity3 = new BillFeesReceiveAirTempEntity();
		for (DataColumn dc : dr.getColumns()) {
			try {
				System.out.println("列名【" + dc.getColName() + "】|值【"
						+ dc.getColValue() + "】");
				
				
				switch (dc.getColName()) {
				case "区域仓":
					entity.setWarehouseName(dc.getColValue());
					break;
				case "发货日期":
					entity.setCreateTime(DateUtil.transStringToTimeStamp(dc
							.getColValue()));
					break;
				case "始发站":
					entity.setSendSite(dc.getColValue());
					break;
				case "目的站":
					entity.setReceiveSite(dc.getColValue());
					break;
				case "计费重量":
					entity.setTotalWeight(new BigDecimal(dc.getColValue()));
					break;
				case "运单号":
					entity.setWaybillNo(dc.getColValue());
					break;
				default:
					break;
				}

/*				if (dc.getColName().equals("区域仓")) {
					entity.setWarehouseName(dc.getColValue());
				} else if (dc.getColName().equals("发货日期")) {
					entity.setCreateTime(DateUtil.transStringToTimeStamp(dc
							.getColValue()));
				} else if (dc.getColName().equals("始发站")) {
					entity.setSendSite(dc.getColValue());
				} else if (dc.getColName().equals("目的站")) {
					entity.setReceiveSite(dc.getColValue());
				} else if (dc.getColName().equals("计费重量")) {
					entity.setTotalWeight(new BigDecimal(dc.getColValue()));
				} else if (dc.getColName().equals("运单号")) {
					entity.setWaybillNo(dc.getColValue());
				}*/

			} catch (Exception ex) {
				throw new BizException("行【" + dr.getRowNo() + "】，列【"
						+ dc.getColName() + "】格式不正确");
			}
		}
	
		for(DataColumn dc : dr.getColumns()){
			switch (dc.getColName()) {
			case "航空运费":
				PropertyUtils.copyProperties(entity1, entity);
				entity1.setAmount(new BigDecimal(dc.getColValue()));
				entity1.setFeesType("BASE");
				listEntity.add(entity1);
				break;
			case "其他费用":
				PropertyUtils.copyProperties(entity2, entity);
				entity2.setAmount(new BigDecimal(dc.getColValue()));
				entity2.setFeesType("OTHER");
				listEntity.add(entity2);
				break;
			case "货物赔偿费":
				PropertyUtils.copyProperties(entity3, entity);
				entity3.setAmount(new BigDecimal(dc.getColValue()));
				entity3.setFeesType("BASE");
				listEntity.add(entity3);
				break;
			default:
				break;
			}

		}
		
		return listEntity;
	}

	@Override
	public void transErr(DataRow dr) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void save() {
		billFeesReceiveAirTempService.insertBatchTemp(list);

	}

}
