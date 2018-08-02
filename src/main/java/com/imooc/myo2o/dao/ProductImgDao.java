package com.imooc.myo2o.dao;

import com.imooc.myo2o.entity.ProductImg;

import java.util.List;

public interface ProductImgDao {

	List<ProductImg> queryProductImgList(long productId);

	/**
	 * 批量插入商品图片
	 * @param productImgList
	 * @return
	 */
	int batchInsertProductImg(List <ProductImg> productImgList);

	int deleteProductImgByProductId(long productId);
}
