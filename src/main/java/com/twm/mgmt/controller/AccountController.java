package com.twm.mgmt.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.twm.mgmt.Enum.LogFormat;
import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.model.account.APAccountConditionVo;
import com.twm.mgmt.model.account.APAccountVo;
import com.twm.mgmt.model.account.AccountConditionVo;
import com.twm.mgmt.model.account.AccountVo;
import com.twm.mgmt.model.account.DepartmentConditionVo;
import com.twm.mgmt.model.account.DepartmentVo;
import com.twm.mgmt.model.account.MOAccountConditionVo;
import com.twm.mgmt.model.account.MOAccountVo;
import com.twm.mgmt.model.account.MoAccountMapAccountConditionVo;
import com.twm.mgmt.model.account.PermissionVo;
import com.twm.mgmt.model.account.RedeemTotalApiVo;
import com.twm.mgmt.model.account.RoleVo;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.model.report.momoDepartmentVo;
import com.twm.mgmt.persistence.dto.AccountActionHistoryDto;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MOAccountRepository;
import com.twm.mgmt.service.AccountService;
import com.twm.mgmt.service.ReportService;
import com.twm.mgmt.utils.CommonUtils;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.validator.account.APAccountVoValidator;
import com.twm.mgmt.validator.account.AccountVoValidator;
import com.twm.mgmt.validator.account.DepartmentVoValidator;
import com.twm.mgmt.validator.account.MOAccountVoValidator;
import com.twm.mgmt.validator.account.PermissionVoValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/account")
@Controller
public class AccountController extends BaseController {

	private static final String FIND_ACCOUNT_LIST_URI = "/findAccount";

	private static final String ROLE_PERMISSION_URI = "/rolePermission";

	private static final String ACCOUNT_PERMISSION_URI = "/accountPermission";

	private static final String FIND_ROLE_MENU_URI = "/findRoleMenuList";

	private static final String FIND_ACCOUNT_MENU_URI = "/findAccountMenuList";

	private static final String SAVE_ROLE_PERMISSION_URI = "/saveRolePermission";

	private static final String SAVE_ACCOUNT_PERMISSION_URI = "/saveAccountPermission";

	private static final String ACCOUNT_SETTING_URI = "/setting";

	private static final String SAVE_ACCOUNT_SETTING_URI = "/saveAccountSetting";

	private static final String UPDATE_ACCOUNT_STATUS_URI = "/updateAccountStatus";

	private static final String FIND_DEPARTMENT_LIST_URI = "/findDepartment";

	private static final String DEPARTMENT_SETTING_URI = "/departmentSetting";

	private static final String SAVE_DEPARTMENT_SETTING_URI = "/saveDepartmentSetting";

	private static final String UPDATE_DEPARTMENT_STATUS_URI = "/updateDepartmentStatus";

	private static final String FIND_AP_ACCOUNT_LIST_URI = "/findAPAccount";

	private static final String AP_ACCOUNT_SETTING_URI = "/apAccountSetting";

	private static final String SAVE_AP_ACCOUNT_SETTING_URI = "/saveAPAccountSetting";

	private static final String FIND_MO_ACCOUNT_LIST_URI = "/findMOAccount";

	private static final String MO_ACCOUNT_SETTING_URI = "/moAccountSetting";

	private static final String SAVE_MO_ACCOUNT_SETTING_URI = "/saveMOAccountSetting";

	private static final String FIND_DEPARTMENT_ACCOUNT_LIST_URI = "/findDepartmentAccountList";
	
	private static final String FIND_MOACCOUNT_MAP_ACCOUNT_LIST_URI = "/findMoAccountMapAccount";
	
	private static final String DELETE_MOACCOUNT_MAP_ACCOUNT = "/deleteMoAccountMapAccount";
	
	private static final String INSERT_MOACCOUNT_MAP_ACCOUNT = "/insertMoAccountMapAccount";
	
	private static final String FIND_REFEEM_TOTAL_API = "/switchOfRedeemTotalApi";
	
	private static final String DELETE_REFEEM_TOTAL_API = "/deleteRedeemTotalApi";
	
	private static final String INSERT_REFEEM_TOTAL_API = "/insertRedeemTotalApi";
	
	private static final String FIND_ACCOUNT_ACTION_HISTORY = "/findAccountActionHistory";
	
	
	private String commonFormat = LogFormat.CommonLog.getName();
	private String errorFormat = LogFormat.SqlError.getName();
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private DepartmentRepository departmentRepo;
	@Autowired
	private MOAccountRepository moacRepo;
	
	@GetMapping(FIND_ACCOUNT_LIST_URI)
	public ModelAndView findAccount() {
		ModelAndView mv = new ModelAndView("account/findAccount");
		
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/findAccount");
		
		List<RoleVo> roleList = accountService.findRoleList();

		List<DepartmentVo> departmentList = accountService.findDepartmentList();

		mv.addObject("roleList", roleList);

		mv.addObject("departmentList", departmentList);

		return mv;
	}

	@PostMapping(FIND_ACCOUNT_LIST_URI)
	public ResponseEntity<?> findAccount(AccountConditionVo condition, HttpServletRequest request) {
		ActionType action = condition.getAction();

		HttpSession session = request.getSession();

		try {
			if (action.isQuery()) {
				session.setAttribute(CrsConstants.QUERY_CONDITION, condition);
			} else {
				AccountConditionVo oriCondition = (AccountConditionVo) session.getAttribute(CrsConstants.QUERY_CONDITION);

				accountService.copyQueryCondition(oriCondition, condition);
			}

			QueryResultVo result = accountService.findAccountList(condition);

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("AccountController findAccount Error: {}", e.getMessage(), e);

			return getErrorResponse();
		}
	}

	@GetMapping(ROLE_PERMISSION_URI)
	public ModelAndView rolePermissionSetting() {
		ModelAndView mv = new ModelAndView("account/rolePermission");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/rolePermission");
		try {
			mv.addObject("action", ActionType.ADD.getCode());

			mv.addObject("roleList", accountService.findRoleList());
		} catch (Exception e) {
			log.error("AccountController rolePermissionSetting Error: {}", e.getMessage(), e);

		}

		return mv;
	}

	@GetMapping(ACCOUNT_PERMISSION_URI)
	public ModelAndView accountPermissionSetting() {
		ModelAndView mv = new ModelAndView("account/accountPermission");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/accountPermission");
		try {
			mv.addObject("accounts", accountService.findAccountList());
		} catch (Exception e) {
			log.error("AccountController accountPermissionSetting Error: {}", e.getMessage(), e);

		}

		return mv;
	}

	@PostMapping({ FIND_ROLE_MENU_URI, FIND_ACCOUNT_MENU_URI })
	public ModelAndView findMenuList(@RequestParam(name = "id", required = false) Long id, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("account/menuList :: permission_menu");

		try {
			String uri = request.getRequestURI();

			if (uri.indexOf(FIND_ROLE_MENU_URI) != -1) {
				mv.addObject("vos", accountService.findRoleMenu(id));
			} else {
				AccountEntity account = accountService.getAccountEntity(id);

				RoleEntity role = account.getRole();

				mv.addObject("roleName", account == null ? null : new RoleVo(role).getRoleName());

				mv.addObject("vos", accountService.findAccountMenu(id, role.getRoleId()));
			}

		} catch (Exception e) {
			log.error("AccountController findMenuList: {}", e.getMessage(), e);

		}

		return mv;
	}

	@PostMapping({ SAVE_ROLE_PERMISSION_URI, SAVE_ACCOUNT_PERMISSION_URI })
	public ResponseEntity<?> saveOrUpdatePermission(PermissionVo vo, HttpServletRequest request) {
		try {
			Map<String, List<String>> validErrors = CommonUtils.validate(Arrays.asList(new PermissionVoValidator(vo)));

			if (!validErrors.isEmpty()) {

				return getValidErrorResponse(validErrors);
			}

			accountService.saveOrUpdatePermission(vo);

		} catch (Exception e) {
			log.error("AccountController saveOrUpdatePermission: {}", e.getMessage(), e);

			return getErrorResponse();
		}

		return getSuccessResponse("儲存成功");
	}

	@RequestMapping(value = ACCOUNT_SETTING_URI, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView accountSetting(Long accountId, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("account/accountSetting");
		mv.addObject("menudata", "6");
		
		try {
			mv.addObject("vo", accountService.getAccount(accountId));

			mv.addObject("roleList", accountService.findRoleList());

			mv.addObject("departmentList", accountService.findDepartmentList());
			
			if(accountService.getAccount(accountId).getAccountId()==null)
				mv.addObject("menuop", "account/setting");
			else
				mv.addObject("menuop", "account/findAccount");
		} catch (Exception e) {		
			mv.addObject("menuop", "account/setting");
			log.error("AccountController accountSetting: {}", e.getMessage(), e);

		}

		return mv;
	}

	@PostMapping(SAVE_ACCOUNT_SETTING_URI)
	public ResponseEntity<?> saveAccountSetting(AccountVo vo) {
		try {
			Map<String, List<String>> validErrors = CommonUtils.validate(Arrays.asList(new AccountVoValidator(vo)));

			if (!validErrors.isEmpty()) {

				return getValidErrorResponse(validErrors);
			}

			accountService.saveOrUpdateAccount(vo);

		} catch (Exception e) {
			log.error("AccountController saveAccountSetting: {}", e.getMessage(), e);

			return getErrorResponse();
		}

		return getSuccessResponse("儲存成功");
	}

	@PostMapping(UPDATE_ACCOUNT_STATUS_URI)
	public ResponseEntity<?> updateAccountStatus(Long accountId, String status,String requestId) {
		try {
			accountService.updateAccountStatus(accountId, status,requestId);

		} catch (Exception e) {
			log.error("AccountController updateAccountStatus: {}", e.getMessage(), e);

			return getErrorResponse();
		}

		return getSuccessResponse("更新成功");
	}

	@GetMapping(FIND_DEPARTMENT_LIST_URI)
	public ModelAndView findDepartment() {
		ModelAndView mv = new ModelAndView("account/findDepartment");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/findDepartment");
		return mv;
	}

	@PostMapping(FIND_DEPARTMENT_LIST_URI)
	public ResponseEntity<?> findDepartment(DepartmentConditionVo condition, HttpServletRequest request) {
		ActionType action = condition.getAction();

		HttpSession session = request.getSession();

		try {
			if (action.isQuery()) {
				session.setAttribute(CrsConstants.QUERY_CONDITION, condition);
			} else {
				DepartmentConditionVo oriCondition = (DepartmentConditionVo) session.getAttribute(CrsConstants.QUERY_CONDITION);

				accountService.copyQueryCondition(oriCondition, condition);
			}

			QueryResultVo result = accountService.findDepartmentList(condition);

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("AccountController findDepartment Error: {}", e.getMessage(), e);

			return getErrorResponse();
		}
	}

	@PostMapping(DEPARTMENT_SETTING_URI)
	public ModelAndView departmentSetting(Long departmentId) {
		ModelAndView mv = new ModelAndView("account/departmentSetting");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/findDepartment");
		try {
			mv.addObject("vo", accountService.getDepartment(departmentId));
		} catch (Exception e) {
			log.error("AccountController departmentSetting Error: {}", e.getMessage(), e);

		}

		return mv;
	}

	@PostMapping(SAVE_DEPARTMENT_SETTING_URI)
	public ResponseEntity<?> saveDepartmentSetting(DepartmentVo vo) {
		try {
			Map<String, List<String>> validErrors = CommonUtils.validate(Arrays.asList(new DepartmentVoValidator(vo)));

			if (!validErrors.isEmpty()) {

				return getValidErrorResponse(validErrors);
			}

			accountService.saveOrUpdateDepartment(vo);

		} catch (Exception e) {
			log.error("AccountController saveDepartmentSetting: {}", e.getMessage(), e);

			return getErrorResponse();
		}

		return getSuccessResponse("儲存成功");
	}

	@PostMapping(UPDATE_DEPARTMENT_STATUS_URI)
	public ResponseEntity<?> updateDepartmentStatus(Long departmentId, String status) {
		try {
			accountService.updateDepartmentStatus(departmentId, status);

		} catch (Exception e) {
			log.error("AccountController updateDepartmentStatus: {}", e.getMessage(), e);

			return getErrorResponse();
		}

		return getSuccessResponse("更新成功");
	}

	@GetMapping(FIND_AP_ACCOUNT_LIST_URI)
	public ModelAndView findAPAccount() {
		ModelAndView mv = new ModelAndView("account/findAPAccount");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/findAPAccount");
		List<DepartmentVo> departmentList = accountService.findDepartmentList();

		mv.addObject("departmentList", departmentList);

		return mv;
	}

	@PostMapping(FIND_AP_ACCOUNT_LIST_URI)
	public ResponseEntity<?> findAPAccount(APAccountConditionVo condition, HttpServletRequest request) {
		ActionType action = condition.getAction();

		HttpSession session = request.getSession();

		try {
			if (action.isQuery()) {
				session.setAttribute(CrsConstants.QUERY_CONDITION, condition);
			} else {
				APAccountConditionVo oriCondition = (APAccountConditionVo) session.getAttribute(CrsConstants.QUERY_CONDITION);

				accountService.copyQueryCondition(oriCondition, condition);
			}

			QueryResultVo result = accountService.findAPAccountList(condition);

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("AccountController findAPAccount Error: {}", e.getMessage(), e);

			return getErrorResponse();
		}
	}

	@RequestMapping(value = AP_ACCOUNT_SETTING_URI, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView apAccountSetting(Long departmentId, String sourceId, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("account/apAccountSetting");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/apAccountSetting");
		try {
			List<DepartmentVo> departmentList = accountService.findDepartmentList();

			mv.addObject("departmentList", departmentList);

			mv.addObject("vo", accountService.getAPAccount(departmentId, sourceId));
		} catch (Exception e) {
			log.error("AccountController apAccountSetting: {}", e.getMessage(), e);

		}

		return mv;
	}

	@PostMapping(SAVE_AP_ACCOUNT_SETTING_URI)
	public ResponseEntity<?> saveAPAccountSetting(APAccountVo vo) {
		try {
			Map<String, List<String>> validErrors = CommonUtils.validate(Arrays.asList(new APAccountVoValidator(vo)));

			if (!validErrors.isEmpty()) {

				return getValidErrorResponse(validErrors);
			}

			accountService.saveOrUpdateAPAccount(vo);

		} catch (Exception e) {
			log.error("AccountController saveApAccountSetting: {}", e.getMessage(), e);

			return getErrorResponse();
		}

		return getSuccessResponse("儲存成功");
	}

	@GetMapping(FIND_MO_ACCOUNT_LIST_URI)
	public ModelAndView findMOAccount() {
		ModelAndView mv = new ModelAndView("account/findMOAccount");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/findMOAccount");
		return mv;
	}

	@PostMapping(FIND_MO_ACCOUNT_LIST_URI)
	public ResponseEntity<?> findMOAccount(MOAccountConditionVo condition, HttpServletRequest request) {
		ActionType action = condition.getAction();

		HttpSession session = request.getSession();

		try {
			if (action.isQuery()) {
				session.setAttribute(CrsConstants.QUERY_CONDITION, condition);
			} else {
				MOAccountConditionVo oriCondition = (MOAccountConditionVo) session.getAttribute(CrsConstants.QUERY_CONDITION);

				accountService.copyQueryCondition(oriCondition, condition);
			}

			QueryResultVo result = accountService.findMOAccountList(condition);

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("AccountController findMOAccount Error: {}", e.getMessage(), e);

			return getErrorResponse();
		}
	}

	@RequestMapping(value = MO_ACCOUNT_SETTING_URI, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView moAccountSetting(String merchantId,String deptNo, HttpServletRequest request) {
		//checkmarx弱掃
		merchantId = StringEscapeUtils.escapeHtml(merchantId);
		deptNo = StringEscapeUtils.escapeHtml(deptNo);
		ModelAndView mv = new ModelAndView("account/moAccountSetting");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/moAccountSetting");
		try {
			List<DepartmentVo> departmentList = accountService.findDepartmentList();

			mv.addObject("departmentList", departmentList);

			mv.addObject("vo", accountService.getMOAccount(merchantId,deptNo));
		} catch (Exception e) {
			log.error("AccountController moAccountSetting: {}", e.getMessage(), e);

		}

		return mv;
	}

	@PostMapping(FIND_DEPARTMENT_ACCOUNT_LIST_URI)
	public ResponseEntity<?> findAccountList(Long departmentId) {
		try {
			List<AccountVo> accountList = accountService.findAccountList(departmentId);

			return new ResponseEntity<>(accountList, HttpStatus.OK);
		} catch (Exception e) {
			log.error("AccountController findAccountList: {}", e.getMessage(), e);

			return getErrorResponse();
		}
	}

	@PostMapping(SAVE_MO_ACCOUNT_SETTING_URI)
	public ResponseEntity<?> saveMOAccountSetting(MOAccountVo vo) {
		try {
			Map<String, List<String>> validErrors = CommonUtils.validate(Arrays.asList(new MOAccountVoValidator(vo)));

			if (!validErrors.isEmpty()) {

				return getValidErrorResponse(validErrors);
			}

			accountService.saveOrUpdateMOAccount(vo);

		} catch (Exception e) {
			log.error("AccountController saveMOAccountSetting: {}", e.getMessage(), e);

			return getErrorResponse();
		}

		return getSuccessResponse("儲存成功");
	}
	
	/**
	 * 核帳報表權限控管 進入畫面
	 * @return
	 */
	@GetMapping(FIND_MOACCOUNT_MAP_ACCOUNT_LIST_URI)
	public ModelAndView findMoAccountMapAccount() {
		ModelAndView mv = new ModelAndView("account/findMoAccountMapAccount");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/findMoAccountMapAccount");
		
		//這裡是抓momo公司單位清單
		List<momoDepartmentVo> momoDepartmentList = new ArrayList<momoDepartmentVo>();
		List<String> momoDepartmentStr_List = reportService.findmomoDepartmentList1();
		for(String strs :  momoDepartmentStr_List) {
			momoDepartmentVo vo = new momoDepartmentVo();
			MOAccountEntity result = moacRepo.findBydepartmentId1(strs);
			//checkmarx弱掃
			result = JsonUtil.jsonToPojo(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(result))), MOAccountEntity.class);
			DepartmentEntity entity1 = departmentRepo.findBydepartmentId(result.getDepartmentId());
			vo.setDepartmentId(result.getDepartmentId());
			vo.setDepartmentName(strs+"_"+entity1.getDepartmentName());
			momoDepartmentList.add(vo);
		}
		mv.addObject("departmentList", momoDepartmentList);
		
		//這裡是抓CRS用戶帳號
		mv.addObject("accounts", accountService.findAccountList());

		return mv;
	}
	
	/**
	 * 核帳報表權限控管 顯示清單
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(FIND_MOACCOUNT_MAP_ACCOUNT_LIST_URI)
	public ResponseEntity<?> findMoAccountMapAccount(MoAccountMapAccountConditionVo condition, HttpServletRequest request) {
		QueryResultVo result = accountService.getMoAccountMapAccountList(condition);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	 * 核帳報表權限控管 刪除項目
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(DELETE_MOACCOUNT_MAP_ACCOUNT)
	public ResponseEntity<?> deleteMoAccountMapAccount (MoAccountMapAccountConditionVo condition, HttpServletRequest request) {
		String schedule_id = "核帳報表權限管理_刪除資料" + DateUtilsEx.dateFormat().format(new Date());
		String functionId = request.getParameter("functionId");
	    String moDeptNo = request.getParameter("moDeptNo");
	    String crsAccountId = request.getParameter("crsAccountId");
	    String requestId = request.getParameter("requestId");
	    String functionTitle = request.getParameter("functionTitle");
	    
	    HttpSession session = request.getSession();
		UserInfoVo userInfo = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		Long accountId = userInfo.getAccountId();
		log.info(commonFormat, schedule_id, "執行此筆刪除者id:"+accountId, "functionId:", functionId, "moDeptNo:", moDeptNo, "crsAccountId:", crsAccountId,"requestId:",requestId,"functionTitle:",functionTitle);
		
		accountService.deleteMoAccountMapAccount(schedule_id,functionId,moDeptNo,crsAccountId,requestId,functionTitle);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 核帳報表權限控管 新增項目
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(INSERT_MOACCOUNT_MAP_ACCOUNT)
	public ResponseEntity<?> insertMoAccountMapAccount (MoAccountMapAccountConditionVo condition, HttpServletRequest request) {
		String schedule_id = "核帳報表權限管理_新增資料" + DateUtilsEx.dateFormat().format(new Date());
		String functionId = request.getParameter("functionId");
	    String departmentId = request.getParameter("departmentId");
	    String crsAccountId = request.getParameter("crsAccountId");
	    String requestId = request.getParameter("requestId");
	    String functiontitle = request.getParameter("functiontitle");
	    
	    HttpSession session = request.getSession();
		UserInfoVo userInfo = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		Long accountId = userInfo.getAccountId();
		log.info(commonFormat, schedule_id, "執行此筆新增者id:"+accountId, "functionId:", functionId, "departmentId:", departmentId, "crsAccountId:", crsAccountId,"requestId:",requestId,"functiontitle:",functiontitle);
		
		accountService.insertMoAccountMapAccount(schedule_id,functionId,departmentId,crsAccountId,requestId,functiontitle);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 每月兌幣總額API開關  進入畫面
	 * @return
	 */
	@GetMapping(FIND_REFEEM_TOTAL_API)
	public ModelAndView findRedeemTotalApi() {
		ModelAndView mv = new ModelAndView("account/switchOfRedeemTotalApi");
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/switchOfRedeemTotalApi");
		
		//這裡是抓momo公司單位清單
		List<momoDepartmentVo> momoDepartmentList = new ArrayList<momoDepartmentVo>();
		List<String> momoDepartmentStr_List = reportService.findmomoDepartmentList1();
		for(String strs :  momoDepartmentStr_List) {
			momoDepartmentVo vo = new momoDepartmentVo();
			MOAccountEntity result = moacRepo.findBydepartmentId1(strs);
			//checkmarx弱掃
			result = JsonUtil.jsonToPojo(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(result))), MOAccountEntity.class);
			DepartmentEntity entity1 = departmentRepo.findBydepartmentId(result.getDepartmentId());
			vo.setDepartmentId(result.getDepartmentId());
			vo.setDepartmentName(strs+"_"+entity1.getDepartmentName());
			momoDepartmentList.add(vo);
		}
		mv.addObject("departmentList", momoDepartmentList);
		
		return mv;
	}
	
	/**
	 *每月兌幣總額API開關 顯示清單
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(FIND_REFEEM_TOTAL_API)
	public ResponseEntity<?> findRedeemTotalApi(RedeemTotalApiVo condition, HttpServletRequest request) {
		QueryResultVo result = accountService.getRedeemTotalApiList(condition);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	 * 兌幣總額API 刪除項目
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(DELETE_REFEEM_TOTAL_API)
	public ResponseEntity<?> deleteRedeemTotalApi (RedeemTotalApiVo condition, HttpServletRequest request) {
		String schedule_id = "兌幣總額API_刪除資料" + DateUtilsEx.dateFormat().format(new Date());
	    String moDeptNo = request.getParameter("moDeptNo");
	    String apiUrl = request.getParameter("apiUrl");
	    String requestId = request.getParameter("requestId");
	    
	    HttpSession session = request.getSession();
		UserInfoVo userInfo = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		Long accountId = userInfo.getAccountId();
		log.info(commonFormat, schedule_id, "執行此筆刪除者id:"+accountId, "moDeptNo:", moDeptNo, "apiUrl:", apiUrl, "requestId:", requestId);
		
		accountService.deleteRedeemTotalApi(schedule_id,moDeptNo,apiUrl,requestId);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 兌幣總額API 新增項目
	 * @param condition
	 * @param request
	 * @return
	 */
	@PostMapping(INSERT_REFEEM_TOTAL_API)
	public ResponseEntity<?> insertRedeemTotalApi (RedeemTotalApiVo condition, HttpServletRequest request) {
		String schedule_id = "兌幣總額API_新增資料" + DateUtilsEx.dateFormat().format(new Date());
	    String departmentId = request.getParameter("departmentId");
	    String apiUrl = request.getParameter("apiUrl");
	    String requestId = request.getParameter("requestId");
	    
	    HttpSession session = request.getSession();
		UserInfoVo userInfo = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
		Long accountId = userInfo.getAccountId();
		log.info(commonFormat, schedule_id, "執行此筆新增者id:"+accountId, "departmentId:", departmentId, "apiUrl:", apiUrl, "requestId:", requestId);
		
		accountService.insertRedeemTotalApi(schedule_id,departmentId,apiUrl,requestId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping(FIND_ACCOUNT_ACTION_HISTORY)
	public ModelAndView findAccountActionHistory() {
		ModelAndView mv = new ModelAndView("account/findAccountActionHistory");
		
		mv.addObject("menudata", "6");
		mv.addObject("menuop", "account/findAccountActionHistory");
		mv.addObject("executeAccountList", accountService.getExecuteAccountList(1L));
		mv.addObject("accountList", accountService.getAccountList());

		return mv;
	}
	
	@PostMapping(FIND_ACCOUNT_ACTION_HISTORY)
	public ResponseEntity<?> findAccountActionHistory(@RequestParam(value="executeAccountId",defaultValue = "")Long executeAccountId, @RequestParam(value="accountId",defaultValue = "")Long accountId, @RequestParam(value="requestId",defaultValue = "")String requestId) {
		

		

		try {

			List<AccountActionHistoryDto> accountActionHistoryDto = accountService.getAccountActionHistoriesByCriteria(executeAccountId, accountId, requestId);

			return new ResponseEntity<>(accountActionHistoryDto, HttpStatus.OK);
		} catch (Exception e) {
			log.error("AccountController findAccountActionHistory Error: {}", e.getMessage(), e);

			return getErrorResponse();
		}
	}
	
	
}
