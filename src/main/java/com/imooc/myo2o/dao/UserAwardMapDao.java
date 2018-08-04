package com.imooc.myo2o.dao;

import com.imooc.myo2o.entity.UserAwardMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//顾客已领取奖品的映射
public interface UserAwardMapDao {
	/**
	 * 
	 * @param userAwardCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<UserAwardMap> queryUserAwardMapList(
            @Param("userAwardCondition") UserAwardMap userAwardCondition,
            @Param("rowIndex") int rowIndex,@Param("pageSize") int pageSize);

	/**
	 * 
	 * @param userAwardCondition
	 * @return
	 */
	int queryUserAwardMapCount(
            @Param("userAwardCondition") UserAwardMap userAwardCondition);

	/**
	 * 
	 * @param userAwardId
	 * @return
	 */
	UserAwardMap queryUserAwardMapById(long userAwardId);

	/**
	 * 添加一条奖品兑换信息
	 * @param userAwardMap
	 * @return
	 */
	int insertUserAwardMap(UserAwardMap userAwardMap);

	/**
	 * 
	 * @param userAwardMap
	 * @return
	 */
	int updateUserAwardMap(UserAwardMap userAwardMap);
}
