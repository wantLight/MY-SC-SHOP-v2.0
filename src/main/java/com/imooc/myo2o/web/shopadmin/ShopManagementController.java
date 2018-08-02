package com.imooc.myo2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.ShopCategory;
import com.imooc.myo2o.enums.ShopStateEnum;
import com.imooc.myo2o.service.AreaService;
import com.imooc.myo2o.service.ShopCatetoryService;
import com.imooc.myo2o.service.ShopService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;
import com.imooc.myo2o.vo.ImageHolder;
import com.imooc.myo2o.vo.ShopExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xyzzg on 2018/7/12.
 */
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopCatetoryService shopCatetoryService;
    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/getShopManageInfo",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopManageInfo(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap <>();
        long shopId = HttpServletRequestUtil.getLong(request,"shopId");
        if (shopId <= 0){
            Object currentShopObj = request.getSession().getAttribute("currentShop");
            if (currentShopObj == null){
                modelMap.put("redirect",true);
                modelMap.put("url","/myo2o/shop/shoplist");
            } else {
                Shop currentShop = (Shop) currentShopObj;
                modelMap.put("redirect",false);
                modelMap.put("shopId",currentShop.getShopId());
            }
        } else {
            Shop currentShop = new Shop();
            currentShop.setShopId(shopId);
            request.getSession().setAttribute("currentShop",currentShop);
            modelMap.put("redirect",false);
        }
        return modelMap;
    }

    @RequestMapping(value = "/getShopList",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopList(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap <>();
        PersonInfo user = new PersonInfo();
        user.setUserId(1L);
        request.getSession().setAttribute("user",user);
        user = (PersonInfo) request.getSession().getAttribute("user");
        try{
            Shop shop = new Shop();
            shop.setOwnerId(user.getUserId());
            ShopExecution shopExecution = shopService.getShopList(shop,0,100);
            modelMap.put("shopList",shopExecution.getShopList());
            //列出店铺列表后，直接将店铺放入session中作为权限验证依赖
            request.getSession().setAttribute("shopList",shopExecution.getShopList());
            modelMap.put("user",user);
            modelMap.put("success",true);
        } catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errorMsg",e.getMessage());
        }
        return modelMap;
    }

    @RequestMapping(value = "/getShopById",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopById(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap <>();
        Long shopId = HttpServletRequestUtil.getLong(request,"shopId");
        if (shopId > -1 ){
            Shop shop = shopService.getByShopId(shopId);
            List<Area> areaList = areaService.getAreaList();
            modelMap.put("shop",shop);
            modelMap.put("areaList",areaList);
            modelMap.put("success",true);
        } else{
            modelMap.put("success",false);
            modelMap.put("errMsg","empty shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/getshopinitInfo",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> getShopInitInfo(){
        Map<String,Object> modelMap = new HashMap <>();
        List<ShopCategory> shopCategoryList = new ArrayList <>();
        List<Area> areaList = new ArrayList <>();
        try {
            //获取全部列表
            shopCategoryList = shopCatetoryService.getShopCategory(new ShopCategory());
            areaList = areaService.getAreaList();
            modelMap.put("shopCategoryList",shopCategoryList);
            modelMap.put("areaList",areaList);
            modelMap.put("success",true);
        } catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
        }
        return modelMap;
    }

    @RequestMapping(value = "/registershop",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> registerShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        if (!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入错误的验证码");
            return modelMap;
        }
        //1.接受并转化相应的参数，包括店铺信息以及图片信息 --使用工具类
        String shopStr = HttpServletRequestUtil.getString(request,"shopStr");

        ObjectMapper mapper = new ObjectMapper();
        Shop shop =null;
        try {
            //转换成Java对象匹配JSON结构
            shop = mapper.readValue(shopStr,Shop.class);
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
            return modelMap;
        }
        //接收图片
        CommonsMultipartFile shopImg = null;
        //从上下文中获取内容
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        //是不是有上传的文件流
        if (commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
        } else {
            modelMap.put("success",false);
            modelMap.put("errMsg","上传图片不得为空");
            return modelMap;
        }
        //2.注册店铺
        if (shop != null && shopImg != null){
            //一定要假定前端信息不靠谱,从Session取
            PersonInfo owner = (PersonInfo) request.getSession().getAttribute("user");
            shop.setOwnerId(owner.getUserId());
            //File shopImgFile = new File(FileUtil.getImgBasePath()+ FileUtil.getRandomFileName());
            /*try {
                shopImgFile.createNewFile();
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
                return modelMap;
            }
            try {
                inputStreamToFile(shopImg.getInputStream(),shopImgFile);
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
                return modelMap;
            }*/
            ShopExecution shopExecution;
            try {
                ImageHolder imageHolder = new ImageHolder(shopImg.getName(),shopImg.getInputStream());
                shopExecution = shopService.addShop(shop,imageHolder);
                if (shopExecution.getState() == ShopStateEnum.CHECK.getState()){
                    modelMap.put("success",true);
                    //该用户可以操作的店铺列表
                    List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
                    if(shopList == null || shopList.size() == 0) {
                        shopList = new ArrayList <>();
                    }
                    shopList.add(shopExecution.getShop());
                    request.getSession().setAttribute("shopList",shopList);
                } else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",shopExecution.getStateInfo());
                }
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }
            return modelMap;
        } else{
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺信息");
            return modelMap;
        }
    }

    @RequestMapping(value = "/modifyshop",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> modifyshop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        if (!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入错误的验证码");
            return modelMap;
        }
        //1.接受并转化相应的参数，包括店铺信息以及图片信息 --使用工具类
        String shopStr = HttpServletRequestUtil.getString(request,"shopStr");

        ObjectMapper mapper = new ObjectMapper();
        Shop shop =null;
        try {
            //转换成Java对象匹配JSON结构
            shop = mapper.readValue(shopStr,Shop.class);
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
            return modelMap;
        }
        //接收图片
        CommonsMultipartFile shopImg = null;
        //从上下文中获取内容
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        //是不是有上传的文件流
        if (commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
        }
        //2.修改店铺
        if (shop != null && shop.getShopId() != null){
            ShopExecution shopExecution = null;
            try {
                ImageHolder imageHolder = new ImageHolder(shopImg.getName(),shopImg.getInputStream());
                if (shopImg == null){
                    shopExecution = shopService.modifyShop(shop,imageHolder);
                }else{
                    shopExecution = shopService.modifyShop(shop,imageHolder);
                }
                if (shopExecution.getState() == ShopStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                } else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",shopExecution.getStateInfo());
                }
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }
            return modelMap;
        } else{
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺ID");
            return modelMap;
        }
    }

    /*private static void inputStreamToFile(InputStream inputStream,File file){
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            int bytes = 0;
            byte[] buffer = new byte[1024];
            while ((bytes = inputStream.read(buffer)) != -1){
                //读满1024个字节就往输出流写入
                outputStream.write(buffer,0,bytes);
            }
        } catch (Exception e){
            throw new RuntimeException("调用inputStreamToFile产生异常"+e.getMessage());
        } finally {
            try {
                if (outputStream != null){
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e){
                throw new RuntimeException("调用inputStreamToFile关闭IO产生异常"+e.getMessage());
            }
        }
    }*/
}
