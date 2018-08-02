package com.imooc.myo2o.web.shopadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by xyzzg on 2018/7/12.
 */
@Controller
@RequestMapping(value = "/shopadmin",method = RequestMethod.GET)
public class ShopAdminController {

    @RequestMapping(value = "/shopoperation",method = RequestMethod.POST)
    public String shopOperation(){
        return "shop/shopoperation";
    }

    @RequestMapping(value = "/shoplist",method = RequestMethod.GET)
    public String shoplist(){
        return "shop/shoplist";
    }

    @RequestMapping(value = "/shopmanagement",method = RequestMethod.POST)
    public String shopmanagement(){
        return "shop/shopmanagement";
    }

    @RequestMapping(value = "/productoperation",method = RequestMethod.POST)
    public String productOperation(){
        return "shop/productoperation";
    }

    @RequestMapping(value = "/productomanagement",method = RequestMethod.POST)
    public String productomanagementOperation(){
        return "shop/productomanagement";
    }


}
