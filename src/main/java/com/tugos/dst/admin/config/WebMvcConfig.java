package com.tugos.dst.admin.config;

import com.tugos.dst.admin.interceptor.ServerParameterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * @author qinming
 * @date 2020-12-19 15:21:27
 * <p> 国际化配置 </p>
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    private ServerParameterInterceptor serverParameterInterceptor;

    @Bean
    public LocaleResolver localeResolver() {
        //通过cookie判断语言环境
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        //默认语音 简体中文
        cookieLocaleResolver.setDefaultLocale(Locale.CHINA);
        return cookieLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // 语言环境
        lci.setParamName("_lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());

        //添加转发
        registry.addInterceptor(serverParameterInterceptor)
                .addPathPatterns("/backup/**")
                .addPathPatterns("/home/**")
                .addPathPatterns("/player/**")
                .addPathPatterns("/setting/**")
                .addPathPatterns("/system/**")
                .excludePathPatterns("/excludePath1/**", "/excludePath2/**"); // 排除指定路径 举个例，没有实际使用
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");


        // Knife4j资源处理
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }


}
