package com.twm.mgmt.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.twm.mgmt.Exception.AjaxException;
import com.twm.mgmt.Exception.ModelAndViewException;
import com.twm.mgmt.model.serialCampaign.MoMoEventDetailResponse;
import com.twm.mgmt.model.serialCampaign.MoMoSeqEventResponse;
import com.twm.mgmt.model.serialCampaign.SerialCampaignRequest;
import com.twm.mgmt.model.serialCampaign.SerialCampaignSettingVo;
import com.twm.mgmt.model.serialCampaign.WebMomoEventDetailRequest;
import com.twm.mgmt.model.serialCampaign.WebMomoSeqEventReqObject;
import com.twm.mgmt.model.serialCampaign.WebMomoSeqEventRequest;
import com.twm.mgmt.persistence.entity.SerialMomoCreateBatchEntity;
import com.twm.mgmt.persistence.entity.SerialMomoCreateDetailEntity;
import com.twm.mgmt.persistence.repository.SerialRewardReportRepository;
import com.twm.mgmt.service.SerialCampaignService;

@RequestMapping("/serialCampaign")
@Controller
public class SerialCampaignController extends BaseController {
	private static final String FIND_REWARD = "/findReward";
	private static final String AMOUNT_VALIDITY_DATE_UPDATE = "/amountValidityDateUpdate";
	private static final String CAMPAIGN_SETTING_URI = "/setting";
	private static final String VALIDATE_EVENT_CODE = "/validateEventCode";
	private static final String CREATE = "/create";

	@Autowired
	private SerialCampaignService serialCampaignService;

	@Value("${MOGW.DomainName}")
	private String MOGWDomainName;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 進入序號發幣活動資訊查詢頁
	 * 
	 * @return
	 */
	@GetMapping(FIND_REWARD)
	public ModelAndView findReward() {
		String api_id = FIND_REWARD + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "Starting GET findReward method", "-", "-", "-", "-", "-", "-");
		try {
			ModelAndView mv = new ModelAndView("serialCampaign/findReward");
			mv.addObject("menudata", "2");
			mv.addObject("menuop", "serialCampaign/findReward");

			return mv;
		} catch (Exception e) {
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			throw new ModelAndViewException();
		} finally {
			log.info("Ending GET findReward method");
		}
	}

	@GetMapping(AMOUNT_VALIDITY_DATE_UPDATE)
	public ModelAndView amountValidityDateUpdate() {
		String api_id = AMOUNT_VALIDITY_DATE_UPDATE + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "Starting GET amountValidityDateUpdate method", "-", "-", "-", "-", "-", "-");
		ModelAndView mv = new ModelAndView("serialCampaign/amountValidityDateUpdate");

		try {
			mv.addObject("menudata", "2");
			mv.addObject("menuop", "serialCampaign/amountValidityDateUpdate");

		} catch (Exception e) {
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			throw new ModelAndViewException();
		} finally {
			log.info("Ending GET amountValidityDateUpdate method");
		}

		return mv;
	}

	/**
	 * 序號發幣活動資訊查詢頁的查詢
	 * 
	 * @param serialCampaignSettingVo
	 * @return
	 */
	@PostMapping(FIND_REWARD)
	public ResponseEntity<?> findReward(SerialCampaignSettingVo serialCampaignSettingVo) {
		String api_id = FIND_REWARD + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "Starting POST findReward method with SerialCampaignSettingVo: {}",
				serialCampaignSettingVo, "-", "-", "-", "-", "-");
		try {

			return new ResponseEntity<>(serialCampaignService.findReward(serialCampaignSettingVo), HttpStatus.OK);

		} catch (Exception e) {
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			throw new AjaxException();
		} finally {
			log.info("Ending POST findReward method");
		}
	}

	@RequestMapping(CAMPAIGN_SETTING_URI + FIND_REWARD)
	public ModelAndView settingFindReward(BigDecimal reportId, BigDecimal campaignDetailId, String status,
			String eventno, String eventdtid, String button) {
		String api_id = CAMPAIGN_SETTING_URI + "/findReward" + dateFormat().format(new Date());
		log.info(commonFormat, api_id, "reportId: {}", reportId, "campaignDetailId: {}", campaignDetailId, "status: {}",
				status, "-");
		try {
			ModelAndView mv = new ModelAndView("serialCampaign/settingFindReward");

			mv.addObject("menudata", "2");
			mv.addObject("menuop", "serialCampaign/findReward");

			if ("新增發幣活動資訊".equals(button)) {
				mv.addObject("reportId", reportId.toString());

				List<Object[]> details = serialCampaignService.findRewardSummaryByAccountId();

				String fmt = "%s | %s | %s | %s | %s | %s";

				List<String> options = details.stream().map(arr -> {
					// 其他欄位如果都只有英文、數字，就可以直接 String.format
					return String.format("%s | %s | %s | %s | %s | %s", arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
				}).collect(Collectors.toList());
				List<String> campaignNames = details.stream().map(arr -> arr[2].toString())
						.collect(Collectors.toList());
				List<String> optionValues = details.stream().map(arr -> arr[0].toString()).collect(Collectors.toList());

				mv.addObject("options", options);
				mv.addObject("titleCampaignNames", campaignNames);
				mv.addObject("optionValues", optionValues);

		        // 3. 改成支援多筆 specificOptions
		        List<Object[]> specificOptions = details.stream()
		            .filter(arr -> arr[0].equals(reportId))
		            .collect(Collectors.toList());
				if (!specificOptions.isEmpty()) {
		            // 3.1 把每筆轉字串或直接塞 Object[] 到 model
		            mv.addObject("specificOptions", specificOptions);
				}

			} else if ("查看".equals(button)) {
				// 1. 讀 batch 資訊 (PAY_ACCOUNT、ORDER_NUMBER…)
				SerialMomoCreateBatchEntity batch = serialCampaignService.getBatchRepo().findByEventNoAndEventDtId(
						!eventno.equals("null") ? eventno : null,
						!eventdtid.equals("null") ? Long.valueOf(eventdtid) : null);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				mv.addObject("amountValidityStartDate",
						batch.getAmountValidityStartDate().toLocalDate().format(formatter));
				mv.addObject("amountValidityEndDate", batch.getAmountValidityEndDate().toLocalDate().format(formatter));
				mv.addObject("batch", batch);
				// 2. 讀 detail 資訊
				List<SerialMomoCreateDetailEntity> detailList = serialCampaignService.getDetailRepo()
						.findByEventNoAndEventDtId(!eventno.equals("null") ? eventno : null,
								!eventdtid.equals("null") ? Long.valueOf(eventdtid) : null);
				mv.addObject("detailList", detailList);
				// 3. 標記「查看」模式
				mv.addObject("viewMode", true);				
				mv.addObject("rewardMonth",serialCampaignService.getSerialRewardReportRepository().findRewardDateByReportId(reportId));
			}

			return mv;
		} catch (Exception e) {
			log.info(commonFormat, "-", api_id, "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", api_id, "-", "失敗", e.toString(), e);
			throw new ModelAndViewException();
		} finally {
			log.info("Ending POST settingFindReward method");
		}

	}

	/**
	 * 1. 前端傳入 momoEventNo、deptNo 2. 組成 WebMomoEventDetailRequest，呼叫上面那支
	 * fetchMomoEventDetail 3. 擷取回來的四個欄位 + auditStatus 回給前端
	 */
	@PostMapping(CAMPAIGN_SETTING_URI + VALIDATE_EVENT_CODE)
	@ResponseBody
	public ResponseEntity<?> validateEventCode(@RequestParam String momoEventNo, @RequestParam String deptNo) {
		try {
			// 組裝 request
			WebMomoEventDetailRequest req = new WebMomoEventDetailRequest();
			req.setEventNo(momoEventNo);
			req.setDeptNo(deptNo.trim().substring(0, 4));

			// 呼叫並可能拋出 BindException
			MoMoEventDetailResponse detailResp = fetchMomoEventDetail(req, new BeanPropertyBindingResult(req, "req"))
					.getBody();

			// 正常回應邏輯…
			Map<String, Object> resp = new HashMap<>();
			resp.put("auditStatus", Integer.parseInt(detailResp.getAuditStatus()));
			resp.put("issueAmount", detailResp.getEventAmt());
			resp.put("remainingAmount", detailResp.getRemainingAmt());
			String from = detailResp.getStartDate();
			String to = detailResp.getEndDate();
			resp.put("validFrom", from);
			resp.put("validTo", to);

			return ResponseEntity.ok(resp);

		} catch (BindException bex) {
			// 1. 取出所有欄位錯誤訊息
			String errorMsg = bex.getBindingResult().getFieldErrors().stream()
					.map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).collect(Collectors.joining("; "));
			// 2. 回 400 並把錯誤訊息給前端
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", errorMsg));

		} catch (Exception ex) {
			log.error("validateEventCode 呼叫失敗", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "系統錯誤，請稍後再試"));
		}
	}

	/**
	 * 接收前端建立序號請求的 JSON 資料
	 */
	@PostMapping(CAMPAIGN_SETTING_URI + CREATE)
	public ResponseEntity<?> createSerialCampaign(@RequestBody SerialCampaignRequest request) {
		// 1. 準備一個 list 收集所有錯誤訊息
		List<String> errs = new ArrayList<>();

		// 2. 逐項做驗證
		// 2.1 eventNo: 16 碼
		String eventNo = request.getEventNo();
		if (eventNo == null || eventNo.isBlank()) {
			errs.add("為必填");
		} else if (eventNo.length() != 16) {
			errs.add("長度須為 16 碼");
		}
		
		LocalDate rewardDate = request.getRewardDate();
		if (rewardDate == null) {
			errs.add("為必填");
		} 
		// 2.2 payAccount -> deptNo: 4 碼大寫英文字母
		String deptNo = request.getPayAccount().trim().substring(0, 4);
		if (deptNo == null || deptNo.isBlank()) {
			errs.add("為必填");
		} else if (!deptNo.matches("^[A-Z]{4}$")) {
			errs.add("必須是 4 碼大寫英文字母");
		}

		// 2.3 conditions 不可為空
		List<SerialCampaignRequest.Condition> conditions = request.getConditions();
		if (conditions == null || conditions.isEmpty()) {
			errs.add("列表不可為空");
		}

		// 2.4 計算 requestCount、requestAmount 總和
		long sumCount = 0;
		long sumAmt = 0;
		if (conditions != null) {
			for (SerialCampaignRequest.Condition c : conditions) {
				Integer amt = c.getSeqDenoAmt() != null ? c.getSeqDenoAmt().intValue() : null;
				Integer count = c.getSeqDenoCount() != null ? c.getSeqDenoCount() : null;

				if (amt == null || amt < 1)
					errs.add("最少要 1 元");
				if (count == null || count < 1)
					errs.add("最少要 1 筆");

				if (amt != null && count != null) {
					sumCount += count;
					sumAmt += amt * count;
				}
			}
		}

		// 2.5 與請求 payload 比對
		if (request.getRequestCount() == null) {
			errs.add("為必填");
		} else if (request.getRequestCount() != sumCount) {
			errs.add("必須等於各條件筆數的總和: " + sumCount);
		}

		if (request.getRequestAmount() == null) {
			errs.add("為必填");
		} else if (request.getRequestAmount().longValue() != sumAmt) {
			errs.add("必須等於各條件金額的總和: " + sumAmt);
		}

		// 3. 如果有任何錯誤，就提前回 400
		if (!errs.isEmpty()) {
			// 你也可以改成 Map<Field,List<String>> 的結構化回傳
			return ResponseEntity.badRequest().body(Map.of("errors", errs));
		}

		// 4. 全部通過，才呼叫 seqEvent API
		try {
			// （組 payload、呼叫下游 API 的程式如你現有 code）
			WebMomoSeqEventRequest seqReq = new WebMomoSeqEventRequest();
			seqReq.setEventNo(eventNo);
			seqReq.setRequestCount(request.getRequestCount());
			seqReq.setRequestAmout(request.getRequestAmount().intValue());
			seqReq.setDeptNo(deptNo);
			// …設定 seqReq.conditions …

			ResponseEntity<MoMoSeqEventResponse> apiResp = restTemplate.exchange(
					MOGWDomainName + "/fromcrsweb/momo/seqEvent", HttpMethod.POST,
					new HttpEntity<>(seqReq, new HttpHeaders() {
						{
							setContentType(MediaType.APPLICATION_JSON);
						}
					}), MoMoSeqEventResponse.class);

			MoMoSeqEventResponse seqResp = apiResp.getBody();
			log.info("呼叫 seqEvent 回來：{}", seqResp);

			// 再存自己的服務
			serialCampaignService.createSerialCampaign(request, seqResp);

			return ResponseEntity.ok(seqResp);

		} catch (Exception ex) {
			log.error("createSerialCampaign 呼叫 seqEvent 失敗", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "系統忙碌中，請稍後再試"));
		}
	}

	public ResponseEntity<MoMoEventDetailResponse> fetchMomoEventDetail(
			@Valid @RequestBody WebMomoEventDetailRequest req, BindingResult errors) throws Exception {
		if (errors.hasErrors()) {
			String msg = errors.getFieldErrors().stream().map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
					.collect(Collectors.joining("; "));
			throw new BindException(errors);
		}

		String url = MOGWDomainName + "/fromcrsweb/momo/eventDetail";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<WebMomoEventDetailRequest> entity = new HttpEntity<>(req, headers);

		ResponseEntity<MoMoEventDetailResponse> resp = restTemplate.exchange(url, HttpMethod.POST, entity,
				MoMoEventDetailResponse.class);
		log.info("從 WebApiController 拿到的回應：{}", resp.getBody());
		return resp;
	}

	@PostMapping("/getReportData")
	public ResponseEntity<?> getReportData(
			@RequestParam(value = "reportId", required = false) BigDecimal reportId,
			@RequestParam(value = "rewardDate", required = false) @DateTimeFormat(pattern = "yyyy-MM") Date rewardDate)
			throws Exception {

		try {
			return new ResponseEntity<>(serialCampaignService.getReportData(reportId, rewardDate), HttpStatus.OK);
		} catch (Exception e) {
			log.info(commonFormat, "-", "-", "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", "-", "-", "失敗", e.toString(), e);
			throw new Exception();
		}
	}

	@PostMapping("/updateAmountValidityDates")
	public ResponseEntity<String> updateAmountValidityDates(@RequestParam("reportIds") List<BigDecimal> reportIds,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		try {
			serialCampaignService.updateAmountValidityDates(reportIds, startDate, endDate);
			return ResponseEntity.ok("更新成功");
		} catch (Exception e) {
			log.info(commonFormat, "-", "-", "-", "-", "失敗", "-", "-", e.toString(), e);
			log.error(errorFormat, "-", "-", "-", "失敗", e.toString(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失败：" + e.getMessage());
		}
	}

}
