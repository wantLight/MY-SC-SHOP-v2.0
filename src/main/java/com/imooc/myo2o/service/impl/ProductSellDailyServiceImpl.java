package com.imooc.myo2o.service.impl;

import com.imooc.myo2o.dao.ProductSellDaily;
import com.imooc.myo2o.service.ProductSellDailyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xyzzg on 2018/8/3.
 */
@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {

    private static final Logger log = LoggerFactory.getLogger(ProductSellDailyServiceImpl.class);
    @Autowired
    private ProductSellDaily productSellDaily;

    @Override
    public void dailyCalculate() {
        //这里需要使用Quartz
        log.info("Quartz Running");
        productSellDaily.insertProductSellDaily();
       // System.out.print("Quartz Running");
    }
}
