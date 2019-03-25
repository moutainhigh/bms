package com.jiuyescm.bms.biz.storage.service;

import java.security.Timestamp;
import java.util.List;

import com.jiuyescm.bms.biz.storage.vo.StoBizPalletVo;
import com.jiuyescm.bms.biz.storage.vo.StoFeePalletVo;

/**
 * 托数计费服务
 * @author caojianwei
 *
 */
public interface StoPalletService {

	/**
	 * 查询未计算的入库数据 最多返回1000行
	 * @param customerId  商家ID
	 * @param subjectCode 科目编码
	 * @param startTime	      开始时间
	 * @param endTime     结束时间
	 * @return
	 */
	List<StoBizPalletVo> queryUnExeBiz(String customerId,String subjectCode,Timestamp startTime,Timestamp endTime);
	
	/**
	 * 更新费用
	 * @param vos 费用集合
	 */
	void updateFee(List<StoFeePalletVo> vos);
}
