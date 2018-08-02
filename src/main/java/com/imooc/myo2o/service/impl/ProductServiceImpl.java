package com.imooc.myo2o.service.impl;

import com.imooc.myo2o.dao.ProductDao;
import com.imooc.myo2o.dao.ProductImgDao;
import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductImg;
import com.imooc.myo2o.enums.ProductStateEnum;
import com.imooc.myo2o.service.ProductService;
import com.imooc.myo2o.util.FileUtil;
import com.imooc.myo2o.util.ImageUtil;
import com.imooc.myo2o.util.PageCalculator;
import com.imooc.myo2o.vo.ImageHolder;
import com.imooc.myo2o.vo.ProductExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xyzzg on 2018/7/25.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductImgDao productImgDao;

    @Override
    public ProductExecution getProductList(Product productCondition,int pageIndex,int pageSize) {
        //页码转换成数据库的行码，并调用dao层取回指定页码的商品列表
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);
        //得到商品总数
        int count = productDao.queryProductCount(productCondition);
        ProductExecution pe = new ProductExecution();
        pe.setProductList(productList);
        pe.setCount(count);
        return pe;
    }

    @Override
    public Product getProductById(long productId) {
        return productDao.queryProductByProductId(productId);
    }

    //@Transactional 事务管理的目的 在出现异常的情况下,保证数据的一致性
    @Override
    @Transactional
    /**
     * 1.处理缩略图，获取路径并赋值给product
     * 2.往tb_product写入商品信息，获取productID
     * 3.结合productID批量处理商品详情图
     * 4.批量插入到tb_product_img中
     */
    public ProductExecution addProduct(Product product,ImageHolder thumbnail,List <ImageHolder> productImgs) throws RuntimeException {
        if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
            //给商品设置上默认属性
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            product.setEnableStatus(1);
            if (thumbnail != null) {
                //添加缩略图
                addThumbnail(product, thumbnail);
            }
            try {
                //创建商品信息
                int effectedNum = productDao.insertProduct(product);
                if (effectedNum <= 0) {
                    throw new RuntimeException("创建商品失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("创建商品失败:" + e.toString());
            }
            if (productImgs != null && productImgs.size() > 0) {
                addProductImgs(product, productImgs);
            }
            return new ProductExecution(ProductStateEnum.SUCCESS, product);
        } else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    /**
     *1.若缩略图参数有值，处理缩略图（先删除）
     * 2.详情图参数列表有值，对商品详情图片列表进行同样的操作
     * 3.删除tb_product_img下面的该商品原有的商品详情图记录
     * 4.更新tb_product，tb_product_img
     */
    @Override
    @Transactional
    public ProductExecution modifyProduct(Product product,ImageHolder thumbnail,List <ImageHolder> productImgs) throws RuntimeException {
        if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
            product.setLastEditTime(new Date());
            if (thumbnail != null) {
                Product tempProduct = productDao.queryProductByProductId(product.getProductId());
                if (tempProduct.getImgAddr() != null) {
                    //先删除再添加新图
                    FileUtil.deleteFile(tempProduct.getImgAddr());
                }
                addThumbnail(product, thumbnail);
            }
            if (productImgs != null && productImgs.size() > 0) {
                deleteProductImgs(product.getProductId());
                addProductImgs(product, productImgs);
            }
            try {
                int effectedNum = productDao.updateProduct(product);
                if (effectedNum <= 0) {
                    throw new RuntimeException("更新商品信息失败");
                }
                return new ProductExecution(ProductStateEnum.SUCCESS, product);
            } catch (Exception e) {
                throw new RuntimeException("更新商品信息失败:" + e.toString());
            }
        } else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    private void addProductImgs(Product product,List<ImageHolder> productImgs) {
        String dest = FileUtil.getShopImagePath(product.getShop().getShopId());
        List<ProductImg> productImgList = new ArrayList <>();
        //遍历图片一次去处理，并添加进List<ProductImg>里
        for (ImageHolder imageHolder : productImgs){
            String imgAddr = ImageUtil.generateNormalImg(imageHolder.getImage(),imageHolder.getImageName(),dest);
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setProductId(product.getProductId());
            productImg.setCreateTime(new Date());
            productImgList.add(productImg);
        }
        //确实有图片需要添加，执行批量添加操作
        if (productImgList != null && productImgList.size() > 0) {
            try {
                int effectedNum = productImgDao.batchInsertProductImg(productImgList);
                if (effectedNum <= 0) {
                    throw new RuntimeException("创建商品详情图片失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("创建商品详情图片失败:" + e.toString());
            }
        }
    }

    private void deleteProductImgs(long productId) {
        List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
        for (ProductImg productImg : productImgList) {
            FileUtil.deleteFile(productImg.getImgAddr());
        }
        productImgDao.deleteProductImgByProductId(productId);
    }

    //添加缩略图
    private void addThumbnail(Product product,ImageHolder thumbnail) {
        String dest = FileUtil.getShopImagePath(product.getShop().getShopId());
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail.getImage(),thumbnail.getImageName(), dest);
        product.setImgAddr(thumbnailAddr);
    }
}
