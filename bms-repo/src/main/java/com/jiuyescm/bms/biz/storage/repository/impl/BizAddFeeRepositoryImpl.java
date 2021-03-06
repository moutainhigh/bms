/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuyescm.bms.biz.storage.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.PageInfo;
import com.jiuyescm.cfm.persistence.mybatis.MyBatisDao;
import com.jiuyescm.bms.asyn.entity.BmsAsynCalcuTaskEntity;
import com.jiuyescm.bms.base.dictionary.entity.SystemCodeEntity;
import com.jiuyescm.bms.base.dictionary.repository.ISystemCodeRepository;
import com.jiuyescm.bms.biz.storage.entity.BizAddFeeEntity;
import com.jiuyescm.bms.biz.storage.repository.IBizAddFeeRepository;
import com.jiuyescm.bms.fees.storage.entity.FeesReceiveStorageEntity;

/**
 * 
 * @author stevenl
 * 
 */
@Repository("bizAddFeeRepository")
public class BizAddFeeRepositoryImpl extends MyBatisDao implements IBizAddFeeRepository {
    
    @Autowired
    private ISystemCodeRepository systemCodeRepository;  

	private static final Logger logger = Logger.getLogger(BizAddFeeRepositoryImpl.class.getName());

	public BizAddFeeRepositoryImpl(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public PageInfo<BizAddFeeEntity> query(Map<String, Object> condition, int pageNo, int pageSize) {
        List<BizAddFeeEntity> list = selectList("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.query", condition, new RowBounds(
                pageNo, pageSize));
        PageInfo<BizAddFeeEntity> pageInfo = new PageInfo<BizAddFeeEntity>(list);
        return pageInfo;
    }

    @Override
    public BizAddFeeEntity findById(Long id) {
        BizAddFeeEntity entity = (BizAddFeeEntity) selectOne("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.findById", id);
        return entity;
    }

    @SuppressWarnings("unchecked")
	@Override
    public BizAddFeeEntity save(BizAddFeeEntity entity) {
        insert("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.save", entity);
        return entity;
    }

    @SuppressWarnings("unchecked")
	@Override
    public BizAddFeeEntity update(BizAddFeeEntity entity) {
        update("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.update", entity);
        return entity;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void delete(BizAddFeeEntity entity) {
    	update("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.update", entity);
    }

	@SuppressWarnings("unchecked")
	@Override
	public int saveList(List<BizAddFeeEntity> addList) {
		return insertBatch("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.save",addList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int updateList(List<BizAddFeeEntity> updateList) {
		try {
			return this.updateBatch("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.update",updateList);
		} catch (Exception ex) {
			logger.error("批量更新主表异常"+ex.getMessage());
		}	
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int updateByMap(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		try {
			return update("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.updateByMap",condition);
		} catch (Exception ex) {
			logger.error("批量更新主表异常"+ex.getMessage());
		}	
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BizAddFeeEntity selectOne(Map<String, Object> param) {
		return (BizAddFeeEntity)selectOne("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.selectOne",param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BizAddFeeEntity> queryList(Map<String, Object> condition) {
		return selectList("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.queryRecount",condition);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BizAddFeeEntity queryWms(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return (BizAddFeeEntity) selectOne("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.queryWms", param);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BizAddFeeEntity> querybizAddFee(Map<String, Object> condition) {
		return selectList("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.querybizAddFee",condition);
	}
	
    /**
	 * 分组统计
	 * @param page
	 * @param param
	 */
	@SuppressWarnings("unchecked")
	@Override
    public PageInfo<BizAddFeeEntity> groupCount(Map<String, Object> condition, int pageNo, int pageSize){
		List<BizAddFeeEntity> list = selectList("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.groupCount", condition, new RowBounds(
                pageNo, pageSize));
		return new PageInfo<BizAddFeeEntity>(list);
	}

	   /**
     * 重算(新)
     * @param param
     * @return
     */
	@SuppressWarnings("unchecked")
	@Override
	public int retryCalcu(Map<String, Object> condition) {
		try {
		    List<String> codeList = new ArrayList<String>();;
	        List<SystemCodeEntity> systemCodeList = systemCodeRepository.findEnumList("NO_CALCULATE_STORAGE_ADD");    
	        for (SystemCodeEntity systemCodeEntity : systemCodeList) {
	            if (null == systemCodeEntity.getCode()) {
	                continue;
	            }            
	            codeList.add(systemCodeEntity.getCode());
	        }
	        condition.put("subjectList", codeList);
			update("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.retryCalcu",condition);
			return 1;
		} catch (Exception ex) {
			logger.error("批量更新主表异常"+ex.getMessage());
			return 0;
		}	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BmsAsynCalcuTaskEntity> queryTask(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return this.selectList("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.queryTask", condition);

	}

    @SuppressWarnings("unchecked")
    @Override
    public int omssave(List<BizAddFeeEntity> addList) {
        return insertBatch("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.omssave",addList);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public BizAddFeeEntity queryPayNo(Map<String, Object> param) {
        return (BizAddFeeEntity) selectOne("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.queryPayNo", param);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public int feesave(List<FeesReceiveStorageEntity> feeList) {
        return insertBatch("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.feesave",feeList);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int cancalCustomerBiz(Map<String, Object> map) {
        // TODO Auto-generated method stub
        return update("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.cancalCustomerBiz", map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int restoreCustomerBiz(Map<String, Object> map) {
        // TODO Auto-generated method stub
        return update("com.jiuyescm.bms.biz.storage.BizAddFeeEntityMapper.restoreCustomerBiz", map);
    }
}
