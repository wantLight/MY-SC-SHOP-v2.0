package com.imooc.myo2o.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.cache.JedisUtil;
import com.imooc.myo2o.dao.AreaDao;
import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.exception.AreaOperationException;
import com.imooc.myo2o.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xyzzg on 2018/7/9.
 */
@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaDao areaDao;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;

    private static String AREALISTKEY = "arealist";
    private static Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    @Override
    @Transactional
    //经常访问，且较少更新，放入Redis
    public List<Area> getAreaList() {
        String key = AREALISTKEY;
        List<Area> areaList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        if (!jedisKeys.exists(key)){
            areaList = areaDao.queryArea();
            String jsonString = null;
            try {
                jsonString = objectMapper.writeValueAsString(areaList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
            jedisStrings.set(key,jsonString);
    } else {
            String jsonString = jedisStrings.get(key);
            //转换， 获取类型的创建工厂
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class,Area.class);
                try {
                    //（要转换的对象，转换成的对象）
                    areaList = objectMapper.readValue(jsonString,javaType);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
        }
        return areaList;
    }
}
