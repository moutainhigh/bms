package com.jiuyescm.bms.biz.pallet.service;

import java.util.Map;
import java.util.List;
import com.github.pagehelper.PageInfo;
import com.jiuyescm.bms.biz.pallet.entity.BizPalletInfoEntity;

/**
 * ..Service
 * @author wangchen
 * 
 */
public interface IBizPalletInfoService {
	
    PageInfo<BizPalletInfoEntity> query(Map<String, Object> condition, int pageNo,
            int pageSize);

	List<BizPalletInfoEntity> query(Map<String, Object> condition);

    BizPalletInfoEntity save(BizPalletInfoEntity entity);

    BizPalletInfoEntity update(BizPalletInfoEntity entity);
    
	int delete(List<BizPalletInfoEntity> list);
	
	/**
	 * 分组统计
	 * @param condition
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	PageInfo<BizPalletInfoEntity> groupCount(Map<String, Object> condition, int pageNo, int pageSize);
	
	/**
	 * 重算
	 * @param list
	 * @return
	 */
	int reCalculate(List<BizPalletInfoEntity> list);
	
	/**
	 * 批量更新
	 * @param list
	 * @return
	 */
	int updateBatch(List<Map<String, Object>> list);


}
