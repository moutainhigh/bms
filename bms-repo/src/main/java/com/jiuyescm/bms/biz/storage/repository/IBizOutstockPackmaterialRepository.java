package com.jiuyescm.bms.biz.storage.repository;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.pagehelper.PageInfo;
import com.jiuyescm.bms.asyn.entity.BmsAsynCalcuTaskEntity;
import com.jiuyescm.bms.biz.storage.entity.BizOutstockPackmaterialEntity;
import com.jiuyescm.bms.fees.storage.vo.FeesReceiveMaterial;

public interface IBizOutstockPackmaterialRepository {

	PageInfo<BizOutstockPackmaterialEntity> query(Map<String, Object> condition, int pageNo, int pageSize);
	
	List<BizOutstockPackmaterialEntity> getBizstockPack(Map<String, Object> condition);
	
	public int update(BizOutstockPackmaterialEntity entity);
	
	int updateList(List<BizOutstockPackmaterialEntity> list);
	
	int saveList(List<BizOutstockPackmaterialEntity> list);
	
	/*
	 * 仓库维度统计
	 */
	PageInfo<BizOutstockPackmaterialEntity> queryWarehouseGroupCount(Map<String, Object> condition,int pageNo, int pageSize);
	
	/*
	 * 财务维度统计
	 */
	PageInfo<BizOutstockPackmaterialEntity> queryPriceGroupCount(Map<String, Object> condition,int pageNo, int pageSize);
	
    Properties validRetry(Map<String, Object> param);
	 
	 int reCalculate(Map<String, Object> param);
	 
	 int deleteList(List<BizOutstockPackmaterialEntity> list);
		
	List<BizOutstockPackmaterialEntity> queryList(Map<String, Object> condition);
	
	int deleteFees(Map<String, Object> condition);
	
	PageInfo<Map<String,String>> queryByMonth(Map<String, Object> condition,int pageNo, int pageSize);
	 	 
    PageInfo<Map<String,String>>  queryCustomeridF(Map<String, Object> condition,int pageNo, int pageSize);
	
	 /**
	  * 判断是否有计算异常的数据
	  * @param condition
	  * @return
	  */
	 public BizOutstockPackmaterialEntity queryExceptionOne(Map<String,Object> condition);
	 
	 List<Map<String,String>> getMaterialCode(Map<String,Object> param);

	List<BizOutstockPackmaterialEntity> getOrgPackMaterialList(
			Map<String, Object> querymap);
	
	List<String> queryBillWarehouse(Map<String,Object> param);
	
	List<BizOutstockPackmaterialEntity> queryAllByWaybillNoList(
			List<String> waybillNoList);

	int updateCostIsCalculated(Map<String, Object> param, String isCalculated);

	List<String> queryCosthasBill(Map<String, Object> param);

	int saveDataFromTemp(String batchNum);

	boolean checkDataExist(String waybillNoExcel, String materialCode);

	List<BizOutstockPackmaterialEntity> getMaterialCodeFromBizData(
			Map<String, Object> condition);

	PageInfo<FeesReceiveMaterial> queryMaterialFromBizData(
			Map<String, Object> condition, int pageNo, int size);

	List<BizOutstockPackmaterialEntity> queryAllWarehouseFromBizData(
			Map<String, Object> condition);
	
	List<BizOutstockPackmaterialEntity> queryMaterial(Map<String, Object> condition);
	public List<String> queryWayBillNo(Map<String,Object> condition);
	
	Double getMaxVolum(Map<String,Object> condition);
	
	Double getMaxPmxVolum(Map<String,Object> condition);
	
	Double getMaxZxVolum(Map<String,Object> condition);
	
	Double getMaxVolumByMap(Map<String,Object> condition);
	
	Double getStandVolumByMap(Map<String,Object> condition);

	int deleteAllByWayBillNo(List<String> waybillNoList);
	
	/**
	 * 非成功统计
	 * @param condition
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	PageInfo<BizOutstockPackmaterialEntity> queryErrorCal(Map<String, Object> condition, int pageNo, int pageSize);
	
	/**
	 * 查询原始耗材
	 * @param condition
	 * @return
	 */
	List<BizOutstockPackmaterialEntity> queryOriginMaterialFromBizData(Map<String, Object> condition);
	
	/**
	 * 获取最高的体积
	 * @param condition
	 * @return
	 */
	public String getMaxBwdVolumn(Map<String, Object> condition);
	
	
	
	/**
	 * 删除运单号对应的泡沫箱和纸箱
	 * @param wayList
	 * @return
	 */
	public int deleteOldMaterial(Map<String, Object> condition);
	
	/**
	 * 删除运单号对应的泡沫箱
	 * @param wayList
	 * @return
	 */
	public int deleteOldPmx(Map<String,Object> condition);
	
	/**
	 * 删除运单号对应的纸箱
	 * @param wayList
	 * @return
	 */
	public int deleteOldZx(Map<String,Object> condition);
	
	/**
	 * 删除运单号对应的保温袋
	 * @param wayList
	 * @return
	 */
	public int deleteOldBwd(Map<String,Object> condition);
	
	
	public List<String> queryFeeNo(Map<String,Object> condition);
	
	public List<BmsAsynCalcuTaskEntity> queryTask(Map<String, Object> condition);

	/**
	 * 作废使用标准包装方案运单的耗材 或者 将作废的耗材状态重置（del_flag从2变成0）
	 * 
	 * @author wangchen870
	 * @date 2019年4月19日 下午1:51:23
	 *
	 * @param list
	 * @return
	 */
    int deleteOrRevertMaterialStatus(List<BizOutstockPackmaterialEntity> list);

    /**
     * 通过运单号查询
     * <功能描述>
     * 
     * @author wangchen870
     * @date 2019年5月20日 上午11:51:01
     *
     * @param list
     * @return
     */
    List<BizOutstockPackmaterialEntity> queryByWaybillNo(List<String> list);

    /**
     * 作废费用
     * <功能描述>
     * 
     * @author wangchen870
     * @date 2019年5月20日 下午5:58:02
     *
     * @param list
     * @return
     */
    int delFees(List<BizOutstockPackmaterialEntity> list);

    int cancalCustomerBiz(Map<String,Object> map);

    int restoreCustomerBiz(Map<String,Object> map);
    
    /**
     * 根据运单号或者转寄后运单后得到其实际对应得运单号
     * <功能描述>
     * 
     * @author zhaofeng
     * @date 2019年7月4日 下午2:54:46
     *
     * @param condition
     * @return
     */
    String getWayBillNo(Map<String,Object> condition);
    
    List<FeesReceiveMaterial> queryMaterialSellData(
            Map<String, Object> condition);
    
    BizOutstockPackmaterialEntity queryOne(Map<String,Object> condition);
}
