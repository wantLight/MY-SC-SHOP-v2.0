package com.imooc.myo2o.service.impl;

import com.imooc.myo2o.dao.ShopDao;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.enums.ShopStateEnum;
import com.imooc.myo2o.exception.ShopOperationException;
import com.imooc.myo2o.service.ShopService;
import com.imooc.myo2o.util.FileUtil;
import com.imooc.myo2o.util.ImageUtil;
import com.imooc.myo2o.util.PageCalculator;
import com.imooc.myo2o.vo.ImageHolder;
import com.imooc.myo2o.vo.ShopExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by xyzzg on 2018/7/10.
 */
@Service("shopService")
public class ShopServiceImpl implements ShopService{

    @Autowired
    private ShopDao shopDao;

    @Override
    public ShopExecution getShopList(Shop shop,int pageIndex,int pageSize) {
        //将pageIndex转换成rowIndex
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex,pageSize);
        List<Shop> shopList = shopDao.queryShopList(shop, rowIndex, pageSize);
        int count = shopDao.queryShopCount(shop);
        ShopExecution shopExecution = new ShopExecution();
        if (shopList != null){
            shopExecution.setShopList(shopList);
            shopExecution.setCount(count);
        } else {
            shopExecution.setState(ShopStateEnum.INNER_ERROR.getState());
        }
        return shopExecution;
    }

    @Override
    public ShopExecution addShop(Shop shop,ImageHolder thumbnail) {
        //各种非空逻辑判断
        if (shop == null){
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }
        try{
            //赋予初始值
            shop.setEnableStatus(0);
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            //添加店铺信息
            int effectedNum = shopDao.insertShop(shop);
            if (effectedNum <=0 ){
                //只有RuntimeException，事务中止并回滚
                throw new ShopOperationException("店铺创建失败");
            } else{
                if (thumbnail != null){
                    try {
                        addShopImg(shop,thumbnail.getImage(),thumbnail.getImageName());
                    } catch (Exception e){
                        throw new ShopOperationException("addShopImg error:"+e.getMessage());
                    }
                    //跟新店铺图片地址
                    effectedNum = shopDao.updateShop(shop);
                    if (effectedNum <= 0){
                        throw new ShopOperationException("跟新图片地址失败");
                    }
                }
            }
        } catch (Exception e){
            throw new ShopOperationException("addShop error:"+e.getMessage());
        }
        return new ShopExecution(ShopStateEnum.CHECK,shop);
    }

    private void addShopImg(Shop shop,InputStream shopImgInputStream,String fileName) {
        //获取shop图片目录相对子路径
        String dest = FileUtil.getShopImagePath(shop.getShopId());
        String shopImgAddr = ImageUtil.generateThumbnail(shopImgInputStream,fileName,dest);
        shop.setShopImg(shopImgAddr);
    }
    @Override
    public Shop getByShopId(long shopId) {
        return shopDao.queryByShopId(shopId);
    }

    @Override
    public ShopExecution modifyShop(Shop shop,ImageHolder thumbnail)
            throws ShopOperationException {
        if(shop == null || shop.getShopId() == null){
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        } else{
            //1.判断是否需要处理图片
            if(thumbnail.getImage() != null){
                Shop tempShop = shopDao.queryByShopId(shop.getShopId());
                if (tempShop.getShopImg() != null){
                    FileUtil.deleteFile(tempShop.getShopImg());
                }
                addShopImg(shop, thumbnail.getImage(), thumbnail.getImageName());
            }
            //2.更新店铺信息
            shop.setLastEditTime(new Date());
            int effectedNum = shopDao.updateShop(shop);
            if (effectedNum <= 0){
                return new ShopExecution(ShopStateEnum.INNER_ERROR);
            } else {
                //获取更改后的信息
                shop = shopDao.queryByShopId(shop.getShopId());
                return new ShopExecution(ShopStateEnum.SUCCESS,shop);
            }
        }
    }


}
