package com.imooc.myo2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductCategory;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.enums.ProductStateEnum;
import com.imooc.myo2o.service.ProductCategoryService;
import com.imooc.myo2o.service.ProductService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;
import com.imooc.myo2o.vo.ImageHolder;
import com.imooc.myo2o.vo.ProductExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

@Controller
@RequestMapping("/shop")
public class ProductManagementController {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductCategoryService productCategoryService;

	//支持上传商品详情图的最大数量
	private static final int IMAGEMAXCOUNT = 6;

	@RequestMapping(value = "/listproductsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listProductsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
			int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
			int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
			Shop currentShop = (Shop) request.getSession().getAttribute(
					"currentShop");
			if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null)
					&& (currentShop.getShopId() != null)) {
				//获取需要检索的条件
				long productCategoryId = HttpServletRequestUtil.getLong(request,
					"productCategoryId");
			String productName = HttpServletRequestUtil.getString(request,
					"productName");
			Product productCondition = compactProductCondition4Search(
					currentShop.getShopId(), productCategoryId, productName);
			ProductExecution pe = productService.getProductList(
					productCondition, pageIndex, pageSize);
			modelMap.put("productList", pe.getProductList());
			modelMap.put("count", pe.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	@RequestMapping(value = "/getproductbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductById(@RequestParam Long productId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (productId > -1) {
			Product product = productService.getProductById(productId);
			List<ProductCategory> productCategoryList = productCategoryService
					.getByShopId(product.getShop().getShopId());
			modelMap.put("product", product);
			modelMap.put("productCategoryList", productCategoryList);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	@RequestMapping(value = "/getproductcategorylistbyshopId", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductCategoryListByShopId(
			HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop currentShop = (Shop) request.getSession().getAttribute(
				"currentShop");
		if ((currentShop != null) && (currentShop.getShopId() != null)) {
			List<ProductCategory> productCategoryList = productCategoryService
					.getByShopId(currentShop.getShopId());
			modelMap.put("productCategoryList", productCategoryList);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	@RequestMapping(value = "/addproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addProduct(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//接收前端参数变量的初始化
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		String productStr = HttpServletRequestUtil.getString(request,
				"productStr");
		MultipartHttpServletRequest multipartRequest = null;
		//CommonsMultipartFile thumbnail = null;
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgs = new ArrayList<ImageHolder>();
		//获取文件流
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		try {
			if (multipartResolver.isMultipart(request)) {
				thumbnail = handleImages((MultipartHttpServletRequest) request,productImgs);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "上传图片不能为空");
                return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		try {
			//尝试获取前端传过来的表单String流并将其转换成Product实体类
			product = mapper.readValue(productStr, Product.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (product != null && thumbnail != null && productImgs.size() > 0) {
			try {
				//从session获取shopId,减少对前端数据的依赖
				Shop currentShop = (Shop) request.getSession().getAttribute(
						"currentShop");

				product.setShop(currentShop);
				ProductExecution pe = productService.addProduct(product,
						thumbnail, productImgs);
				if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入商品信息");
		}
		return modelMap;
	}

	private ImageHolder handleImages(MultipartHttpServletRequest request,List <ImageHolder> productImgs) throws IOException {
		MultipartHttpServletRequest multipartRequest;
		ImageHolder thumbnail;
		multipartRequest = request;
		//取出缩略图并构建ImageHolder对象
		CommonsMultipartFile commonsMultipartFile=(CommonsMultipartFile)multipartRequest;
		thumbnail = new ImageHolder(commonsMultipartFile.getName(),commonsMultipartFile.getInputStream());
		//取出详情图列表并构建List<ImageHolder>
		for (int i = 0; i < IMAGEMAXCOUNT; i++) {
            CommonsMultipartFile productImg = (CommonsMultipartFile) multipartRequest
                    .getFile("productImg" + i);

            if (productImg != null) {
                ImageHolder imageHolder = new ImageHolder(productImg.getName(),
                        productImg.getInputStream());
                productImgs.add(imageHolder);
            }else{
                //第i个图片文件流为空，终止循环
                break;
            }
        }
		return thumbnail;
	}

	@RequestMapping(value = "/modifyproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyProduct(HttpServletRequest request) {
		//商品编辑时调用还是上下架操作的时候调用，无需验证码
		boolean statusChange = HttpServletRequestUtil.getBoolean(request,
				"statusChange");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//接受前端参数的变量初始化
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		String productStr = HttpServletRequestUtil.getString(request,
				"productStr");
		MultipartHttpServletRequest multipartRequest = null;
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgs = new ArrayList<>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		//请求中存在文件流，则取出相关文件
		try{
		if (multipartResolver.isMultipart(request)) {
			thumbnail = handleImages((MultipartHttpServletRequest) request,productImgs);
		}} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			product = mapper.readValue(productStr, Product.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (product != null) {
			try {
				Shop currentShop = (Shop) request.getSession().getAttribute(
						"currentShop");
				product.setShop(currentShop);
				CommonsMultipartFile commonsMultipartFile=(CommonsMultipartFile)multipartRequest;
				ImageHolder thumbnail1 = new ImageHolder(commonsMultipartFile.getName(),commonsMultipartFile.getInputStream());
				ProductExecution pe = productService.modifyProduct(product,
						thumbnail1, productImgs);
				if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入商品信息");
		}
		return modelMap;
	}

	//整合出查询条件
	private Product compactProductCondition4Search(long shopId,
                                                   long productCategoryId,String productName) {
		//ID是必须的
		Product productCondition = new Product();
		Shop shop = new Shop();
		shop.setShopId(shopId);
		productCondition.setShop(shop);
		//有的话就添加到条件里
		if (productCategoryId != -1L) {
			ProductCategory productCategory = new ProductCategory();
			productCategory.setProductCategoryId(productCategoryId);
			productCondition.setProductCategory(productCategory);
		}
		if (productName != null) {
			productCondition.setProductName(productName);
		}
		return productCondition;
	}
}
