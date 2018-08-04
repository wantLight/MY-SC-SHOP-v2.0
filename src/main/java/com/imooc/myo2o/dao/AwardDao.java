package com.imooc.myo2o.dao;

import com.imooc.myo2o.entity.Award;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AwardDao {
	//依据传入进来的条件，分页显示
	List<Award> queryAwardList(@Param("awardCondition") Award awardCondition,
                               @Param("rowIndex") int rowIndex,@Param("pageSize") int pageSize);

	//返回上面的奖品数
	int queryAwardCount(@Param("awardCondition") Award awardCondition);

	Award queryAwardByAwardId(long awardId);

	int insertAward(Award award);

	int updateAward(Award award);

	int deleteAward(long awardId);
}
