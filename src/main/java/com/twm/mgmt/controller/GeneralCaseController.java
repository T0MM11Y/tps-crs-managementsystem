package com.twm.mgmt.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.twm.mgmt.constant.CrsConstants;


@RequestMapping("/campaign")
@Controller
@SessionAttributes("campaignSettingVo")
public class GeneralCaseController extends BaseController {
	
	private static final String CAMPAIGN_SETTING_URI = "/setting";
		
	@RequestMapping(CAMPAIGN_SETTING_URI + "/generalcasestep1")
	public ModelAndView generalCase1() {
		ModelAndView mv = new ModelAndView("campaign/generalCaseStep1");
		mv.addObject("menudata", "1");
		mv.addObject("menuop", "campaign/setting");
		mv.addObject("rewardType", "API");
		mv.addObject("campaignMainId", 55);
		mv.addObject("isAutoMonthInfo",1);
		return mv;
	}
	
//	@RequestMapping(CAMPAIGN_SETTING_URI + "/generalcase1Save")
//	@ResponseBody
//	public ResponseEntity<?> generalCase1Save(HttpServletRequest request,
//			@ModelAttribute("soFastSettingVo") CampaignSettingVo soFastSettingVo) {
//
//		return new ResponseEntity<>("OK", HttpStatus.OK);
//	}
	
	@RequestMapping(CAMPAIGN_SETTING_URI + "/generalcasestep2")
	public ModelAndView generalCase2() {
		ModelAndView mv = new ModelAndView("campaign/generalCaseStep2");
		mv.addObject("menudata", "1");
		mv.addObject("menuop", "campaign/setting");

		return mv;
	}
}
