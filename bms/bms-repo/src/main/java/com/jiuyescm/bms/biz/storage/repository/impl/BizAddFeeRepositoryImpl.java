/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuyescm.bms.biz.storage.repository.impl;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.PageInfo;
import com.jiuyescm.cfm.persistence.mybatis.MyBatisDao;
import com.jiuyescm.bms.biz.storage.entity.BizAddFeeEntity;
import com.jiuyescm.bms.biz.storage.repository.IBizAddFeeRepository;

/**
 * 
 * @author stevenl
 * 
 */
@Repository("bizAddFeeRepository")
public class BizAddFeeRepositoryImpl extends MyBatisDao<BizAddFeeEntity> implements IBizAddFeeRepository {

	private static final Logger logger = Logger.getLogger(BizAddFeeRepositoryImpl.class.getName());

	public BizAddFeeRepositoryImpl() {
		super();
	}
	
	@Override
    public PageInfo<BizAddFeeEntity> query(Map<String, Object> condition, int pageNo, int pageSize) {
        List<BizAddFeeEntity> list = selectList("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.query", condition, new RowBounds(
                pageNo, pageSize));
        PageInfo<BizAddFeeEntity> pageInfo = new PageInfo<BizAddFeeEntity>(list);
        return pageInfo;
    }

    @Override
    public BizAddFeeEntity findById(Long id) {
        BizAddFeeEntity entity = selectOne("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.findById", id);
        return entity;
    }

    @Override
    public BizAddFeeEntity save(BizAddFeeEntity entity) {
        insert("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.save", entity);
        return entity;
    }

    @Override
    public BizAddFeeEntity update(BizAddFeeEntity entity) {
        update("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.update", entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        delete("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.delete", id);
    }
	
}
