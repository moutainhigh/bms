package com.jiuyescm.bms.correct.service;

import java.util.List;
import java.util.Map;

import com.jiuyescm.bms.biz.storage.entity.BizOutstockPackmaterialEntity;
import com.jiuyescm.bms.correct.vo.BmsMarkingMaterialVo;
import com.jiuyescm.bms.correct.vo.BmsProductsMaterialAccountVo;

public interface IBmsProductsMaterialService {
	
	//查询出所有占比最高的
	List<BmsProductsMaterialAccountVo> queyAllMax(Map<String,Object> condition);
	
	//查询出所有占比最高的保温袋
	List<BmsProductsMaterialAccountVo> queyAllBwxMax(Map<String,Object> condition);

	//统计占比
	List<BmsProductsMaterialAccountVo> queyMaterialCount(Map<String,Object> condition);
	
	//获取最多使用的保温袋
	BizOutstockPackmaterialEntity queryBwdMaterial(Map<String,Object> condition);
	
	//保存统计数据
	int saveList(List<BmsProductsMaterialAccountVo> list);
	
	//插入耗材统计表
	int saveMaterial(Map<String,Object> condition);
	
	//插入保温袋统计表
	int saveBwd(Map<String,Object> condition);
	
	//查询出所有占比不是最高的
	List<BmsMarkingMaterialVo> queyNotMax(Map<String,Object> condition);
	
	//查询出所有占比不是最高的泡沫箱纸箱
	List<BizOutstockPackmaterialEntity> queyNotMaxMaterial(Map<String,Object> condition);
	
	//查询出所有占比不是最高的保温袋
	List<BizOutstockPackmaterialEntity> queyNotMaxBwd(Map<String,Object> condition);
	
	//根据商品明细查询出所有的运单
	List<BmsMarkingMaterialVo> queryMark(Map<String,Object> condition);
	
	BmsMarkingMaterialVo queryOneMark(Map<String,Object> condition);
	
	//批量更新打标记录
	int updateMarkList(List<BmsMarkingMaterialVo> list);
	
	//根据条件查询打标记录
	BmsMarkingMaterialVo queryMarkVo(Map<String,Object> condition);
	
	//根据批次号查询运单中耗材明细，打标
	int markMaterial(Map<String,Object> condition);
	
	//根据批次号保存标对应得耗材明细
	int saveMarkMaterial(Map<String,Object> condition);
	
	//打标保温袋
	int markBwd(Map<String,Object> condition);
	
	//根据批次号保存标对应得保温袋明细
	int saveMarkBwd(Map<String,Object> condition);

	int deleteMarkMaterialByWaybillNo(List<String> waybillNoList);
	
	//更新汇总表
	int updateMaterialAccount(Map<String,Object> condition);
	
	//根据耗材标得到对应得耗材编码
	Map<String,String> getMaterialMap(Map<String,Object> condition);
}
