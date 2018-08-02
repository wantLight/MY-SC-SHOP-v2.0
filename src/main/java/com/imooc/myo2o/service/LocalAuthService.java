package com.imooc.myo2o.service;

import com.imooc.myo2o.entity.LocalAuth;
import com.imooc.myo2o.vo.LocalAuthExecution;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface LocalAuthService {
	/**
	 * 通过账号密码查询平台账号信息
	 * @param userName
	 * @return
	 */
	LocalAuth getLocalAuthByUserNameAndPwd(String userName,String password);

	/**
	 * 通过userId获取平台账号信息
	 * @param userId
	 * @return
	 */
	LocalAuth getLocalAuthByUserId(long userId);

	/**
	 * 
	 * @param localAuth
	 * @param profileImg
	 * @return
	 * @throws RuntimeException
	 */
	LocalAuthExecution register(LocalAuth localAuth,
                                CommonsMultipartFile profileImg) throws RuntimeException;

	/**
	 * 绑定微信，生成平台专属账号
	 * @param localAuth
	 * @return
	 * @throws RuntimeException
	 */
	LocalAuthExecution bindLocalAuth(LocalAuth localAuth)
			throws RuntimeException;

	/**
	 * 修改平台账号登陆密码
	 * @param userId
	 * @param userName
	 * @param password
	 * @param newPassword
	 * @return
	 */
	LocalAuthExecution modifyLocalAuth(Long userId,String userName,
                                       String password,String newPassword);
}
