package com.imooc.myo2o.web.local;

import com.imooc.myo2o.entity.LocalAuth;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.enums.LocalAuthStateEnum;
import com.imooc.myo2o.service.LocalAuthService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;
import com.imooc.myo2o.vo.LocalAuthExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xyzzg on 2018/7/28.
 */
@Controller
@RequestMapping(value = "/local",method = RequestMethod.GET)
public class LocalAuthController {

    @Autowired
    private LocalAuthService localAuthService;

    @RequestMapping(value = "/bindlocalauth",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> bindLocalAuth(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap <>();
        if ( !CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }
        String username = HttpServletRequestUtil.getString(request,"userName");
        String password = HttpServletRequestUtil.getString(request,"password");
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        //UserID一定也是非空的，否则无法进行绑定
        if ( username != null && password != null && user!=null && user.getUserId() != null){
            LocalAuth localAuth = new LocalAuth();
            localAuth.setUserName(username);
            localAuth.setPassword(password);
            localAuth.setPersonInfo(user);
            LocalAuthExecution localAuthExecution = localAuthService.bindLocalAuth(localAuth);
            if (localAuthExecution.getState() == LocalAuthStateEnum.SUCCESS.getState()){
                modelMap.put("success",true);
            } else {
                modelMap.put("success",false);
                modelMap.put("errMsg",localAuthExecution.getStateInfo());
            }
        } else{
            modelMap.put("success",false);
            modelMap.put("errMsg","用户名与密码不能为空");
        }
        return modelMap;
    }

    @RequestMapping(value = "/changelocalpwd",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> changeLocalPwd(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap <>();
        if ( !CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }
        String username = HttpServletRequestUtil.getString(request,"userName");
        String password = HttpServletRequestUtil.getString(request,"password");
        String newPassword = HttpServletRequestUtil.getString(request,"newPassword");
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        if ( username != null && password != null && user!=null && user.getUserId() != null
                && password != newPassword){
            LocalAuth localAuth = localAuthService.getLocalAuthByUserId(user.getUserId());
            if (localAuth == null || localAuth.getUserName().equals(username)){
                //不一致直接退出
                modelMap.put("success",false);
                modelMap.put("errMsg","输入账号非本次使用账号");
                return modelMap;
            }
            LocalAuthExecution localAuthExecution = localAuthService.modifyLocalAuth(user.getUserId(),username,password,newPassword);
            if (localAuthExecution.getState() == LocalAuthStateEnum.SUCCESS.getState()){
                modelMap.put("success",true);
            }else {
                modelMap.put("success",false);
                modelMap.put("errMsg",localAuthExecution.getStateInfo());
            }
        }
        return modelMap;
    }

    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> logout(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap <>();
        //将用户session滞空
        request.getSession().setAttribute("user",null);
        modelMap.put("success",true);
        return  modelMap;
    }
}
