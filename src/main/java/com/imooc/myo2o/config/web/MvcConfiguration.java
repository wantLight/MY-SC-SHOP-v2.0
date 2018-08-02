package com.imooc.myo2o.config.web;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.imooc.myo2o.interceptor.shop.ShopLoginInterceptor;
import com.imooc.myo2o.interceptor.shop.ShopPermissionInterceptor;
import org.omg.PortableInterceptor.Interceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * WebMvcConfigurerAdapter:配置视图解析器viewResolver
 * ApplicationContextAware接口：方便获取ApplicationContext中所有的bean
 * Created by xyzzg on 2018/8/2.
 */
@Configuration
//<mvc:annotation-driven />开启SpringMVC注解模式
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware{

    //spring容器
    private ApplicationContext applicationContext;

    @Value("${kaptcha.border}")
    private String border;

    //todo ....kaptcha待全部映入。。 都是String

    //由于web.xml不生效了，需要在这里配置Kaptcha验证码Servlet
    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        ServletRegistrationBean servlet = new ServletRegistrationBean(new KaptchaServlet(),"/Kaptcha");
        servlet.addInitParameter("kaptcha.border",border);
        //。。。。and so on
        return servlet;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //静态资源配置
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        //registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/");
        //一定要加file spring boot内置tomcat，无法修改。。
        registry.addResourceHandler("/upload/**").addResourceLocations("file:/Users/baidu/work/image/upload/");
    }

    //定义默认的请求处理器
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    //创建viewResolver
    @Bean("viewResolver")
    public ViewResolver createViewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        //设置Spring容器
        viewResolver.setApplicationContext(this.applicationContext);
        //取消缓存
        viewResolver.setCache(false);
        //设置解析的前缀
        viewResolver.setPrefix("/WEB-INF/html/");
        //设置视图解析的后缀
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    //文件上传解析器
    @Bean("multipartResolver")
    public CommonsMultipartResolver createMultipartResolver(){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        //1024*1024*20 = 20M
        multipartResolver.setMaxUploadSize(20971520);
        multipartResolver.setMaxInMemorySize(20971520);
        return multipartResolver;
    }

    //添加拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String interceptorsPath = "/shopadmin/**";
        //注册拦截器
        InterceptorRegistration loginIR = registry.addInterceptor(new ShopLoginInterceptor());
        //配置拦截路径
        loginIR.addPathPatterns(interceptorsPath);
        //还可以注册其它拦截器
        InterceptorRegistration permisssionIR = new InterceptorRegistration(new ShopPermissionInterceptor());
        //配置拦截路径
        permisssionIR.addPathPatterns(interceptorsPath);
        //配置不拦截的路径
        permisssionIR.excludePathPatterns("/shopadmin/shoplist");
        permisssionIR.excludePathPatterns("/shopadmin/getshoplist");
        permisssionIR.excludePathPatterns("/shopadmin/getshioinitinfo");
        permisssionIR.excludePathPatterns("/shopadmin/registershop");
        permisssionIR.excludePathPatterns("/shopadmin/shopoperation");
        permisssionIR.excludePathPatterns("/shopadmin/shopmanagement");
        permisssionIR.excludePathPatterns("/shopadmin/getshopmanagementinfo");
    }
}
