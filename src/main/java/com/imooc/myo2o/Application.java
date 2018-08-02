package com.imooc.myo2o;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//自动做全package扫描
@SpringBootApplication
//@MapperScan("com.imooc.myo2o.dao") mybatis扫描路径，针对的是接口Mapper类
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
