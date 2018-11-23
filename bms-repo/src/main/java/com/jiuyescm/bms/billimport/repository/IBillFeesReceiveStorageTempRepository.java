package com.jiuyescm.bms.billimport.repository;

import java.util.Map;
import java.util.List;
import com.github.pagehelper.PageInfo;
import com.jiuyescm.bms.billimport.entity.BillFeesReceiveStorageTempEntity;

/**
 * ..Repository
 * @author wangchen
 * 
 */
public interface IBillFeesReceiveStorageTempRepository {

    BillFeesReceiveStorageTempEntity findById(Long id);
	
	PageInfo<BillFeesReceiveStorageTempEntity> query(Map<String, Object> condition,
		int pageNo, int pageSize);

	List<BillFeesReceiveStorageTempEntity> query(Map<String, Object> condition);

    BillFeesReceiveStorageTempEntity save(BillFeesReceiveStorageTempEntity entity);

    BillFeesReceiveStorageTempEntity update(BillFeesReceiveStorageTempEntity entity);

    void delete(Long id);
    
    /**
     * 批量写入
     * @param list
     * @return
     * @throws Exception
     */
	int insertBatch(List<BillFeesReceiveStorageTempEntity> list) throws Exception;
	
	/**
	 * 批量删除
	 * @param condition
	 * @return
	 */
	int deleteBatch(Map<String, Object> condition);

}