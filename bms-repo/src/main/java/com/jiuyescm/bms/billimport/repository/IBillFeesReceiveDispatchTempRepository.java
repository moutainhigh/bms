/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuyescm.bms.billimport.repository;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.jiuyescm.bms.billimport.entity.BillFeesReceiveDispatchTempEntity;

/**
 * 
 * @author stevenl
 * 
 */
public interface IBillFeesReceiveDispatchTempRepository {

	public PageInfo<BillFeesReceiveDispatchTempEntity> query(Map<String, Object> condition,
            int pageNo, int pageSize);

    public BillFeesReceiveDispatchTempEntity findById(Long id);

    public BillFeesReceiveDispatchTempEntity save(BillFeesReceiveDispatchTempEntity entity);

    public BillFeesReceiveDispatchTempEntity update(BillFeesReceiveDispatchTempEntity entity);

    public void delete(Long id);

}
