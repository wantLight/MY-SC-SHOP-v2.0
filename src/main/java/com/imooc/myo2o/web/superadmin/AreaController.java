package com.imooc.myo2o.web.superadmin;

import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xyzzg on 2018/7/9.
 */
@Controller
@RequestMapping("/superadmin")
public class AreaController {

    Logger logger = LoggerFactory.getLogger(AreaController.class);

    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/listarea",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listArea(){
        //记录日志
        logger.info("====start====");
        long startTime = System.currentTimeMillis();

        Map<String,Object> modelMap = new HashMap<String,Object>();
        List<Area> list = new ArrayList<Area>();
        try {
            list = areaService.getAreaList();
            modelMap.put("rows",list);
            modelMap.put("total",list.size());
        } catch (Exception e){
            e.printStackTrace();
            modelMap.put("success",false);
            modelMap.put("errmsg",e.toString());
        }

        logger.error("test error");
        long endTime = System.currentTimeMillis();
        //debug 负责调优
        logger.debug("costTime:[{}ms]",endTime - startTime);
        logger.info("===end===");
        return modelMap;
    }
}
