package com.gms.web.admin.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gms.web.admin.common.web.interceptor.SessionCheckInterceptor;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private SessionCheckInterceptor authInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
		.addPathPatterns("/gms/**")
		.excludePathPatterns("/login")
		.excludePathPatterns("/policy")
		.excludePathPatterns("/api/*")
		.excludePathPatterns("/loginAction.do")
		.excludePathPatterns("/dist/**")
		.excludePathPatterns("/plugins/**");
	}

}