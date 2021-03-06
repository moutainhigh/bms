package com.jiuyescm.bms.receivable.storage.service;

import java.util.List;
import java.util.Map;



import com.jiuyescm.bms.biz.storage.entity.BizInStockDetailEntity;
import com.jiuyescm.bms.biz.storage.entity.BizInStockMasterEntity;
import com.jiuyescm.bms.general.entity.FeesReceiveStorageEntity;

/**
 * 
 * @author cjw
 * 
 */
public interface IBizInstockMasterService {
	
	public List<BizInStockMasterEntity> getInStockMasterList(Map<String, Object> condition);
	
	public void updateInstock(BizInStockMasterEntity entity);
	
	public void updateInstockBatch(List<BizInStockMasterEntity> entities);
	
	List<BizInStockDetailEntity> getDetailByMaster(String instockNo);
	
	//************** 入库操作费 **************
	public List<BizInStockMasterEntity> getInStockWorkList(Map<String, Object> condition);
	public void updateInstockWork(BizInStockMasterEntity entity);
	public void updateInstockWorkBatch(List<BizInStockMasterEntity> entities);
	
	/**
	 * 根据费用表数据更新业务表
	 * @param entities
	 */
	void updateInstockBatchByFees(List<FeesReceiveStorageEntity> entities);
	
	
}
