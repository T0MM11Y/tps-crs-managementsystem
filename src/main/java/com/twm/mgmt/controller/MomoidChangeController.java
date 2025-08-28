package com.twm.mgmt.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.enums.CrsErrorCode;
import com.twm.mgmt.model.common.MailVo;
import com.twm.mgmt.model.common.MenuVo;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.ReceiveInfoVo;
import com.twm.mgmt.model.common.RespVo;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.model.momoidChange.MomoUpdateIdResponse;
import com.twm.mgmt.model.momoidChange.MomoidChangeVo;
import com.twm.mgmt.persistence.dto.MomoidChangeMainDto;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.CampaignMainEntity;
import com.twm.mgmt.persistence.entity.ContractInfoEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeApprovalEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeMainEntity;
import com.twm.mgmt.persistence.entity.MomoidChangePictureEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeSmsEntity;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.persistence.repository.CampaignMainRepository;
import com.twm.mgmt.persistence.repository.ContractInfoRepository;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeApprovalRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeListRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeMainRepository;
import com.twm.mgmt.service.MomoidChangeService;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.CommonUtils;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.StringUtilsEx;


@Controller
@RequestMapping("/momoidChange")
public class MomoidChangeController extends BaseController {
	
	private static final String MOMOID_CHANGE_SETTING_STEP1 = "/momoidChangeSettingStep1";
	
	private static final String MOMOID_CHANGE_APPROVAL = "/momoidChangeApproval";
	
	private static final String MOMOID_CHANGE_SETTING_STEP1_MOMOID_CHANGE_APPROVAL = MOMOID_CHANGE_SETTING_STEP1+ MOMOID_CHANGE_APPROVAL;
	
	private static final String FIND_MOMOID_CHANGE_RECORD = "/findMomoidChangeRecord";
	
	private static final String FIND_MOMOID_CHANGE = "/findMomoidChange";
	
	private static final String FIND_MOMOID_CHANGE_SETTING_STEP1 = "/find/momoidChangeSettingStep1";
	
	private static final String FIND_MOMOID_CHANGE_APPROVAL = "/findMomoidChangeApproval";
	
	private static final String FIND_MOMOID_CHANGE_APPROVAL_MOMOID_CHANGE_APPROVAL2 = FIND_MOMOID_CHANGE_APPROVAL+"/momoidChangeApproval2";
		
	private static final String MOMOID_CHANGE_SETTING_STEP1_CHECK = MOMOID_CHANGE_SETTING_STEP1+"/check";
	
	private static final String WITHDRAW_MOMOID_CHANGE_MAIN = "/withdraw/momoidChangeMain";
	
	private static final String SIGN_OFF_FLOW_CHART = "/signOffFlowChart";

	@Autowired
	private MomoidChangeService momoidChangeService;

	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private MomoidChangeListRepository momoidChangeListRepo;

	@Autowired
	private MomoidChangeMainRepository momoidChangeMainRepo;
	
	@Autowired
	private ContractInfoRepository contractInfoRepository;
	
	@Autowired
	private DepartmentRepository departmentRepo;
	
	@Autowired
	private MomoidChangeApprovalRepository momoidChangeApproval;
	

	/**
	 * 進入簡訊發送名單設定頁
	 * 判斷momoidChangeList的session物件是否有被建立，沒有就建立物件
	 * map1的key對應到畫面的欄位.
	 * readOnly是false，畫面沒有disabled。是true則有disabled
	 * @param request
	 * @return
	 */
	@GetMapping(MOMOID_CHANGE_SETTING_STEP1)
	public ModelAndView momoidChangeSettingStep1(HttpServletRequest request) {
		
		String api_id=MOMOID_CHANGE_SETTING_STEP1+dateFormat().format(new Date());
		ModelAndView mv = null;
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
					
			mv = new ModelAndView("momoidChange/momoidChangeSettingStep1");
			mv.addObject("menudata", "7");
			mv.addObject("menuop", "momoidChange/momoidChangeSettingStep1");

			List list3 = new ArrayList<>();

			if (request.getSession().getAttribute("momoidChangeList") == null) {

				Map map1 = new HashMap();

				map1.put("phoneNumber", "");
				map1.put("projectCode", "");
				map1.put("projectSeqNbr", "");
				map1.put("subid", "");
				map1.put("sendReason", "");
				map1.put("momoMemberId", "");
				map1.put("momoidChangeSmsEntity", new MomoidChangeSmsEntity());
				
				list3.add(map1);
				request.getSession().setAttribute("momoidChangeList", list3);
			}

			//checkmarx弱掃
			mv.addObject("momoidChangeList", JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(request.getSession().getAttribute("momoidChangeList")))), Map.class));
			mv.addObject("readOnly", false);		

		} catch (Exception e) {
			
			e.printStackTrace();
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
		}

		return mv;
	}

	
	/**
	 * 簡訊發送名單設定頁點按完成鈕
	 * str是前端的JSON.stringify($("#totalForm").serializeArray())
	 * size是7表示一張卡片有7個欄位，每七個欄位就分割成一個List，變成List<List<Map<String, Object>>>
	 * 把分割好的List<List<Map<String, Object>>>用for迴圈整理成List<Map<String, Object>>的形式，方便前端thymeleaf讀取值，變數名稱為list3。
	 * 按下完成鈕，建立Session物件
	 * @param request
	 * @param str
	 * @return
	 * @throws ParseException
	 * @throws IOException 
	 */
	@RequestMapping(MOMOID_CHANGE_SETTING_STEP1)
	@ResponseBody
	public ResponseEntity<?> momoidChangeSendingSave(HttpServletRequest request,  String str,@RequestParam(value="file",required=false) MultipartFile[] files)
			throws ParseException, IOException {
		
		String api_id=MOMOID_CHANGE_SETTING_STEP1+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			//checkmarx弱掃
			str = StringEscapeUtils.escapeHtml(str);

			List momoidChangeList = new ArrayList<>();
						
				GsonBuilder builder = new GsonBuilder();
				builder.setPrettyPrinting();
				Gson gson = builder.create();
				//checkmarx弱掃
				List<Map<String, Object>> list = gson.fromJson(ESAPI.encoder().decodeForHTML(str), List.class);
				int size = 7;
				final AtomicInteger counter = new AtomicInteger();
				List<List<Map<String, Object>>> subLists = new ArrayList<>(list.stream().collect(Collectors.groupingBy((e) -> {
					int key = counter.getAndIncrement() / size;
					return key;
				})).values());

				Map momoidChangeListMap = new HashMap();
				

				for (List<Map<String, Object>> list2 : subLists) {
					for (Map<String, Object> map2 : list2) {
						momoidChangeListMap.put(map2.get("name"), map2.get("value"));

					}
					momoidChangeList.add(momoidChangeListMap);
					momoidChangeListMap.put("momoidChangeSmsEntity", new MomoidChangeSmsEntity());
					momoidChangeListMap = new HashMap();

				}
				
				for (int i = 0; i < momoidChangeList.size(); i++) {
					Map map = (Map)momoidChangeList.get(i);
					String momoMemberId = (String)map.get("momoMemberId");

					if (StringUtils.isBlank(momoMemberId)) {
						return new ResponseEntity<>("用戶使用中 momo 客編資料缺漏，請點按對應用戶檢查鈕喔！ ", HttpStatus.BAD_REQUEST);
					}
					

				}
				

				
			
			request.getSession().setAttribute("momoidChangeList", momoidChangeList);
			
			if (files!=null) {
				String[] fileNames = new String[15];
				byte[][] fileBytes = new byte[15][];
				for(int i = 0 ; i < files.length;i++) {
					MultipartFile file = files[i];
					fileNames[i] = file.getOriginalFilename();
					fileBytes[i]=file.getBytes();
				}
				request.getSession().setAttribute("momoidChangeFileNames", fileNames);
				request.getSession().setAttribute("momoidChangeFileBytes", fileBytes);
			}
			

			
		
			
			

			return new ResponseEntity<>("儲存成功", HttpStatus.OK);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
			
		}
	}

	
	
	/**
	 * 簡訊發送名單設定頁點按完成鈕鈕進入簡訊簽核人員設定設定頁
	 * 設定簽核人員，取得已啟用且有簽核權限的部門同仁不包含指定的人
	 * 不包含指定的人是自己。
	 * 
	 * @param request
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@GetMapping(MOMOID_CHANGE_SETTING_STEP1_MOMOID_CHANGE_APPROVAL)
	public ModelAndView campaigmomoidChangeApprovalnSetting2(HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		String api_id=MOMOID_CHANGE_SETTING_STEP1_MOMOID_CHANGE_APPROVAL+dateFormat().format(new Date());
		ModelAndView mv = null;
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			mv = new ModelAndView("momoidChange/momoidChangeApproval");
			mv.addObject("menudata", "7");
			mv.addObject("menuop", "momoidChange/momoidChangeSettingStep1");

			UserInfoVo vo = (UserInfoVo) request.getSession().getAttribute(CrsConstants.USER_INFO);

			List<Long> longList = new ArrayList<Long>();
			longList.add(vo.getAccountId());

			List<AccountEntity> approvalList = accountRepo.findByDepartmentIdAndApprovable(longList, vo.getDepartmentId());
			//checkmarx弱掃
			approvalList =  JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(approvalList))), AccountEntity.class);

			//approvalList = new ObjectMapper().readValue(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(approvalList.toString())), new TypeReference<List<AccountEntity>>() {});
			mv.addObject("approvalList", approvalList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			e.printStackTrace();
		}

		return mv;
	}

	
	/**
	 * 點按簡訊簽核人員設定頁的確定並送簽鈕
	 * 設定簽核人員，將Session的物件momoidChangeList寫入db
	 * @param momoidChangeVo
	 * @param request
	 * @return
	 * @throws ParseException
	 * @throws MalformedURLException
	 */
	@PostMapping(MOMOID_CHANGE_SETTING_STEP1_MOMOID_CHANGE_APPROVAL)
	@ResponseBody
	public ResponseEntity<?> momoidChangeSettingStep1MomoidChangeApproval(MomoidChangeVo momoidChangeVo,
			HttpServletRequest request) throws ParseException, MalformedURLException {
		String api_id=MOMOID_CHANGE_SETTING_STEP1_MOMOID_CHANGE_APPROVAL+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");	
			
			if(momoidChangeVo.getApprovalId() == null) {
					return getResponse(CrsErrorCode.VALIDATE_ERROR, null, HttpStatus.BAD_REQUEST);
				}

			try {
				UserInfoVo userInfoVo = (UserInfoVo) request.getSession().getAttribute(CrsConstants.USER_INFO);
				momoidChangeVo.setUserInfoVo(userInfoVo);
				momoidChangeVo.setListMap((List<Map<String, Object>>) request.getSession().getAttribute("momoidChangeList"));								
				momoidChangeVo.setMomoidChangeFileNames((String[]) request.getSession().getAttribute("momoidChangeFileNames"));				
				momoidChangeVo.setMomoidChangeFileBytes((byte[][]) request.getSession().getAttribute("momoidChangeFileBytes"));
				
				momoidChangeService.saveApproval(momoidChangeVo,request);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("MomoidChangeController momoidChangeSettingStep1MomoidChangeApproval Error: {}", e.getMessage(), e);
				return getErrorResponse();
				
			}
			
			request.getSession().removeAttribute("momoidChangeList");
			request.getSession().removeAttribute("momoidChangeFileNames");
			request.getSession().removeAttribute("momoidChangeFileBytes");
			return getSuccessResponse("儲存成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
		}
	}

	
	
	/**
	 * 進入簡訊發送紀錄查詢設定頁
	 * 部門別，平台PM與系統管理員可選擇All,服務PM僅能查看自已部門
	 */
	@GetMapping(FIND_MOMOID_CHANGE_RECORD)
	public ModelAndView findMomoidChange() {
		
		ModelAndView mv = null;
		String api_id=FIND_MOMOID_CHANGE_RECORD+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			mv = new ModelAndView("momoidChange/findMomoidChange");
			mv.addObject("menudata", "7");
			mv.addObject("menuop", "momoidChange/findMomoidChangeRecord");
			
			
			AccountEntity accountEntity = accountRepo.findByAccountID1(momoidChangeService.getAccountId());
			Long roleId = momoidChangeService.getRoleId();
			List<DepartmentEntity> departmentEntityList = null;
			
			if (1==roleId || 21 == roleId) {
				departmentEntityList = departmentRepo.findEnabledDepartment();
				
			} else if (23==roleId || 25 == roleId) {
				departmentEntityList = departmentRepo.getBydepartmentId(momoidChangeService.getDepartmentId());
			}
			
			
			departmentEntityList = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(departmentEntityList))), DepartmentEntity.class);
			//checkmarx弱掃
			mv.addObject("departmentEntityList", departmentEntityList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
		}

		return mv;
	}

	
	
	/**
	 * 點按簡訊發送紀錄查詢設定頁的查詢鈕
	 * 平台PM與系統管理員可選擇All,服務PM僅能查看自已部門
	 * @param MomoidChangeVo
	 * @return
	 */
	@PostMapping(FIND_MOMOID_CHANGE)
	public ResponseEntity<?> findMomoidChange(MomoidChangeVo MomoidChangeVo) {
		String api_id=FIND_MOMOID_CHANGE+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			Long roleId = momoidChangeService.getRoleId();
			if (23==roleId || 25 == roleId) {
				MomoidChangeVo.setDepartmentId(momoidChangeService.getDepartmentId().toString());
			}

			return new ResponseEntity<>(momoidChangeService.findMomoidChange(MomoidChangeVo), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
			
		}

	}

	
	
	/**
	 * 點按簡訊發送紀錄查詢的table的查看鈕，進入簡訊發送名單設定設定頁，只能讀取。
	 * momoidChangeMainId是前端table的系統單號，readOnly是true，disabled
	 * @param momoidChangeMainId
	 * @param isReEdit
	 * @return
	 */
	@RequestMapping(FIND_MOMOID_CHANGE_SETTING_STEP1)
	public ModelAndView findMomoidChangeSettingStep1(BigDecimal momoidChangeMainId, BigDecimal isReEdit) {
		String api_id=FIND_MOMOID_CHANGE_SETTING_STEP1+dateFormat().format(new Date());
		ModelAndView mv = null;
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			mv = new ModelAndView("momoidChange/momoidChangeSettingStep1");
			mv.addObject("menudata", "7");
			mv.addObject("menuop", "momoidChange/momoidChangeSettingStep1");
			mv.addObject("momoidChangeList", momoidChangeService.findByMomoidChangeMainId(momoidChangeMainId));
			mv.addObject("momoidChangePictureEntity",momoidChangeService.getMomoidChangePictureFindById(momoidChangeMainId));
			
			mv.addObject("readOnly", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
		}
		return mv;

	}
	
	
	
	
	/**
	 * 進入簡訊簽核紀錄查詢頁
	 * @return
	 */
	@GetMapping(FIND_MOMOID_CHANGE_APPROVAL)
	public ModelAndView findMomoidChangeApproval() {
		String api_id=FIND_MOMOID_CHANGE_APPROVAL+dateFormat().format(new Date());
		ModelAndView mv = null;
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			mv = new ModelAndView("momoidChange/findMomoidChangeApproval");
			mv.addObject("menudata", "7");
			mv.addObject("menuop", "momoidChange/findMomoidChangeApproval");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
		}


		return mv;
	}
	
	
	
	
	/**
	 * 點按簡訊簽核紀錄查詢頁的查詢鈕
	 * MomoidChangeVo是前端畫面上的欄位條件
	 * @param MomoidChangeVo
	 * @return
	 */
	@PostMapping(FIND_MOMOID_CHANGE_APPROVAL)
	public ResponseEntity<?> findMomoidChangeApproval(MomoidChangeVo MomoidChangeVo) {
		String api_id=FIND_MOMOID_CHANGE_APPROVAL+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			return new ResponseEntity<>(momoidChangeService.findMomoidChangeApproval(MomoidChangeVo), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
		}

	}
	
	
	
	/**
	 * 點按簡訊簽核紀錄查詢頁的table的簽核鈕或查看
	 * momoidChangeApproval2是簡訊簽核紀錄查詢頁的的小視窗頁的body
	 * id是系統單號，index是table第幾個row，從0開始數
	 * @param id
	 * @param index
	 * @return
	 */
	@PostMapping(MOMOID_CHANGE_APPROVAL)
	public ModelAndView displayCampaignDetail(BigDecimal id,BigDecimal index) {
		String api_id=MOMOID_CHANGE_APPROVAL+dateFormat().format(new Date());
		ModelAndView mv = null;
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			mv = new ModelAndView("momoidChange/momoidChangeApproval2 :: momoidChange__Approval");
			mv.addObject("momoidChangeList", momoidChangeService.findByMomoidChangeMainId(id));
			Map userNameAndDepartmentName = momoidChangeMainRepo.getUserNameAndDepartmentName(id);
			//checkmarx弱掃
			userNameAndDepartmentName = JsonUtil.jsonToPojo(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(userNameAndDepartmentName))), Map.class);
			mv.addObject("USER_NAME",userNameAndDepartmentName.get("USER_NAME"));
			mv.addObject("DEPARTMENT_NAME", userNameAndDepartmentName.get("DEPARTMENT_NAME"));
			mv.addObject("MOMOID_CHANGE_APPROVAL_ID", userNameAndDepartmentName.get("MOMOID_CHANGE_APPROVAL_ID"));
			mv.addObject("MOMOID_CHANGE_MAIN_ID", userNameAndDepartmentName.get("MOMOID_CHANGE_MAIN_ID"));
			mv.addObject("index", index);
			MomoidChangeApprovalEntity momoidChangeApprovalEntity = momoidChangeApproval.findByMomoidChangeMainId(id);
			mv.addObject("momoidChangeApprovalEntity", momoidChangeApprovalEntity);
			
		

			

			MomoidChangePictureEntity momoidChangePictureEntity = momoidChangeService.getMomoidChangePictureFindById2(id);
			if (momoidChangePictureEntity!=null) {
				byte[] picture1File = momoidChangePictureEntity.getPicture1File();
				byte[] picture2File = momoidChangePictureEntity.getPicture2File();
				byte[] picture3File = momoidChangePictureEntity.getPicture3File();
				byte[] picture4File = momoidChangePictureEntity.getPicture4File();
				byte[] picture5File = momoidChangePictureEntity.getPicture5File();
				byte[] picture6File = momoidChangePictureEntity.getPicture6File();
				byte[] picture7File = momoidChangePictureEntity.getPicture7File();
				byte[] picture8File = momoidChangePictureEntity.getPicture8File();
				byte[] picture9File = momoidChangePictureEntity.getPicture9File();
				byte[] picture10File = momoidChangePictureEntity.getPicture10File();
				byte[] picture11File = momoidChangePictureEntity.getPicture11File();
				byte[] picture12File = momoidChangePictureEntity.getPicture12File();
				byte[] picture13File = momoidChangePictureEntity.getPicture13File();
				byte[] picture14File = momoidChangePictureEntity.getPicture14File();
				byte[] picture15File = momoidChangePictureEntity.getPicture15File();
							
				String picture1FileImage = Base64.getEncoder().encodeToString(resizeImage(picture1File));
				String picture2FileImage = Base64.getEncoder().encodeToString(resizeImage(picture2File));
				String picture3FileImage = Base64.getEncoder().encodeToString(resizeImage(picture3File));
				String picture4FileImage = Base64.getEncoder().encodeToString(resizeImage(picture4File));
				String picture5FileImage = Base64.getEncoder().encodeToString(resizeImage(picture5File));
				String picture6FileImage = Base64.getEncoder().encodeToString(resizeImage(picture6File));
				String picture7FileImage = Base64.getEncoder().encodeToString(resizeImage(picture7File));
				String picture8FileImage = Base64.getEncoder().encodeToString(resizeImage(picture8File));
				String picture9FileImage = Base64.getEncoder().encodeToString(resizeImage(picture9File));
				String picture10FileImage = Base64.getEncoder().encodeToString(resizeImage(picture10File));
				String picture11FileImage = Base64.getEncoder().encodeToString(resizeImage(picture11File));
				String picture12FileImage = Base64.getEncoder().encodeToString(resizeImage(picture12File));
				String picture13FileImage = Base64.getEncoder().encodeToString(resizeImage(picture13File));
				String picture14FileImage = Base64.getEncoder().encodeToString(resizeImage(picture14File));
				String picture15FileImage = Base64.getEncoder().encodeToString(resizeImage(picture15File));
				
				if (StringUtils.isNotBlank(picture1FileImage)) {
					mv.addObject("picture1FileFormat", getFormat(new ByteArrayInputStream(picture1File)));
					mv.addObject("picture1FileImage", picture1FileImage);
				}
				if (StringUtils.isNotBlank(picture2FileImage)) {
					mv.addObject("picture2FileFormat", getFormat(new ByteArrayInputStream(picture2File)));
					mv.addObject("picture2FileImage", picture2FileImage);
				}
				if (StringUtils.isNotBlank(picture3FileImage)) {
					mv.addObject("picture3FileFormat", getFormat(new ByteArrayInputStream(picture3File)));
					mv.addObject("picture3FileImage", picture3FileImage);
				}
				if (StringUtils.isNotBlank(picture4FileImage)) {
					mv.addObject("picture4FileFormat", getFormat(new ByteArrayInputStream(picture4File)));
					mv.addObject("picture4FileImage", picture4FileImage);
				}
				if (StringUtils.isNotBlank(picture5FileImage)) {
					mv.addObject("picture5FileFormat", getFormat(new ByteArrayInputStream(picture5File)));
					mv.addObject("picture5FileImage", picture5FileImage);
				}
				if (StringUtils.isNotBlank(picture6FileImage)) {
					mv.addObject("picture6FileFormat", getFormat(new ByteArrayInputStream(picture6File)));
					mv.addObject("picture6FileImage", picture6FileImage);
				}
				if (StringUtils.isNotBlank(picture7FileImage)) {
					mv.addObject("picture7FileFormat", getFormat(new ByteArrayInputStream(picture7File)));
					mv.addObject("picture7FileImage", picture7FileImage);
				}
				if (StringUtils.isNotBlank(picture8FileImage)) {
					mv.addObject("picture8FileFormat", getFormat(new ByteArrayInputStream(picture8File)));
					mv.addObject("picture8FileImage", picture8FileImage);
				}
				if (StringUtils.isNotBlank(picture9FileImage)) {
					mv.addObject("picture9FileFormat", getFormat(new ByteArrayInputStream(picture9File)));
					mv.addObject("picture9FileImage", picture9FileImage);
				}
				if (StringUtils.isNotBlank(picture10FileImage)) {
					mv.addObject("picture10FileFormat", getFormat(new ByteArrayInputStream(picture10File)));
					mv.addObject("picture10FileImage", picture10FileImage);
				}
				if (StringUtils.isNotBlank(picture11FileImage)) {
					mv.addObject("picture11FileFormat", getFormat(new ByteArrayInputStream(picture11File)));
					mv.addObject("picture11FileImage", picture11FileImage);
				}
				if (StringUtils.isNotBlank(picture12FileImage)) {
					mv.addObject("picture12FileFormat", getFormat(new ByteArrayInputStream(picture12File)));
					mv.addObject("picture12FileImage", picture12FileImage);
				}
				if (StringUtils.isNotBlank(picture13FileImage)) {
					mv.addObject("picture13FileFormat", getFormat(new ByteArrayInputStream(picture13File)));
					mv.addObject("picture13FileImage", picture13FileImage);
				}
				if (StringUtils.isNotBlank(picture14FileImage)) {
					mv.addObject("picture14FileFormat", getFormat(new ByteArrayInputStream(picture14File)));
					mv.addObject("picture14FileImage", picture14FileImage);
				}
				if (StringUtils.isNotBlank(picture15FileImage)) {
					mv.addObject("picture15FileFormat", getFormat(new ByteArrayInputStream(picture15File)));
					mv.addObject("picture15FileImage", picture15FileImage);
				}
			} 

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
		}

		return mv;
	}
	
	
	
	



	/**
	 * 調整圖像尺寸
	 * @param originalImage
	 * @return
	 * @throws IOException
	 */
	public static byte[] resizeImage(byte[] originalImage) throws IOException {
		
		if (originalImage!=null) {
			InputStream in = new ByteArrayInputStream(originalImage);
			BufferedImage originalBufferedImage = ImageIO.read(in);
			int originalWidth = originalBufferedImage.getWidth();
			int originalHeight = originalBufferedImage.getHeight();
			//int desiredHeight =(int)(originalHeight*((double)800/originalWidth));
			
			BufferedImage resizedBuffdredImage = new BufferedImage(originalWidth,originalHeight,originalBufferedImage.getType());
			Graphics2D g = resizedBuffdredImage.createGraphics();
			g.drawImage(originalBufferedImage,0,0,originalWidth,originalHeight,null);
			g.dispose();
			String format = getFormat(new ByteArrayInputStream(originalImage));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(resizedBuffdredImage,format,out);
			
			return out.toByteArray();
		}
		return new byte[0];

	}
	
	/**
	 * 輔助方法來檢測圖像格式
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static String getFormat(InputStream is) throws IOException {
		ImageInputStream iis = ImageIO.createImageInputStream(is);
		Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
		if(iter.hasNext()) {
			ImageReader reader = iter.next();
			iis.close();
			return reader.getFormatName();
		}
		iis.close();
		return null;
	}
	
	/**
	 * 點按簡訊簽核紀錄查詢的table的簽核鈕，出pop up小視窗，進行簽核同意/駁回。
	 * momoidChangeVo是pop up小視窗的欄位
	 * @param momoidChangeVo
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@PostMapping(FIND_MOMOID_CHANGE_APPROVAL_MOMOID_CHANGE_APPROVAL2)
	@ResponseBody
	public ResponseEntity<?> findMomoidChangeApprovalMomoidChangeApproval2(MomoidChangeVo momoidChangeVo,HttpServletRequest request
			) throws ParseException {
		
		String api_id=FIND_MOMOID_CHANGE_APPROVAL_MOMOID_CHANGE_APPROVAL2+dateFormat().format(new Date());
		log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
				
		try {
			return new ResponseEntity<>(momoidChangeService.setMomoidChangeApprovalMomoidChangeApproval2(momoidChangeVo, request), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
		}

		

		
	}
	
	
	
	
	/**
	 * 點按簡訊發送名單設定的卡片的檢查鈕
	 * str是前端的JSON.stringify($("#totalForm").serializeArray().push(aBtnsIndex))，aBtnsIndex是用來判斷哪顆按鈕
	 * size是7表示一張卡片有7個欄位，每七個欄位就分割成一個List，變成List<List<Map<String, Object>>>
	 * 檢查續約流水號、專案代碼、subid。查合約流水號及用戶使用中的 momo客編
	 * @param str
	 * @return
	 */
	@PostMapping(MOMOID_CHANGE_SETTING_STEP1_CHECK)
	public ResponseEntity<?> momoidChangeSettingStep1(@RequestBody String str) {
		String api_id=MOMOID_CHANGE_SETTING_STEP1_CHECK+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			GsonBuilder builder = new GsonBuilder();
			builder.setPrettyPrinting();
			Gson gson = builder.create();
			List<Map<String, Object>> list = gson.fromJson(str, List.class);
			
			int index = list.size()-1;
			Map map3 = list.get(index);
			
			list.remove(index);
			
			
			int size = 7;
			final AtomicInteger counter = new AtomicInteger();
			List<List<Map<String, Object>>> subLists = new ArrayList<>(list.stream().collect(Collectors.groupingBy((e) -> {
				int key = counter.getAndIncrement() / size;
				return key;
			})).values());

			Map map1 = new HashMap();
			List list3 = new ArrayList<>();

			for (List<Map<String, Object>> list2 : subLists) {
				for (Map<String, Object> map2 : list2) {
					map1.put(map2.get("name"), map2.get("value"));

				}
				list3.add(map1);
				map1 = new HashMap();

			}
			
			int aBtnsIndex = ((Double)map3.get("aBtnsIndex")).intValue();
			Map map4 = (Map) list3.get(aBtnsIndex);
			String projectSeqNbr2 =(String)map4.get("projectSeqNbr") ;
			String projectCode = (String)map4.get("projectCode");
			String subid = (String)map4.get("subid");
			BigDecimal contractId = contractInfoRepository.getContractId(projectSeqNbr2, projectCode, subid);
			
			if(contractId == null ) {
				contractId = BigDecimal.ZERO;
			}
			
			String momoMemberId = contractInfoRepository.getMomoMemberId(projectSeqNbr2, projectCode, subid,contractId);
			MomoidChangeVo momoidChangeVo = new MomoidChangeVo();
			momoidChangeVo.setMomoMemberId(momoMemberId);
			momoidChangeVo.setABtnsIndex(aBtnsIndex);
			
			if (StringUtils.isBlank(momoMemberId)) {
				//return new ResponseEntity<>("您輸入之專案代碼/續約流水號/SubID有誤，系統查無該用戶 momo 客編資料，請調整後再進行檢查喔! ", HttpStatus.BAD_REQUEST);
				List<String> list4 = new ArrayList<String>();
				list4.add("您輸入之專案代碼/續約流水號/SubID有誤，系統查無該用戶 momo 客編資料，請調整後再進行檢查喔！ ");
				HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();
				hashMap.put("查無用戶使用中 momo 客編資料", list4);
				RespVo respVo = new RespVo();
				respVo.setMessage("查無用戶使用中 momo 客編資料");
				respVo.setErrorMessages(hashMap);
				
				 return getResponse(respVo,HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity<>(momoidChangeVo, HttpStatus.OK);
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
		}
		
		

	}
	
	
	
	/**
	 * 點按簡訊發送紀錄查詢的table的撤回鈕
	 * momoidChangeVo裡帶的是前端table的系統單號跟index，index是table的row，從0開始數
	 * @param momoidChangeVo
	 * @return
	 */
	@RequestMapping(WITHDRAW_MOMOID_CHANGE_MAIN)
	@ResponseBody
	public ResponseEntity<?> withdrawMomoidChangeMain(MomoidChangeVo momoidChangeVo) {
		String api_id=WITHDRAW_MOMOID_CHANGE_MAIN+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			MomoidChangeMainEntity momoidChangeMainEntity = momoidChangeService.withdrawMomoidChangeMain(momoidChangeVo);

			return new ResponseEntity<>(momoidChangeVo, HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
		}



	}
	
	
	/**
	 * 點按簡訊發送紀錄查詢的table的簽核流程圖鈕
	 * approvalId是table的系統單號
	 * @param approvalId
	 * @return
	 */
	@RequestMapping(SIGN_OFF_FLOW_CHART)
	@ResponseBody
	public ResponseEntity<?> signOffFlowChart(@RequestBody String approvalId) {
		String api_id=SIGN_OFF_FLOW_CHART+dateFormat().format(new Date());
		try {
			
			log.info(commonFormat,api_id,"-","-","-","-","-","-","-");
			
			MomoidChangeMainDto signOffFlowChart = momoidChangeService.signOffFlowChart(approvalId);
			
			String comment_INFO = signOffFlowChart.getCOMMENT_INFO();
			String opinion = signOffFlowChart.getOPINION();
			if (StringUtils.isBlank(comment_INFO) && !("待簽核".equals(opinion) || "撤回作廢".equals(opinion))) {
				signOffFlowChart.setCOMMENT_INFO("無簽核意見");
			}

			return new ResponseEntity<>(signOffFlowChart, HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info(commonFormat,api_id,"-","-","-","-","-","-",e.toString());
			log.error(errorFormat,api_id,"-","-","-","-",e.toString());
			return getErrorResponse();
		}
	}
	


}
