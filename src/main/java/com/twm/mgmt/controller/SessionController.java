package com.twm.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.twm.mgmt.interceptor.CrsInterceptor;

@RestController
public class SessionController extends CrsInterceptor{

	@GetMapping("/api/sessionTime")
	public boolean getSessionTime(HttpServletRequest request) {
		if (!isAuthorized(request)) {
			

			return false;
		}
		return true;
	}
}
