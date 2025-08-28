package com.twm.mgmt.controller;



import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.twm.mgmt.model.momoApi.MomoUpdateIdResponse;

import com.twm.mgmt.service.MomoApiService;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MomoApiController extends BaseController {
	
	@Autowired
	private MomoApiService momoApiService;



	
	@PostMapping(value=GET_MOMOID_CHANGE_SMS,produces= {"application/json;charset=UTF-8"})
	public String getMomoidChangeSms(@RequestBody List<MomoUpdateIdResponse> momoList,HttpServletRequest request) {
				
		try {
			
			momoApiService.getMomoidChangeSms(momoList,request);
			

			
			return "SUCCESS";
		} catch (Exception e) {
			return "FAIL";
		}
		
		
	}

}
