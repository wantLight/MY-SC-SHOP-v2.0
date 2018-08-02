package com.imooc.myo2o.dao;

import com.imooc.myo2o.entity.Area;

import java.util.List;

public interface AreaDao {
	/**
	 * 列出地域列表
	 * @param areaCondition1
	 * @return
	 */
	List<Area> queryArea();

	/**
	 * 
	 * @param area
	 * @return
	 */
	int insertArea(Area area);

	/**
	 * 
	 * @param area
	 * @return
	 */
	int updateArea(Area area);

	/**
	 * 
	 * @param areaId
	 * @return
	 */
	int deleteArea(long areaId);

	/**
	 * 
	 * @param areaIdList
	 * @return
	 */
	int batchDeleteArea(List <Long> areaIdList);
}
