package com.twm.mgmt.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.MultiValueMap;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.enums.Rewardmemo;
import com.twm.mgmt.model.account.AccountVo;
import com.twm.mgmt.model.account.DepartmentVo;
import com.twm.mgmt.model.usermanage.*;
import com.twm.mgmt.model.report.CheckbillVo;
import com.twm.mgmt.model.report.momoDepartmentVo;
import com.twm.mgmt.model.common.MailVo;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.ReceiveInfoVo;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.CampaignMainEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.entity.RewardReportEntity;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.persistence.repository.CampaignMainRepository;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MOAccountRepository;
import com.twm.mgmt.persistence.repository.RewardReportRepository;
import com.twm.mgmt.service.AccountService;
import com.twm.mgmt.service.ReportService;
import com.twm.mgmt.service.UserManageService;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.CommonUtils;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.MailUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@RequestMapping(UserManageController.userManage_URI)
@Controller
public class UserManageController extends BaseController {
	
	public static final String userManage_URI = "/userManage";
	
	public static final String FIND_ExchangeCurrency_LIST_URI = "/findExchangeCurrencyList";

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserManageService userManageService;		
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private MOAccountRepository moacRepo;
	
	@Autowired
	private DepartmentRepository departmentRepo;	
	/**
	 * 簽核查詢
	 * 
	 * @return
	 */
	@GetMapping(value = "/findExchangeCurrency")
	public ModelAndView findExchangeCurrency(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("userManage/findExchangeCurrency");
		mv.addObject("menudata", "4");
		mv.addObject("menuop", "userManage/findExchangeCurrency");
		//System.out.println("enteruserManage/findExchangeCurrency");
	
		
		HttpSession session= request.getSession();
		UserInfoVo xx = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		List<momoDepartmentVo> momoDepartmentList = new ArrayList<momoDepartmentVo>();
		try {

//			List<momoDepartmentVo> momoDepartmentList = reportService.findmomoDepartmentList();
			
			List<String> momoDepartment_List = reportService.findmomoDepartmentList1();
			for(String strs :  momoDepartment_List) {
				momoDepartmentVo vo = new momoDepartmentVo();
				MOAccountEntity result = moacRepo.findBydepartmentId1(strs);
				//checkmarx弱掃
				result = JsonUtil.jsonToPojo(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(result))), MOAccountEntity.class);
				DepartmentEntity entity1 = departmentRepo.findBydepartmentId(result.getDepartmentId());
				vo.setDepartmentId(result.getDepartmentId());
				vo.setDepartmentName(entity1.getDepartmentName());
//				vo.setDepartmentName(strs+"_"+entity1.getDepartmentName());
				momoDepartmentList.add(vo);
			}
			
//			List<DepartmentVo> departmentList = accountService.findDepartmentList();
			
			//SSOLogin
			AccountEntity accountEntity = accountRepo.findByAccountID1(xx.getAccountId());
//			AccountEntity accountEntity = accountRepo.findByAccountID1((long)1);

			if(accountEntity.getRoleId().toString().equals("25")) { //看角色
				for(int i =0; i<= momoDepartmentList.size()-1 ; i++) {
//					//System.out.println("getRoleId1:"+departmentList.get(i).getDepartmentId());
					
					//SSOLogin
					
					if (!momoDepartmentList.get(i).getDepartmentId().toString().equals(xx.getDepartmentId().toString())){
						momoDepartmentList.remove(i--);
					}
					
//					if (!momoDepartmentList.get(i).getDepartmentId().toString().equals("1")){
//						momoDepartmentList.remove(i--);
//					}
				}				
			}
			
			mv.addObject("departmentList", momoDepartmentList);
		} catch (Exception e) {
			log.error("ExchangecurrencyController", e.getMessage(), e);

		}
		//1 2 3 6 7 8 11
		mv.addObject("Rewardmemo", Rewardmemo.getOptions2());

		return mv;
	}

	@PostMapping(value = FIND_ExchangeCurrency_LIST_URI)
	public ResponseEntity<?> findExchangeCurrency_LIST( ExchangecurrencyVo condition, HttpServletRequest request) {
		//System.out.println("findExchangeCurrency_LISTStart:");
		HttpSession session= request.getSession();
		UserInfoVo xx = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		if(condition.getStatus().equals("checkboxA")) {
			QueryResultVo result = userManageService.findexchangecurrencyList(condition,xx);
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		
		if(condition.getStatus().equals("checkboxB")) {
			QueryResultVo result = userManageService.findexchangecurrencyListB(condition,xx);
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

}
