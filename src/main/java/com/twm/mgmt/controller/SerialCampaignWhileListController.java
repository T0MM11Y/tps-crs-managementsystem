package com.twm.mgmt.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.twm.mgmt.Exception.ModelAndViewException;
import com.twm.mgmt.model.common.MailVo;
import com.twm.mgmt.persistence.entity.AccountEntity;

import com.twm.mgmt.service.SerialCampaignWhileListService;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.CommonUtils;

import com.twm.mgmt.validator.serialCampaignWhileList.SerialCampaignWhileListConditionVoValidator;

@RequestMapping("/" + SerialCampaignWhileListController.SERIAL_CAMPAIGN_WHILE_LIST)
@Controller
public class SerialCampaignWhileListController extends BaseController {

	public static final String SERIAL_CAMPAIGN_WHILE_LIST = "serialCampaignWhileList";

	private static final String SETTING = "/setting";

	private static final String STEP1 = "/step1";

	private static final String FIND_WHILE_LIST = "/findWhileList";

	@Value("${EPAPI.master}")
	public String EpApiMaster;

	@Value("${EPAPI.userId}")
	public String EpApiId;

	@Value("${EPAPI.userPwd}")
	public String EpApiPwd;

	@Value("${crs.mail.from}")
	private String crs_mail_from;

	@Value("${rc.recieve.secrect.key}")
	private String rc_recieve_secrect_key;

	@Value("${rc.recieve.secrect.iv}")
	private String rc_recieve_secrect_iv;
	
	private final String ccEmail = "PattyChiang@taiwanmobile.com";

	@Autowired
	private SerialCampaignWhileListService serialCampaignWhileListService;

	/**
	 * 進入序號簡訊白名單新增第一頁
	 * 
	 * @return
	 */
	@GetMapping(SETTING + STEP1)
	public ModelAndView settingStep1(@RequestParam(defaultValue = "") BigDecimal campaignDetailId,
			@RequestParam(defaultValue = "") BigDecimal reportId, @RequestParam(defaultValue = "") BigDecimal batchId) {
		
		String api_id = SETTING + STEP1 + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		try {
			ModelAndView mv = null;
			mv = new ModelAndView("serialCampaignWhileList/settingStep1");
			mv.addObject("menudata", "2");

			mv.addObject("approvalUser", serialCampaignWhileListService.getApprovalUser());

			if (campaignDetailId != null && reportId != null) {
				mv.addObject("menuop", SERIAL_CAMPAIGN_WHILE_LIST + FIND_WHILE_LIST);
				mv.addAllObjects(serialCampaignWhileListService.editView(campaignDetailId, reportId, batchId));

				mv.addObject("edit", false);
				mv.addObject("h1", "編輯");
				mv.addObject("btnAdd", "更新");
				return mv;
			} else {
				mv.addObject("menuop", SERIAL_CAMPAIGN_WHILE_LIST + SETTING + STEP1);
				mv.addObject("edit", true);
				mv.addObject("h1", "新增");
				mv.addObject("btnAdd", " 新增");
				return mv;

			}
		} catch (Exception e) {
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString());
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
			throw new ModelAndViewException();
		}
	}

	/**
	 * 序號簡訊白名單新增第一頁的欄位邏輯檢查
	 * 
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = { SETTING + "/nextSettingStep1" })
	public ResponseEntity<?> nextSettingStep1(String campaignName, String campaignInfo, MultipartFile projectFile)
			throws IOException {
		String api_id;
		Map<String, String> response = null;
		try {
			api_id = SETTING + "/nextSettingStep1" + dateFormat().format(new Date());
			log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
			response = new HashMap<>();
			Map<String, String> map = new HashMap<>();
			// 检查發幣活動名稱和發幣活動說明是否填写
			if (campaignName == null || campaignName.isEmpty()) {
				map.put("campaignNameError", "發幣活動名稱");
			}
			if (campaignInfo == null || campaignInfo.isEmpty()) {
				map.put("campaignInfoError", "發幣活動說明");
			}

			// 检查文件是否上传
			if (projectFile == null || projectFile.isEmpty()) {
				map.put("fileError", "檔案上傳");
			}

			// 如果有错误，构建并返回错误消息
			if (!map.isEmpty()) {
				StringBuilder errorMessage = new StringBuilder("您尚有以下資料輸入不完全：<br>");
				map.forEach((key, value) -> errorMessage.append(value).append("<br>"));
				response.put("requiredError", errorMessage.toString());
				return ResponseEntity.badRequest().body(response);
			}
			
			// 验证名称和说明
			if (campaignName != null && campaignName.length() > 30) {
				response.put("campaignNameError", "您的 發幣活動名稱 輸入字數超過限制，該欄位上限為 30 個字，請調整後重新輸入喔！");
			}
			if (campaignInfo != null && campaignInfo.length() > 25) {
				response.put("campaignInfoError", "您的 發幣活動說明 輸入字數超過限制，該欄位上限為 25 個字，請調整後重新輸入喔！");
			}

			// 检查文件格式
			if (projectFile != null && !projectFile.getOriginalFilename().endsWith(".csv")) {
				response.put("fileError", "您上傳之檔案格式不為 csv 檔，還請確認檔案格式後再進行上傳作業！");
			}

			if (!response.isEmpty()) {
				return ResponseEntity.badRequest().body(response);
			}
		} catch (Exception e1) {
			sendErrorEmail(e1);
			throw new ModelAndViewException();
		}

		InputStreamReader in = new InputStreamReader(projectFile.getInputStream(), StandardCharsets.UTF_8);
		try {

			BufferedReader reader = new BufferedReader(in);
			Map<String, Integer> subIdCounts = new HashMap<>();
			String[] columnNames = { "Sub ID", "序號", "金額", "效期", "序號期限", "類別", "短網址" };
			Map<String, Integer> emptyFieldCounts = new LinkedHashMap<>();
			String firstExpiryDate = null;
			String firstSerialExpiryDate = null;
			boolean inconsistentExpiryDate = false;
			boolean inconsistentSerialExpiryDate = false;

			for (String columnName : columnNames) {
				emptyFieldCounts.put(columnName, 0);
			}

			String line;
			reader.readLine(); // 跳過標題行
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",", -1);
				IntStream.range(0, values.length).forEach(i -> {
					if (values[i].trim().isEmpty()) {
						emptyFieldCounts.put(columnNames[i], emptyFieldCounts.get(columnNames[i]) + 1);
					}
				});

				// 檢查效期是否一致
				if (firstExpiryDate == null) {
					firstExpiryDate = values[3].trim(); // 假設效期位於第四列
				} else if (StringUtils.isNotBlank(values[3]) && StringUtils.isNotBlank(firstExpiryDate)
						&& !values[3].trim().equals(firstExpiryDate)) {
					inconsistentExpiryDate = true;
				}

				// 檢查序號期限是否一致
				if (firstSerialExpiryDate == null) {
					firstSerialExpiryDate = values[4].trim(); // 假設序號期限位於第五列
				} else if (StringUtils.isNotBlank(values[4]) && StringUtils.isNotBlank(firstSerialExpiryDate)
						&& !values[4].trim().equals(firstSerialExpiryDate)) {
					inconsistentSerialExpiryDate = true;
				}

				// 檢查 Sub ID 是否重複
				String subId = values[0].trim();
				subIdCounts.put(subId, subIdCounts.getOrDefault(subId, 0) + 1);
			}

			// 構建錯誤消息
			boolean hasEmptyFields = emptyFieldCounts.values().stream().anyMatch(count -> count > 0);
			if (hasEmptyFields) {
				StringBuilder errorMessage = new StringBuilder("您上傳之檔案中共有");
				emptyFieldCounts.forEach((key, value) -> {
					if (value > 0) {
						errorMessage.append(key).append(" ").append(value).append(" 筆未填、");
					}
				});
				errorMessage.setLength(errorMessage.length() - 1); // 移除最後的頓號
				response.put("fileContentError", errorMessage.toString() + "，請調整後重新上傳喔！");
			}
			if (inconsistentExpiryDate) {
				response.put("expiryDateError", "您上傳之檔案中效期不一致，請調整後重新上傳喔！");
			}
			if (inconsistentSerialExpiryDate) {
				response.put("serialExpiryDateError", "您上傳之檔案中序號期限不一致，請調整後重新上傳喔！");
			}
		} catch (Exception e) {
			response.put("fileReadError", "文件讀取出錯，請檢查文件格式和內容");
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
		} finally {
			in.close();
		}

		if (!response.isEmpty()) {
			return ResponseEntity.badRequest().body(response);
		}

		// 处理文件和表单数据

		return ResponseEntity.ok("Form submitted successfully");
	}

	/**
	 * 序號簡訊白名單新增第二頁的欄位邏輯檢查
	 * 
	 * @return
	 * @throws IOException
	 */
	@PostMapping(SETTING + "/nextSettingStep2")
	public ResponseEntity<?> nextSettingStep2(@RequestParam("momoEventNo") String momoEventNo,
	        @RequestParam("fileUpload") MultipartFile fileUpload, @RequestParam("rewardDate") String rewardDate,
	        @RequestParam("orderNumber") String orderNumber, @RequestParam("payAccount") String payAccount)
	        throws IOException {
	    String api_id = SETTING + "/nextSettingStep2" + dateFormat().format(new Date());
	    log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
	    Map<String, String> response = new HashMap<>();
	    try {
	        // 檢查檔名格式
	        String fileName = fileUpload.getOriginalFilename();
	        if (!fileName.equals(momoEventNo + ".csv")) {
	            response.put("momoEventNoError", "您上傳之 momo 幣序號檔案檔名格式應為 : " + momoEventNo + ".csv" + "，請調整後重新上傳喔。");
	        }

	        // 檢查 CSV 檔案中的效期資料
	        boolean isValid = checkCsvContent(fileUpload, rewardDate);
	        if (!isValid) {
	            response.put("rewardDateError", "您上傳之檔案中的效期資料，應比序號發送時間大至少二個月以上，請調整後重新上傳喔。");
	        }

	        // 檢查 CSV 檔案中的序號期限資料
	        boolean isSerialPeriodValid = checkSerialPeriodContent(fileUpload, rewardDate);
	        if (!isSerialPeriodValid) {
	            response.put("serialPeriodError", "您上傳之檔案中的序號期限資料，應比序號發送時間大至少二個月以上，請調整後重新上傳喔。");
	        }

	        // 如果有錯誤，返回錯誤回應
	        if (!response.isEmpty()) {
	            return ResponseEntity.badRequest().body(response);
	        }

	        // 計算 CSV 文件中的總數量和總金額
	        InputStreamReader in = new InputStreamReader(fileUpload.getInputStream(), StandardCharsets.UTF_8);
	        try {
	            BufferedReader reader = new BufferedReader(in);
	            String line;
	            int totalRecords = 0;
	            int totalAmount = 0;
	            reader.readLine(); // 跳过标题行
	            while ((line = reader.readLine()) != null) {
	                String[] values = line.split(","); // Assuming comma-separated values
	                if (values.length > 3) { 
	                    totalRecords++;
	                    totalAmount += Integer.parseInt(values[2]); // 假设金额在第三列
	                }
	            }
	            response.put("totalRewardUsers", String.valueOf(totalRecords)); 
	            response.put("totalRewardAmount", String.valueOf(totalAmount)); 
	        } catch (Exception e) {
	            response.put("fileReadError", "文件讀取出錯，請檢查文件格式和內容");
	            log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
	            log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
				sendErrorEmail(e);				
	            return ResponseEntity.badRequest().body(response);
	        } finally {
	            in.close();
	        }

	        // 準備 API 請求資料
	        HashMap<String, Object> mapResponse = new HashMap<>();
	        List<String> poNumsList = new ArrayList<>();
	        poNumsList.add(orderNumber);
	        mapResponse.put("userId", EpApiId);
	        mapResponse.put("userPwd", EpApiPwd);
	        mapResponse.put("poNums", poNumsList);

	        Gson gson = new Gson();
	        String json = gson.toJson(mapResponse);
	        
	     // 定義信任所有證書的 TrustManager
	        TrustManager[] trustAllCerts = new TrustManager[] {
	                new javax.net.ssl.X509TrustManager() {
	                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                        return new java.security.cert.X509Certificate[0];
	                    }
	                    public void checkClientTrusted(
	                        java.security.cert.X509Certificate[] certs, String authType) {
	                    }
	                    public void checkServerTrusted(
	                        java.security.cert.X509Certificate[] certs, String authType) {
	                    }
	                }
	            };

	        HttpsURLConnection conn = null;
            URL connectto = new URL(EpApiMaster);
            conn = (HttpsURLConnection) connectto.openConnection();
            
         // 設置自定義的 SSLSocketFactory，信任所有證書
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());

         // 跳過主機名驗證
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);

	        // 發送 API 請求
	        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	        wr.writeBytes(json);
	        wr.flush();
	        wr.close();

	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
	        StringBuilder sb = new StringBuilder();
	        String line;
	        String strContext = "";
	        while ((line = br.readLine()) != null) {
	            sb.append(line + "\n");
	            strContext = line;
	        }
	        br.close();

	        log.info("po單號請求api回傳的訊息:"+strContext);
	        // 處理 API 回應
	        JsonObject convertedObject = new Gson().fromJson(strContext, JsonObject.class);

	        if (100 == convertedObject.get("result").getAsJsonObject().get("resultID").getAsInt()) {
	            JsonObject convertedPosObject = new Gson().fromJson(
	                    convertedObject.get("pos").toString().replace("[", "").replace("]", ""), JsonObject.class);

	            if ("00".equals(convertedPosObject.get("checkId").getAsString())) {
	                String prRequestorDept = convertedPosObject.get("poInfo").getAsJsonObject().get("prRequestorDept")
	                        .getAsString();
	                response.put("requisitionUnit", prRequestorDept); 
	                String prRequestorName = convertedPosObject.get("poInfo").getAsJsonObject().get("prRequestorName")
	                        .getAsString();
	                response.put("requisitioner", prRequestorName); 
	            }
	        }

	        // 返回成功回應
	        response.put("rewardDate", rewardDate.replace("-", "/"));
	        response.put("momoEventNo", momoEventNo);
	        response.put("payAccount", payAccount);
	        response.put("orderNumber", orderNumber);
	        return ResponseEntity.ok().body(response);
	    } catch (Exception e) {
	        response.put("exceptionError", "系統發生錯誤，請聯繫系統管理員Yvette Yang。");
	        log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
	        log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
	        return ResponseEntity.badRequest().body(response);
	    }
	}


	// 檢查 CSV 內容，包括效期檢查
	private boolean checkCsvContent(MultipartFile file, String rewardDate) throws Exception {
	    InputStreamReader in = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
	    try {
	        BufferedReader reader = new BufferedReader(in);
	        LocalDate referenceDate = LocalDate.parse(rewardDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        String line;
	        reader.readLine(); // 跳过标题行
	        while ((line = reader.readLine()) != null) {
	            String[] values = line.split(","); // Assuming comma-separated values
	            if (values.length > 4) {
	                String[] dateRange = values[3].split("~"); // Assuming expiry date is in the 4th column
	                if (dateRange.length > 1) {
	                    LocalDate endDate = LocalDate.parse(dateRange[1],
	                            DateTimeFormatter.ofPattern(generatePattern(dateRange[1])));
	                    if (ChronoUnit.MONTHS.between(referenceDate, endDate) < 2) {
	                        return false; // Expiry date is less than two months from the reference date
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.info(commonFormat, "-", "checkCsvContent方法", "-", "-", "失敗", "-", "-", e.toString(), e);
	        log.error(errorFormat, "-", "checkCsvContent方法", "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
	        throw new Exception(e);
	    } finally {
	        in.close();
	    }
	    return true;
	}
	
	// 檢查序號期限資料，應比序號發送時間大至少二個月以上
	private boolean checkSerialPeriodContent(MultipartFile file, String rewardDate) throws Exception {
	    InputStreamReader in = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
	    try {
	        BufferedReader reader = new BufferedReader(in);
	        LocalDate referenceDate = LocalDate.parse(rewardDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        String line;
	        reader.readLine(); // 跳过标题行
	        while ((line = reader.readLine()) != null) {
	            String[] values = line.split(","); // Assuming comma-separated values
	            if (values.length > 4) { // 假設序號期限在第五列
	                String serialPeriod = values[4];
	                LocalDate serialPeriodDate = LocalDate.parse(serialPeriod, DateTimeFormatter.ofPattern(generatePattern(serialPeriod))); // 假设序號期限格式為 "yyyy/MM/dd"
	                if (ChronoUnit.MONTHS.between(referenceDate, serialPeriodDate) < 2) {
	                    return false; // 序號期限小於序號發送時間的兩個月
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.info(commonFormat, "-", "checkSerialPeriodContent方法", "-", "-", "失敗", "-", "-", e.toString(), e);
	        log.error(errorFormat, "-", "checkSerialPeriodContent方法", "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
	        throw new Exception(e);
	    } finally {
	        in.close();
	    }
	    return true;
	}

	/**
	 * 序號簡訊白名單新增第二頁的送簽前的檢查
	 * 
	 * @return
	 */
	@PostMapping(SETTING + "/finishSetting")
	public ResponseEntity<?> finishSetting(@RequestParam("signUser1") String signUser1,
			@RequestParam("signUser2") String signUser2
	// other parameters if needed
	) {
		String api_id = SETTING + "/finishSetting" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		Map<String, String> response = new HashMap<>();
		try {
			log.info("第一關簽核人員:" + signUser1);
			log.info("第二關簽核人員:" + signUser2);
			// Check if sign users are selected
			if (StringUtils.isBlank(signUser1) || StringUtils.isBlank(signUser2)) {
				response.put("signUserError", "您未選擇簽核人員，請調整後再進行送簽喔！");
				return ResponseEntity.badRequest().body(response);
			}

			// Check if the same user is selected for both approvals
			if (signUser1.equals(signUser2)) {
				response.put("signUserError", "兩關簽核人員不可為同一人，請調整後再進行完成並送簽喔！");
				return ResponseEntity.badRequest().body(response);
			}

			// Process the form submission
			// ...

			return ResponseEntity.ok().body("Form submitted successfully");
		} catch (Exception e) {

			response.put("exceptionError", "系統發生錯誤，請聯繫系統管理員Yvette Yang。");
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
			return ResponseEntity.badRequest().body(response);
		}
	}

	/**
	 * 序號簡訊白名單新增第二頁的完成並送簽
	 * 
	 * @return
	 * @throws IOException
	 */
	@PostMapping(SETTING + "/completeSendApproval")
	public ResponseEntity<?> completeSendApproval(@RequestParam Map<String, String> requestData,
			@RequestParam("fileUpload") MultipartFile file, HttpServletRequest request) throws IOException {
		String api_id = SETTING + "/completeSendApproval" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		Map<String, String> response = new HashMap<>();
		try {
			serialCampaignWhileListService.completeSendApproval(requestData, file);

			var eJ = AESUtil.encryptStr(rc_recieve_secrect_key, rc_recieve_secrect_iv);
			String content = this.fullUrl(String.format("%s%s?parm=%s", RedirectController.REDIRECT_URI,
					RedirectController.APPRV_URI, URLEncoder.encode(eJ, "UTF-8")), request);
			URL url = new URL(request.getRequestURL().toString());
			Map<String, Object> map = new HashMap<>();
			String name = "序號發幣白名單簽核提醒通知";
			String message1 = "您有序號發幣白名單尚未簽核，";
			String message2 = "請盡速進入系統完成 序號發幣白名單簽核。";
			String message3 = " 序號發幣白名單查詢清單";
			map.put("name", name);
			map.put("message1", message1);
			map.put("message2", message2);
			map.put("message3", message3);
			map.put("CustomerRewardSystem",
					url.getProtocol() + "://" + url.getHost() + "/" + SERIAL_CAMPAIGN_WHILE_LIST + FIND_WHILE_LIST);
			var mailVo = MailVo
					.builder().subject(name).from(crs_mail_from).toEmail(serialCampaignWhileListService
							.getAccountEntity(Long.valueOf(requestData.get("signUser1"))).getEmail()).copyEmail(ccEmail)
					.content(content).build();
			mailVo.setParams(map);
			mailUtils.sendMimeMail(mailVo);
		} catch (Exception e) {

			response.put("exceptionError", "系統發生錯誤，請聯繫系統管理員Yvette Yang。");
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok().body(response);

	}

	/**
	 * 序號簡訊白名單查詢頁
	 * 
	 * @return
	 */
	@GetMapping(FIND_WHILE_LIST)
	public ModelAndView findWhileList() {
		String api_id = FIND_WHILE_LIST + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		try {
			ModelAndView mv = null;
			mv = new ModelAndView("serialCampaignWhileList/findWhileList");
			mv.addObject("menudata", "2");
			mv.addObject("menuop", SERIAL_CAMPAIGN_WHILE_LIST + FIND_WHILE_LIST);

			List<Object[]> findAccountsByAccountId = serialCampaignWhileListService.getAccountRepo()
					.findAccountsByAccountId(serialCampaignWhileListService.getAccountId());
			for (Object[] objects : findAccountsByAccountId) {
				objects[0] = ESAPI.encoder().encodeForHTML((String) objects[0]);
				objects[1] = ESAPI.encoder().encodeForHTML(((BigDecimal) objects[1]).toString());
			}
			mv.addObject("applyIds", findAccountsByAccountId);

			return mv;
		} catch (Exception e) {
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
			throw new ModelAndViewException();
		}

	}

	/**
	 * 序號簡訊白名單查詢頁的table
	 * 
	 * @return
	 */
	@PostMapping(FIND_WHILE_LIST)
	public ResponseEntity<?> findWhileList(@RequestParam Map<String, String> requestData) {
		String api_id = FIND_WHILE_LIST + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");

		try {
			Map<String, List<String>> validErrors = CommonUtils
					.validate(Arrays.asList(new SerialCampaignWhileListConditionVoValidator(requestData)));

			if (!validErrors.isEmpty()) {

				return getValidErrorResponse(validErrors);
			}
			return ResponseEntity.ok().body(serialCampaignWhileListService.findWhileList(requestData));
		} catch (Exception e) {
			sendErrorEmail(e);
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			throw new ModelAndViewException();
		}
	}

	/**
	 * 序號簡訊白名單查詢頁的名單下載
	 * 
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/downloadCampaignFile")
	public ResponseEntity<byte[]> downloadCampaignFile(@RequestParam("campaignDetailId") BigDecimal campaignDetailId)
			throws IOException {
		String api_id = "/downloadCampaignFile" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		try {
			byte[] data = serialCampaignWhileListService.downloadCampaignFile(campaignDetailId);

			HttpHeaders headers = new HttpHeaders();			
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			// Get the original filename from the repository
			String originalFileName = serialCampaignWhileListService
			    .getSerialCampaignFileRepo().findById(campaignDetailId).get().getFileName();

			// Encode the filename using UTF-8
			String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString())
			    .replaceAll("\\+", "%20"); // Replace '+' with space for better compatibility

			// Set Content-Disposition header with both filename and filename* for compatibility
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
			
			return ResponseEntity.ok().headers(headers).contentLength(data.length).body(data);
		} catch (Exception e) {
			sendErrorEmail(e);
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			throw new ModelAndViewException();
		}
	}

	/**
	 * 序號簡訊白名單查詢頁的撤回
	 * 
	 * @return
	 */
	@PostMapping("/rollback")
	public ResponseEntity<?> rollback(@RequestParam("campaignDetailId") BigDecimal campaignDetailId,
			@RequestParam("reportId") BigDecimal reportId,
			@RequestParam("approvalBatchId") BigDecimal approvalBatchId) {
		String api_id = "/rollback" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		Map<String, String> response = new HashMap<>();
		try {
			serialCampaignWhileListService.rollback(campaignDetailId, reportId, approvalBatchId);
			return ResponseEntity.ok().body("200");
		} catch (Exception e) {
			response.put("exceptionError", "系統發生錯誤，請聯繫系統管理員Yvette Yang。");
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
			return ResponseEntity.badRequest().body(response);
		}

	}

	/**
	 * 序號簡訊白名單查詢頁的查看
	 * 
	 * @return
	 */
	@PostMapping("/view")
	public ResponseEntity<?> view(@RequestParam("campaignDetailId") BigDecimal campaignDetailId,
			@RequestParam("reportId") BigDecimal reportId) {
		String api_id = "/view" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		Map<String, String> response = new HashMap<>();
		try {
			return ResponseEntity.ok().body(serialCampaignWhileListService.view(campaignDetailId, reportId));
		} catch (Exception e) {
			response.put("exceptionError", "系統發生錯誤，請聯繫系統管理員Yvette Yang。");
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			sendErrorEmail(e);
			return ResponseEntity.badRequest().body(response);
		}

	}

	/**
	 * 序號簡訊白名單查詢頁的簽核
	 * 
	 * @return
	 */
	@PostMapping("/sendApproval")
	public ResponseEntity<?> sendApproval(@RequestParam("campaignDetailId") BigDecimal campaignDetailId,
			@RequestParam("reportId") BigDecimal reportId, @RequestParam("LEVEL_NO") String LEVEL_NO,
			@RequestParam("BATCH_ID") BigDecimal BATCH_ID, @RequestParam("opinion") String opinion,
			@RequestParam("commentInfo") String commentInfo, @RequestParam("APPLYID") Long APPLYID,
			HttpServletRequest request) {
		String api_id = "/sendApproval" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		try {
			serialCampaignWhileListService.sendApproval(campaignDetailId, reportId, LEVEL_NO, BATCH_ID, opinion,
					commentInfo);

			URL url = new URL(request.getRequestURL().toString());
			var eJ = AESUtil.encryptStr(rc_recieve_secrect_key, rc_recieve_secrect_iv);
			String content = this.fullUrl(String.format("%s%s?parm=%s", RedirectController.REDIRECT_URI,
					RedirectController.APPRV_URI, URLEncoder.encode(eJ, "UTF-8")), request);

			if ("1".equals(LEVEL_NO)) {
				if ("1".equals(opinion)) {
					Map<String, Object> map = new HashMap<>();
					String name = "序號發幣白名單簽核提醒通知";
					String message1 = "您有序號發幣白名單尚未簽核，";
					String message2 = "請盡速進入系統完成 序號發幣白名單簽核。";
					String message3 = " 序號發幣白名單查詢清單";
					map.put("name", name);
					map.put("message1", message1);
					map.put("message2", message2);
					map.put("message3", message3);
					map.put("CustomerRewardSystem", url.getProtocol() + "://" + url.getHost() + "/"
							+ SERIAL_CAMPAIGN_WHILE_LIST + FIND_WHILE_LIST);
					var mailVo = MailVo.builder().subject(name).from(crs_mail_from)
							.toEmail(serialCampaignWhileListService.getAccountEntity(serialCampaignWhileListService
									.getSerialApprovalBatchRepo().findById(BATCH_ID).get().getL2Account()).getEmail()).copyEmail(ccEmail)
							.content(content).build();
					mailVo.setParams(map);
					mailUtils.sendMimeMail(mailVo);
				}
			}

			String subject = "";
			subject = "《 序號發幣白名單 》簽核完成通知 – " + ("1".equals(opinion) ? "同意" : "否決");

			var mailVo = MailVo.builder().subject(subject).from(crs_mail_from)
					.toEmail(serialCampaignWhileListService.getAccountEntity(APPLYID).getEmail()).copyEmail(ccEmail).content(content)
					.build();

			Map<String, Object> map = new HashMap<>();
			map.put("opinion", ("1".equals(opinion) ? "同意" : "否決"));
			map.put("commentInfo", StringUtils.isNotBlank(commentInfo) ? "（" + commentInfo + "）" : "");
			map.put("sysIdList", "■ " + reportId + "<br>");

			map.put("CustomerRewardSystem",
					url.getProtocol() + "://" + url.getHost() + "/" + SERIAL_CAMPAIGN_WHILE_LIST + FIND_WHILE_LIST);
			mailVo.setParams(map);
			mailUtils.sendMail(mailVo, "SerialCampaignWhileList.html");
		} catch (Exception e) {
			sendErrorEmail(e);
			Map<String, String> response = new HashMap<>();
			response.put("exceptionError", "系統發生錯誤，請聯繫系統管理員Yvette Yang。");
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			return ResponseEntity.internalServerError().body(response);
		}
		return ResponseEntity.ok().body("200 ok");

	}

	/**
	 * 序號簡訊白名單查詢頁的催簽
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/urge")
	public ResponseEntity<?> urge(@RequestParam("signName") String signName, HttpServletRequest request)
			throws Exception {
		String api_id = "/urge" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		try {
			List<String> emails = serialCampaignWhileListService.getAccountRepo().findByUserId(signName).stream()
					.map(AccountEntity::getEmail).collect(Collectors.toList());
			URL url = new URL(request.getRequestURL().toString());
			var eJ = AESUtil.encryptStr(rc_recieve_secrect_key, rc_recieve_secrect_iv);
			String content = this.fullUrl(String.format("%s%s?parm=%s", RedirectController.REDIRECT_URI,
					RedirectController.APPRV_URI, URLEncoder.encode(eJ, "UTF-8")), request);
			Map<String, Object> map = new HashMap<>();
			String name = "序號發幣白名單簽核提醒通知";
			String message1 = "您有序號發幣白名單尚未簽核，";
			String message2 = "請盡速進入系統完成 序號發幣白名單簽核。";
			String message3 = " 序號發幣白名單查詢清單";
			map.put("name", name);
			map.put("message1", message1);
			map.put("message2", message2);
			map.put("message3", message3);
			map.put("CustomerRewardSystem",
					url.getProtocol() + "://" + url.getHost() + "/" + SERIAL_CAMPAIGN_WHILE_LIST + FIND_WHILE_LIST);			
			var mailVo = MailVo.builder().subject(name).from(crs_mail_from).toEmails(emails).copyEmail(ccEmail).content(content).build();
			mailVo.setParams(map);
			mailUtils.sendMimeMail(mailVo);
			return ResponseEntity.ok().body("200 ok");
		} catch (Exception e) {
			sendErrorEmail(e);
			Map<String, String> response = new HashMap<>();
			response.put("exceptionError", "系統發生錯誤，請聯繫系統管理員Yvette Yang。");
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			return ResponseEntity.internalServerError().body(response);
		}
	}

	/**
	 * 序號發幣白名單設定的下載上傳範例檔
	 * 
	 * @return
	 * @throws IOException
	 */	
	@RequestMapping(SETTING + "/downloadExampleCsv")
	public ResponseEntity<Resource> downloadExampleCsv(){
	    String api_id = SETTING + "/downloadExampleCsv" + dateFormat().format(new Date());
	    log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
	    try {
	    	//解Checkmarx的高風險Stored Absolute Path Traversal
	    	String filename = "序號簡訊白名單新增檔案上傳範例檔.csv";
	        Path baseDir = Paths.get("/home/tpsacct/");	     
	        Path filePath = baseDir.resolve(filename).normalize();
	        if (!filePath.startsWith(baseDir)) {
	            throw new SecurityException("Invalid file path");
	        }

	        if (!Files.isRegularFile(filePath, LinkOption.NOFOLLOW_LINKS)) {
	            throw new IOException("File does not exist.");
	        }
	        	     
	        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
	        HttpHeaders header = new HttpHeaders();
	        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
	        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
	        header.add("Pragma", "no-cache");
	        header.add("Expires", "0");

	        ByteArrayResource resource;
	        try (InputStream is = Files.newInputStream(filePath, LinkOption.NOFOLLOW_LINKS)) {
	            resource = new ByteArrayResource(is.readAllBytes());
	        }

	        return ResponseEntity.ok()
	                .headers(header)
	                .contentLength(Files.size(filePath))
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(resource);
	    } catch (Exception e) {
	        sendErrorEmail(e);
	        log.info(commonFormat, "-", api_id, "-", "-", "Failed", "-", "-", e.toString());
	        log.error(errorFormat, "-", api_id, "-", "Failed", e.toString(), e);
	        throw new ModelAndViewException();
	    }
	}



	private String generatePattern(String date) throws Exception {
		try {
			String[] parts = date.split("/");
			String yearPattern = "";
			String monthPattern = "";
			String dayPattern = "";

			if (parts[0].length() == 4) {
				yearPattern = "yyyy";
			}

			if (parts[1].length() == 2) {
				monthPattern = "MM";
			} else if (parts[1].length() == 1) {
				monthPattern = "M";
			}

			if (parts[2].length() == 2) {
				dayPattern = "dd";
			} else if (parts[2].length() == 1) {
				dayPattern = "d";
			}

			return yearPattern + "/" + monthPattern + "/" + dayPattern;
		} catch (Exception e) {
			sendErrorEmail(e);
			throw new Exception("日期格式要西元年月日");
		}
	}

	@PostMapping("/setApprovalIng")
	public ResponseEntity<?> setApprovalIng(@RequestParam("reportId") BigDecimal reportId) {
		String api_id = "/setApprovalIng" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "-", "-", "-", "-", "-", "-", "-");
		try {
			serialCampaignWhileListService.setApprovalIng(reportId);
			return ResponseEntity.ok().body("200 ok");
		} catch (Exception e) {
			sendErrorEmail(e);
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString());
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			throw new ModelAndViewException();
		}

	}
	
	private void sendErrorEmail(Exception ex) {
		   
	    StringWriter sw = new StringWriter();
	    ex.printStackTrace(new PrintWriter(sw));
	    String exceptionAsString = sw.toString();
		var mailVo = MailVo.builder().subject("序號發幣白名單Exception通知").from(crs_mail_from)
				.toEmail(ccEmail).content("錯誤訊息：" + ex.getMessage() + "\n\n堆疊追蹤：\n" + exceptionAsString).build();
		mailUtils.sendSimpleMail(mailVo);
	}
}
