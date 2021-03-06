/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuyescm.bms.report.month.service;

import java.util.List;
import java.util.Map;

import com.jiuyescm.bms.report.month.entity.ReportSellerSubjectProfitEntity;

/**
 * 
 * @author stevenl
 * 
 */
public interface IReportSellerSubjectProfitService {

	// 含总部管理费
	List<ReportSellerSubjectProfitEntity> queryAllCP(Map<String, Object> param);
	
	// 不含总部管理费
	List<ReportSellerSubjectProfitEntity> queryAll(Map<String, Object> param);

}
