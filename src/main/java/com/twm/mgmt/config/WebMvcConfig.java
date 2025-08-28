package com.twm.mgmt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.twm.mgmt.interceptor.CrsInterceptor;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private CrsInterceptor crsInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(crsInterceptor).addPathPatterns("/**");

	}

}
