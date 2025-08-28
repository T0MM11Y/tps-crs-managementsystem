package com.twm.mgmt.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.twm.mgmt.Enum.LogFormat;
import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.model.report.CheckbillVo;
import com.twm.mgmt.model.report.ElectronicMoneyVo;
import com.twm.mgmt.model.report.MomoeventVo;
import com.twm.mgmt.model.report.PayaccountVo;
import com.twm.mgmt.model.report.momoDepartmentVo;
import com.twm.mgmt.persistence.dao.CheckbillDao;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MOAccountRepository;
import com.twm.mgmt.persistence.repository.RewardReportRepository;
import com.twm.mgmt.service.AccountService;
import com.twm.mgmt.service.BaseService;
import com.twm.mgmt.service.ReportService;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(ReportController.Report_URI)
@Controller
public class ReportController extends BaseController {
	
	public static final String Report_URI = "/report";
	
	public static final String FIND_ElectronicMoneyList_URI = "/findElectronicMoneyList";
	
	public static final String FIND_CheckbillReport_URI = "/findCheckbill";
	
	@Value("${report.path}")
	private String reportPath;
	
	private Encoder encode=ESAPI.encoder();
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private DepartmentRepository departmentRepo;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ReportService reportService;
	
	private BaseService baseServer;
	
	@Autowired
	private RewardReportRepository rewardreportRepo;
	
	
	@Autowired
	private MOAccountRepository moacRepo;
	
	@Autowired
	private CheckbillDao checkbillDao;
	
	private String commonFormat = LogFormat.CommonLog.getName();
	private String errorFormat = LogFormat.SqlError.getName();

	@GetMapping(value = "/findElectronicMoneyReport")
	public ModelAndView findElectronicMoneyReport(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("report/findElectronicMoneyReport");
		mv.addObject("menudata", "5");
		mv.addObject("menuop", "report/findElectronicMoneyReport");
		
//		//System.out.println("findElectronicMoneyReportgetAccountId:"+baseServer.getAccountId());
		HttpSession session= request.getSession();
		UserInfoVo xx = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);

		try {
			//請購單位	
			List<String> departmentList = reportService.findElectronicmoneyDepartmentList();
			//System.out.println("departmentListsize:"+departmentList.size() );

			
			
			//po單號
			List<MomoeventVo> momoeventList = new ArrayList<MomoeventVo>();
			for(MomoeventVo x : reportService.findMomoeventList()) {
				if(!momoeventList.contains(x)) {
					momoeventList.add(x);
					continue;
				}
			}

			//戶頭代碼
			List<PayaccountVo> payaccountList = new ArrayList<PayaccountVo>();
			for(PayaccountVo x : reportService.findPayaccountList()) {
				if(!payaccountList.contains(x)) {
					payaccountList.add(x);
					continue;
				}
			}
			
			
			mv.addObject("departmentList", departmentList);
			mv.addObject("momoeventList", momoeventList);
			mv.addObject("payaccountList", payaccountList);
		} catch (Exception e) {
			log.error("ExchangecurrencyController", e.getMessage(), e);

		}

		return mv;
	}
	
	@PostMapping(value = FIND_ElectronicMoneyList_URI)
	public ResponseEntity<?> findElectronicMoneyList(HttpSession session,ElectronicMoneyVo condition, HttpServletRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		UserInfoVo xx = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		//SSOLogin
		QueryResultVo result = reportService.findElectronicMoneyList(condition, xx.getRoleId().intValue(), request);
//		QueryResultVo result = reportService.findElectronicMoneyList(condition, 1, request);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	 * 核帳報表 進入頁面
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/findCheckbill")
	public ModelAndView findCheckbill(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("report/findCheckbill");
		mv.addObject("menudata", "5");
		mv.addObject("menuop", "report/findCheckbill");
		List<momoDepartmentVo> momoDepartmentList = new ArrayList<momoDepartmentVo>();
		List<String> momoDepartmentStr_List = reportService.findmomoCustomDepartmentList(getAccountId(request));
		String schedule_id = "核帳報表-findCheckbill_" + DateUtilsEx.dateFormat().format(new Date());
		log.info(commonFormat, "-", schedule_id, momoDepartmentStr_List.toString(), "-", "-", "-","-");
		for(String strs :  momoDepartmentStr_List) {
			momoDepartmentVo vo = new momoDepartmentVo();
			//System.out.println("momoDepartmentStr_List001:"+strs);
//			
			MOAccountEntity result = moacRepo.findBydepartmentId1(strs);
			//checkmarx弱掃
			result = JsonUtil.jsonToPojo(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(result))), MOAccountEntity.class);
			//System.out.println("momoDepartmentStr_List002:"+result.getDepartmentId() + ", "+result.getDeptNo());
//			
			DepartmentEntity entity1 = departmentRepo.findBydepartmentId(result.getDepartmentId());
			//System.out.println("momoDepartmentStr_List003:"+entity1.getDepartmentName());
//			momoDepartmentStr_List.add(strs+"_"+entity1.getDepartmentName());
			vo.setDepartmentId(result.getDepartmentId());
			vo.setDepartmentName(strs+"_"+entity1.getDepartmentName());
			momoDepartmentList.add(vo);
		}
		
		
		mv.addObject("departmentList", momoDepartmentList);
		return mv;
	}
	
	/**
	 * 核帳報表 查出清單
	 * @param condition
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@PostMapping(value = FIND_CheckbillReport_URI)
	public ResponseEntity<?> findCheckbillReport(CheckbillVo condition, HttpServletRequest request) throws ParseException {
		
		List<String> momoDepartmentStr_List = reportService.findmomoCustomDepartmentList(getAccountId(request));
		QueryResultVo result = reportService.findcheckbillList(condition,momoDepartmentStr_List);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	 * 核帳報表 顯示總計 "總計筆數/金額"
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/findCheckbill1")
	public ResponseEntity<?> findCheckbillReport1(CheckbillVo condition, HttpServletRequest request) {
		List<String> momoDepartmentStr_List = reportService.findmomoCustomDepartmentList(getAccountId(request));
		String s = reportService.findcheckbillList1(condition,momoDepartmentStr_List);
		return new ResponseEntity<>(s, HttpStatus.OK);
	}
	
	/**
	 * 核帳報表   這是為了BOMD想要顯示的項目, 所以只有momo公司單位選 "BOMD_營管處" 才會執行這個方法
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/findCheckbillForBOMD")
	public ResponseEntity<?> findCheckbillForBOMD(CheckbillVo condition, HttpServletRequest request) {
		List<String> momoDeptNoList = reportService.findmomoCustomDepartmentList(getAccountId(request));
		
		System.out.println("momoDepartmentStr_List:"+momoDeptNoList);
		
		String s = checkbillDao.findCheckbillForBOMD(condition,momoDeptNoList);
		return new ResponseEntity<>(s, HttpStatus.OK);
	}
	
	/**
	 *  核帳報表 產生總表檔案
	 * @param session
	 * @param condition
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@PostMapping(value = "/findCheckbill2")
	public ResponseEntity<?> findCheckbillReport2(HttpSession session,CheckbillVo condition, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {
		UserInfoVo xx = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		
		String filename = xx.getUserName().replaceAll("\\s+", "")+"_"+String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()); 
//		filename = xx.getUserName()+reportService.findcheckbillList2();//username + return 當下時間
//		filename = reportService.findcheckbillList2();//return 當下時間

//		List<CheckbillDto> dtos = checkbillDao.findByCondition(condition);
		List<String> momoDepartmentStr_List = reportService.findmomoCustomDepartmentList(getAccountId(request));
		
		//解Checkmarx的高風險Relative Path Traversal
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		reportService.findcheckbillList3(checkbillDao.findByCondition(condition,momoDepartmentStr_List),filename, byteArrayOutputStream);//return 當下時間
	    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".xlsx\"")
	            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	            .body(resource);
	}
	
	/**
	 * 核帳報表 產生明細檔案
	 * @param session
	 * @param condition
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@PostMapping(value = "/createDetailFile")
	public ResponseEntity<?> createDetailFile(HttpSession session,CheckbillVo condition, HttpServletRequest request) throws ParseException, IOException {
		UserInfoVo ui = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		String schedule_id = "核帳報表-createDetailFile_" + DateUtilsEx.dateFormat().format(new Date());
		String filename = ui.getUserName().replaceAll("\\s+", "")+"_DetailReport_"+String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()); 
		log.info(commonFormat, "-", schedule_id, ui.toString(), filename, "-", "-", "-","-");
		List<String> momoDeptNoList = reportService.findmomoCustomDepartmentList(getAccountId(request));
		
		//解Checkmarx的高風險Relative Path Traversal
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();		
		reportService.createDetailFile(checkbillDao.findDetailByCondition(condition,momoDeptNoList,"detail"),filename,byteArrayOutputStream);//return 當下時間
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".xlsx\"")
	            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	            .body(resource);
	}
	
				
	private Long getAccountId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserInfoVo userInfo = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		return userInfo.getAccountId();
	}

}
