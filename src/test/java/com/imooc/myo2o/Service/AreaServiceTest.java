package com.imooc.myo2o.Service;


import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.service.AreaService;
import com.imooc.myo2o.service.CacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by xyzzg on 2018/7/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaServiceTest  {

    @Autowired
    private AreaService areaService;
    @Autowired
    private CacheService cacheService;
    @Test
    public void testGetAreaList(){
        List<Area> areaList = areaService.getAreaList();
        assertEquals("东苑",areaList.get(0).getAreaName());
        //cacheService.removeFromCache(areaService.AREALISTKEY);
    }
}
