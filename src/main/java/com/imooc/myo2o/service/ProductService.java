package com.imooc.myo2o.service;

import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.vo.ImageHolder;
import com.imooc.myo2o.vo.ProductExecution;

import java.util.List;

public interface ProductService {
	/**
	 * 查询商品列表并分页，可输入商品名，商品状态，店铺ID,商品类别
	 * @param productCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	ProductExecution getProductList(Product productCondition,int pageIndex,int pageSize);

	/**
	 * 通过商品ID查询唯一商品信息
	 * @param productId
	 * @return
	 */
	Product getProductById(long productId);

	/**
	 * 添加商品信息，以及图片处理
	 * @param product
	 * @param thumbnail
	 * @param productImgs
	 * @return
	 * @throws RuntimeException
	 */
	ProductExecution addProduct(Product product,ImageHolder thumbnail,
                                List <ImageHolder> productImgs)
			throws RuntimeException;

	/**
	 * 修改商品信息
	 * @param product
	 * @param thumbnail
	 * @param productImgs
	 * @return
	 * @throws RuntimeException
	 */
	ProductExecution modifyProduct(Product product,ImageHolder thumbnail,
                                   List <ImageHolder> productImgs) throws RuntimeException;
}
