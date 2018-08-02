package com.imooc.myo2o.service.impl;

import com.imooc.myo2o.cache.JedisUtil;
import com.imooc.myo2o.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * Created by xyzzg on 2018/7/27.
 */
public class CacheServiceImpl implements CacheService{

    @Autowired
    private JedisUtil.Keys jedisKeys;

    @Override
    public void removeFromCache(String keyPrefix) {
        //键值对删除
        Set<String> keySet = jedisKeys.keys(keyPrefix + "*");
        for (String key : keySet){
            jedisKeys.del(key);
        }
    }
}
