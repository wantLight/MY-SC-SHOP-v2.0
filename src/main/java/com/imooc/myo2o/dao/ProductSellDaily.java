package com.imooc.myo2o.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by xyzzg on 2018/8/3.
 */
public interface ProductSellDaily {

    List<ProductSellDaily> quaryProductSellDailyList(
            @Param("productSellDailyCondition") ProductSellDaily productSellDaily,
            @Param("beginTime")Date beginTime,@Param("endTime") Date endTime
    );

    int insertProductSellDaily();
}
