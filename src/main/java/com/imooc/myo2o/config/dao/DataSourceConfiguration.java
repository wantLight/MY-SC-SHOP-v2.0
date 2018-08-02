package com.imooc.myo2o.config.dao;

import com.imooc.myo2o.util.DESUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyVetoException;

/**
 * Created by xyzzg on 2018/7/31.
 */
//配置写入到springIOC容器
@Configuration
//扫描路径
@MapperScan("com.imooc.myo2o.dao")
public class DataSourceConfiguration {

    @Value("${jdbc.driver}")
    private String jdbcDriver;
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.username}")
    private String jdbcUsername;
    @Value("${jdbc.password}")
    private String jdbcPassword;

    /**
     * 生成与spring-dao.xml对应的bean dataSource
     */
    @Bean(name = "dataSource")
    public ComboPooledDataSource createDataSource(){
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        //跟配置文件一样，设置信息
        try {
            dataSource.setDriverClass(jdbcDriver);
            dataSource.setJdbcUrl(jdbcUrl);
            dataSource.setUser(DESUtils.getDecryptString(jdbcUsername));
            dataSource.setPassword(DESUtils.getDecryptString(jdbcPassword));
            dataSource.setMaxPoolSize(30);
            dataSource.setMinPoolSize(10);
            //关闭连接后不自动commit
            dataSource.setAutoCommitOnClose(false);
            dataSource.setCheckoutTimeout(100000);
            //失败重传次数
            dataSource.setAcquireRetryAttempts(2);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}
