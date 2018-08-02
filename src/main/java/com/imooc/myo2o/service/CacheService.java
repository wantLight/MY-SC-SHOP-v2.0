package com.imooc.myo2o.service;

/**
 * Created by xyzzg on 2018/7/27.
 */
public interface CacheService {

    /**
     * 依据key前缀删除匹配该模式下的所有key-value
     * @param keyPrefix
     */
    void removeFromCache(String keyPrefix);
}
