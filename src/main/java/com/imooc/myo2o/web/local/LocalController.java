package com.imooc.myo2o.web.local;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by xyzzg on 2018/7/28.
 */
@Controller
@RequestMapping("/local")
public class LocalController {

    @RequestMapping(value = "/accountbind",method = RequestMethod.GET)
    private String accountbind(){
        return "local/accountbind";
    }

    @RequestMapping(value = "/changepsw",method = RequestMethod.GET)
    private String changepsw(){
        return "local/changepsw";
    }
}
