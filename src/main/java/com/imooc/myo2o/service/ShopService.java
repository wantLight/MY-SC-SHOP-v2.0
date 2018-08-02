package com.imooc.myo2o.service;

import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.exception.ShopOperationException;
import com.imooc.myo2o.vo.ImageHolder;
import com.imooc.myo2o.vo.ShopExecution;

/**
 * Created by xyzzg on 2018/7/10.
 */
public interface ShopService {

    /**
     * 根据shop分页返回相应店铺列表
     * @param shop
     * @param pageIndex
     * @param pageSize
     * @return
     */
    ShopExecution getShopList(Shop shop,int pageIndex,int pageSize);

    //使用File不合理
    ShopExecution addShop(Shop shop,ImageHolder thumbnail) throws ShopOperationException;

    /**
     * 查询指定店铺信息
     *
     * @param shopId
     * @return Shop shop
     */
    Shop getByShopId(long shopId);

    /**
     * 更新店铺信息（从店家角度）
     */
    ShopExecution modifyShop(Shop shop,ImageHolder thumbnail) throws ShopOperationException;
}
