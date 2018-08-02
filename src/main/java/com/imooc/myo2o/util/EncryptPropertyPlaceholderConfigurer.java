package com.imooc.myo2o.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 转换加密字符串
 */
public class EncryptPropertyPlaceholderConfigurer extends
		PropertyPlaceholderConfigurer {
	//需要加密的字段数组
	private String[] encryptPropNames = { "jdbc.username", "jdbc.password" };

	//对关键属性进行转换
	@Override
	protected String convertProperty(String propertyName, String propertyValue) {
		if (isEncryptProp(propertyName)) {
			//对已加密字符串进行解密
			String decryptValue = DESUtils.getDecryptString(propertyValue);
			return decryptValue;
		} else {
			return propertyValue;
		}
	}

	//是否加密？
	private boolean isEncryptProp(String propertyName) {
		//若等于需要加密的field，则进行加密
		for (String encryptpropertyName : encryptPropNames) {
			if (encryptpropertyName.equals(propertyName))
				return true;
		}
		return false;
	}
}
