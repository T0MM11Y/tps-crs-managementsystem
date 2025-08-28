package com.twm.mgmt.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twm.mgmt.Manager.MyX509TrustManager;
import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.controller.BaseController;
import com.twm.mgmt.controller.MomoidChangeController;
import com.twm.mgmt.controller.RedirectController;
import com.twm.mgmt.model.common.MailVo;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.ReceiveInfoVo;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.model.momoidChange.MomoidChangeVo;
import com.twm.mgmt.persistence.dto.MomoidChangeMainDto;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeApprovalEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeListEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeMainEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeMainEntity.MomoidChangeMainEntityBuilder;
import com.twm.mgmt.persistence.entity.MomoidChangePictureEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeSmsEntity;
import com.twm.mgmt.persistence.entity.WarningContractEntity;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.persistence.repository.ContractInfoRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeApprovalRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeListRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeMainRepository;
import com.twm.mgmt.persistence.repository.MomoidChangePictureRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeSmsRepository;
import com.twm.mgmt.persistence.repository.WarningContractRepository;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.GetDateSerialNumUtils;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.MailUtils;

@Service
public class MomoidChangeService extends BaseService {

	@Value("${EPAPI.sms}")
	private String sms;

	@Autowired
	private MomoidChangeMainRepository momoidChangeRepo;

	@Autowired
	private MomoidChangeListRepository momoidChangeListRepo;

	@Autowired
	private MomoidChangeApprovalRepository momoidChangeApproval;

	@Autowired
	private MomoidChangeSmsRepository momoidChangeSmsRepo;
	
	@Autowired
	private MomoidChangePictureRepository momoidChangePictureRepo;

	@Autowired
	private ContractInfoRepository contractInfoRepo;

	@Autowired
	private WarningContractRepository warningContractRepo;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	protected MailUtils mailUtils;

	/**
	 * 將Session物件的momoidChangeList及簽核人員寫入db，momoidChangeList是簡訊發送名單設定的用戶卡片
	 * 發送email給簽核人員
	 * 
	 * @param vo
	 * @param request
	 * @throws Exception
	 */
	@Transactional(rollbackFor = { Exception.class })
	public void saveApproval(MomoidChangeVo vo, HttpServletRequest request) throws Exception {

		BigDecimal numBigDecimal = GetDateSerialNumUtils.getNum();
		Date date = new Date();

		var momoidChangeMainEntity = MomoidChangeMainEntity.builder().momoidChangeMainId(numBigDecimal).createDate(date)
				.momoidChangeMainIdType(0).accountId(vo.getUserInfoVo().getAccountId()).status(0).build();

		momoidChangeRepo.save(momoidChangeMainEntity);

		for (Map<String, Object> map : vo.getListMap()) {

			MomoidChangeSmsEntity momoidChangeSmsEntity = MomoidChangeSmsEntity.builder()
					.momoidChangeMainId(numBigDecimal).createDate(date).build();
			momoidChangeSmsRepo.save(momoidChangeSmsEntity);

			String subId = (String) map.get("subid");
			String projectCode = (String) map.get("projectCode");
			String projectSeqNbr = (String) map.get("projectSeqNbr");
			String phoneNumber = (String) map.get("phoneNumber");
			String sendReason = (String) map.get("sendReason");
			String momoMemberId = (String) map.get("momoMemberId");

			MomoidChangeListEntity momoidChangeListEntity = MomoidChangeListEntity.builder()
					.momoidChangeMainId(numBigDecimal).projectCode(projectCode).subid(subId).phoneNumber(phoneNumber)
					.sendReason(sendReason).createDate(date).projectSeqNbr(projectSeqNbr)
					.momoidChangeSmsId(momoidChangeSmsEntity.getMomoidChangeSmsId()).momoMemberId(momoMemberId)
					.momoidChangeListId(momoidChangeListRepo.getMomoidChangeListId()).build();
			momoidChangeListRepo.save(momoidChangeListEntity);
		}

		String[] momoidChangeFileNames = vo.getMomoidChangeFileNames();
		byte[][] momoidChangeFileBytes = vo.getMomoidChangeFileBytes();
		if (momoidChangeFileNames!=null && momoidChangeFileBytes!=null) {
			MomoidChangePictureEntity momoidChangePictureEntity = MomoidChangePictureEntity.builder().momoidChangeMainId(numBigDecimal).picture1Name(momoidChangeFileNames[0])
					.picture1File(momoidChangeFileBytes[0]).picture2Name(momoidChangeFileNames[1])
					.picture2File(momoidChangeFileBytes[1]).picture3Name(momoidChangeFileNames[2])
					.picture3File(momoidChangeFileBytes[2]).picture4Name(momoidChangeFileNames[3])
					.picture4File(momoidChangeFileBytes[3]).picture5Name(momoidChangeFileNames[4])
					.picture5File(momoidChangeFileBytes[4]).picture6Name(momoidChangeFileNames[5])
					.picture6File(momoidChangeFileBytes[5]).picture7Name(momoidChangeFileNames[6])
					.picture7File(momoidChangeFileBytes[6]).picture8Name(momoidChangeFileNames[7])
					.picture8File(momoidChangeFileBytes[7]).picture9Name(momoidChangeFileNames[8])
					.picture9File(momoidChangeFileBytes[8]).picture10Name(momoidChangeFileNames[9])
					.picture10File(momoidChangeFileBytes[9]).picture11Name(momoidChangeFileNames[10])
					.picture11File(momoidChangeFileBytes[10]).picture12Name(momoidChangeFileNames[11])
					.picture12File(momoidChangeFileBytes[11]).picture13Name(momoidChangeFileNames[12])
					.picture13File(momoidChangeFileBytes[12]).picture14Name(momoidChangeFileNames[13])
					.picture14File(momoidChangeFileBytes[13]).picture15Name(momoidChangeFileNames[14])
					.picture15File(momoidChangeFileBytes[14]).createDate(date).build();
			momoidChangePictureRepo.save(momoidChangePictureEntity);
		}

		
		MomoidChangeApprovalEntity momoidChangeApprovalEntity = MomoidChangeApprovalEntity.builder()
				.momoidChangeMainId(numBigDecimal).accountId(vo.getApprovalId()).createDate(date).opinion(0).build();
		momoidChangeApproval.save(momoidChangeApprovalEntity);

		URL url = new URL(request.getRequestURL().toString());

		Map<String, Object> map = new HashMap<>();
		String name1 = "客編異動簽核提醒通知";
		String message1 = "你有用戶 momo 客編異動簡訊尚未簽核，系統單號為：" + numBigDecimal.toString() + "。";
		String message2 = "請盡速進入系統完成用戶 momo 客編異動簡訊簽核。";
		String message3 = "客編異動簡訊簽核紀錄查詢清單";
		map.put("name", name1);
		map.put("message1", message1);
		map.put("message2", message2);
		map.put("message3", message3);

		map.put("CustomerRewardSystem",
				url.getProtocol() + "://" + url.getHost() + "/momoidChange/findMomoidChangeApproval");

		// send mail 有 bug
		try {
			BigDecimal momoidChangeApprovalId = momoidChangeApprovalEntity.getMomoidChangeApprovalId();
			var v = ReceiveInfoVo.builder().id(numBigDecimal.toString()).level(1)
					.type(momoidChangeApprovalId.toString()).build();
			// System.out.println("campaignSetting6Save mail1");
			var jsonStr = JsonUtil.objectToJson(v);
			var eJ = AESUtil.encryptStr(jsonStr, "9bJHnMNKU5WS7OV5II9JXgUIAGhUZ0Ao", "9NkKmHvnTKw7ywC9");
			var content = this.fullUrl(String.format("%s%s?parm=%s", RedirectController.REDIRECT_URI,
					RedirectController.APPRV_URI, URLEncoder.encode(eJ, "UTF-8")), request);

			Long accountId = momoidChangeApprovalEntity.getAccountId();
			AccountEntity accountEntity = accountRepo.findByAccountID1(accountId);
			String toEmail = accountEntity.getEmail();
			var mailVo = MailVo.builder().subject("客編異動簽核提醒通知").from("crs@taiwanmobile.com")// .from(campaignService.fromEmail)
					.toEmail(toEmail).content(content).build();
			mailVo.setParams(map);

			mailUtils.sendMimeMail(mailVo);
			// System.out.println("campaignSetting6Save mail3");
		} catch (Exception e) {
			log.error("CampaignController campaignSetting6Save Error: {}", e.getMessage(), e);
			throw new Exception(e);
			// System.out.println("campaignSetting6Save mail4 error");
		}

	}

	/**
	 * 簡訊發送紀錄查詢設定頁的查詢table momoidChangeVo.setAccountId(getAccountId())是用來判斷撤回/查看鈕
	 * 
	 * @param momoidChangeVo
	 * @return
	 */
	@Transactional
	public QueryResultVo findMomoidChange(MomoidChangeVo momoidChangeVo) {

		momoidChangeVo.setAccountId(getAccountId());

		QueryResultVo resultVo = new QueryResultVo(momoidChangeVo);

		resultVo.setResult(momoidChangeRepo.findByMomoidChange(momoidChangeVo));

		return resultVo;
	}

	/**
	 * 依照系統單號，查詢用戶卡片。momoidChangeMainId是系統單號
	 * 
	 * @param momoidChangeMainId
	 * @return
	 */
	@Transactional
	public List<MomoidChangeListEntity> findByMomoidChangeMainId(BigDecimal momoidChangeMainId) {
		// checkmarx弱掃
		return JsonUtil.jsonToList(
				ESAPI.encoder()
						.decodeForHTML(ESAPI.encoder()
								.encodeForHTML(JsonUtil.objectToJson(
										momoidChangeListRepo.findByMomoidChangeMainId(momoidChangeMainId)))),
				MomoidChangeListEntity.class);

	}
	
	
	@Transactional
	public MomoidChangePictureEntity getMomoidChangePictureFindById(BigDecimal momoidChangeMainId) {

		//checkmarx弱掃
		Map findByMomoidChangeMainId = momoidChangePictureRepo.findByMomoidChangeMainId(momoidChangeMainId);
		findByMomoidChangeMainId = new Gson().fromJson(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(new Gson().toJson(findByMomoidChangeMainId))), Map.class);
		if(findByMomoidChangeMainId!=null) {
			MomoidChangePictureEntity momoidChangePictureEntity = new MomoidChangePictureEntity();
			momoidChangePictureEntity.setPicture1Name((String) findByMomoidChangeMainId.get("PICTURE1_NAME"));
			momoidChangePictureEntity.setPicture2Name((String) findByMomoidChangeMainId.get("PICTURE2_NAME"));
			momoidChangePictureEntity.setPicture3Name((String) findByMomoidChangeMainId.get("PICTURE3_NAME"));
			momoidChangePictureEntity.setPicture4Name((String) findByMomoidChangeMainId.get("PICTURE4_NAME"));
			momoidChangePictureEntity.setPicture5Name((String) findByMomoidChangeMainId.get("PICTURE5_NAME"));
			momoidChangePictureEntity.setPicture6Name((String) findByMomoidChangeMainId.get("PICTURE6_NAME"));
			momoidChangePictureEntity.setPicture7Name((String) findByMomoidChangeMainId.get("PICTURE7_NAME"));
			momoidChangePictureEntity.setPicture8Name((String) findByMomoidChangeMainId.get("PICTURE8_NAME"));
			momoidChangePictureEntity.setPicture9Name((String) findByMomoidChangeMainId.get("PICTURE9_NAME"));
			momoidChangePictureEntity.setPicture10Name((String) findByMomoidChangeMainId.get("PICTURE10_NAME"));
			momoidChangePictureEntity.setPicture11Name((String) findByMomoidChangeMainId.get("PICTURE11_NAME"));
			momoidChangePictureEntity.setPicture12Name((String) findByMomoidChangeMainId.get("PICTURE12_NAME"));
			momoidChangePictureEntity.setPicture13Name((String) findByMomoidChangeMainId.get("PICTURE13_NAME"));
			momoidChangePictureEntity.setPicture14Name((String) findByMomoidChangeMainId.get("PICTURE14_NAME"));
			momoidChangePictureEntity.setPicture15Name((String) findByMomoidChangeMainId.get("PICTURE15_NAME"));
			return momoidChangePictureEntity;
		}
		
		 //MomoidChangePictureEntity momoidChangePictureEntityOptional = momoidChangePictureRepo.findByMomoidChangeMainId(momoidChangeMainId);
//		 if (momoidChangePictureEntityOptional!=null) {
//			 return momoidChangePictureEntityOptional;
//		 }
		 return null;
	}
	
	
	@Transactional
	public MomoidChangePictureEntity getMomoidChangePictureFindById2(BigDecimal momoidChangeMainId) {


		
		 Optional<MomoidChangePictureEntity> momoidChangePictureEntityOptional = momoidChangePictureRepo.findById(momoidChangeMainId);
		 if (momoidChangePictureEntityOptional.isPresent()) {
			 return momoidChangePictureEntityOptional.get();
		 }
		 return null;
	}

	/**
	 * 點按簡訊簽核紀錄查詢頁的查詢鈕 table的簽核鈕是寫在momoidChangeMainDto.setBTN_STATUS。因為table的update
	 * row 一直無法更新此cell的狀態。才會先寫在後端。
	 * 
	 * @param momoidChangeVo
	 * @return
	 */
	@Transactional
	public QueryResultVo findMomoidChangeApproval(MomoidChangeVo momoidChangeVo) {

		QueryResultVo resultVo = new QueryResultVo(momoidChangeVo);

		List<MomoidChangeMainDto> findMomoidChangeApproval = momoidChangeRepo.findMomoidChangeApproval(momoidChangeVo);

		for (MomoidChangeMainDto momoidChangeMainDto : findMomoidChangeApproval) {

		}

		for (int i = 0; i < findMomoidChangeApproval.size(); i++) {

			MomoidChangeMainDto momoidChangeMainDto = findMomoidChangeApproval.get(i);
			String btn_STATUS = momoidChangeMainDto.getBTN_STATUS();
			if ("簽核".equals(btn_STATUS)) {
				momoidChangeMainDto.setBTN_STATUS(
						"<button type=\"button\" class=\"btn btn-primary--c ml-2 btn-sm btn--showBtn  font-size-14 font-weight-bold\" style=\"background: #FF6100;\" data-id=\""
								+ momoidChangeMainDto.getMOMOID_CHANGE_MAIN_ID() + "\" data-index=\"" + i + "\">"
								+ btn_STATUS + "</button>");
			}

		}

		resultVo.setResult(findMomoidChangeApproval);

		return resultVo;
	}

	/**
	 * 點按簡訊簽核紀錄查詢的table的簽核鈕，出pop up小視窗，進行簽核同意/駁回
	 * 簽核同意寫入warningContract資料表，發送簡訊中心api，傳PHONE_NUMBER及WARNING_ID 發送email給送簽人
	 * map2是用來更新table update row的狀態欄
	 * 
	 * @param momoidChangeVo
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = { Exception.class })
	public Map setMomoidChangeApprovalMomoidChangeApproval2(MomoidChangeVo momoidChangeVo, HttpServletRequest request)
			throws Exception {

		String momoidChangeMainId = momoidChangeVo.getMomoidChangeMainId();
		String opinion = momoidChangeVo.getOpinion();
		Date date = new Date();

		MomoidChangeMainEntity momoidChangeMainEntity = momoidChangeRepo.getById(new BigDecimal(momoidChangeMainId));
		momoidChangeMainEntity.setStatus("1".equals(opinion) ? 3 : 1);
		momoidChangeRepo.save(momoidChangeMainEntity);

		MomoidChangeApprovalEntity momoidChangeApprovalEntity = momoidChangeApproval
				.getById(new BigDecimal(momoidChangeVo.getMomoidChangeApprovalId()));
		momoidChangeApprovalEntity.setCommentInfo(momoidChangeVo.getCommentInfo());
		momoidChangeApprovalEntity.setApprovalDate(date);
		momoidChangeApprovalEntity.setOpinion(Integer.valueOf(opinion));
		momoidChangeApproval.save(momoidChangeApprovalEntity);

		if ("1".equals(opinion)) {
			List<MomoidChangeListEntity> momoidChangeListEntity = momoidChangeListRepo
					.findByMomoidChangeMainId(new BigDecimal(momoidChangeMainId));

			List<Map<String, Object>> jsonList = new ArrayList<Map<String, Object>>();
			HashMap<String, Object> mapRequese = new HashMap<String, Object>();

			for (MomoidChangeListEntity momoidChangeListEntity2 : momoidChangeListEntity) {

				String subid = momoidChangeListEntity2.getSubid();
				String projectCode = momoidChangeListEntity2.getProjectCode();
				String projectSeqNbr = momoidChangeListEntity2.getProjectSeqNbr();
				String phoneNumber = momoidChangeListEntity2.getPhoneNumber();
				String momoMemberId = momoidChangeListEntity2.getMomoMemberId();
				WarningContractEntity warningContractEntity = WarningContractEntity.builder().subId(subid)
						.projectSeqNbr(projectSeqNbr).projectCode(projectCode)
						.contractId(contractInfoRepo.getContractId(projectSeqNbr, projectCode, subid, momoMemberId))
						.rewardType(0).rewardId(momoMemberId).warnType("COMPLAINT").createDate(date).build();
				warningContractRepo.save(warningContractEntity);

				momoidChangeListEntity2.setWarningId(warningContractEntity.getWarningId());
				momoidChangeListRepo.save(momoidChangeListEntity2);

				BigDecimal warningId = momoidChangeListEntity2.getWarningId();

				mapRequese.put("PHONE_NUMBER", phoneNumber);
				mapRequese.put("WARNING_ID", warningId.toString());

				jsonList.add(mapRequese);
				mapRequese = new HashMap<String, Object>();

			}

			Gson gson = new Gson();
			String json = gson.toJson(jsonList);

			URL connectto = new URL(sms);

			HttpURLConnection conn = null;
			conn = (HttpURLConnection) connectto.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(false);
			conn.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(json);
			wr.flush();
			wr.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			String strContext = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
				strContext = line;
			}

			br.close();

		}

		URL url = new URL(request.getRequestURL().toString());

		Map<String, Object> map = new HashMap<>();
		String name1 = "客編異動簽核完成通知";

		String message1 = "您送簽之用戶 momo 客編異動簡訊已完成簽核，簽核人簽核意見為" + ("1".equals(opinion) ? "同意" : "駁回") + "，系統單號為："
				+ momoidChangeMainId.toString() + "。";
		String message2 = "請盡速進入系統確認用戶 momo 客編異動簡訊簽核結果。";
		String message3 = "客編異動簡訊發送紀錄查詢清單";
		map.put("name", name1);
		map.put("message1", message1);
		map.put("message2", message2);
		map.put("message3", message3);

		map.put("CustomerRewardSystem",
				url.getProtocol() + "://" + url.getHost() + "/momoidChange/findMomoidChangeRecord");

		try {
			BigDecimal momoidChangeApprovalId = momoidChangeApprovalEntity.getMomoidChangeApprovalId();
			var v = ReceiveInfoVo.builder().id(momoidChangeMainId.toString()).level(1)
					.type(momoidChangeApprovalId.toString()).build();
			var jsonStr = JsonUtil.objectToJson(v);
			var eJ = AESUtil.encryptStr(jsonStr, "9bJHnMNKU5WS7OV5II9JXgUIAGhUZ0Ao", "9NkKmHvnTKw7ywC9");
			var content = this.fullUrl(String.format("%s%s?parm=%s", RedirectController.REDIRECT_URI,
					RedirectController.APPRV_URI, URLEncoder.encode(eJ, "UTF-8")), request);

			Long accountId = momoidChangeMainEntity.getAccountId();
			AccountEntity accountEntity = accountRepo.findByAccountID1(accountId);
			String toEmail = accountEntity.getEmail();
			var mailVo = MailVo.builder().subject("客編異動簽核完成通知").from("crs@taiwanmobile.com")// .from(campaignService.fromEmail)
					.toEmail(toEmail).content(content).build();
			mailVo.setParams(map);

			mailUtils.sendMimeMail(mailVo);
		} catch (Exception e) {
			log.error("CampaignController campaignSetting6Save Error: {}", e.getMessage(), e);
			throw new Exception(e);
		}

		Map map2 = new HashMap();
		map2.put("btn_STATUS", momoidChangeApprovalEntity.getOpinion() == 1 ? "同意" : "駁回");
		map2.put("approval_DATE",
				DateUtilsEx.formatDate(momoidChangeApprovalEntity.getApprovalDate(), DateUtilsEx.DATE_PATTERN));
		map2.put("status", momoidChangeMainEntity.getStatus() == 1 ? "簽核駁回" : "待發送");

		return map2;

	}

	/**
	 * 點按簡訊發送紀錄查詢的table的撤回鈕，執行撤回功能
	 * 
	 * @param momoidChangeVo
	 * @return
	 */
	@Transactional(rollbackFor = { Exception.class })
	public MomoidChangeMainEntity withdrawMomoidChangeMain(MomoidChangeVo momoidChangeVo) {
		MomoidChangeMainEntity momoidChangeMainEntity = momoidChangeRepo
				.getById(new BigDecimal(momoidChangeVo.getMomoidChangeMainId()));
		momoidChangeMainEntity.setStatus(2);
		momoidChangeRepo.save(momoidChangeMainEntity);

		return momoidChangeMainEntity;

	}

	/**
	 * 點按簡訊發送紀錄查詢的table的簽核流程圖鈕，查詢簽核流聲圖的資訊 approvalId是table的系統單號
	 * 
	 * @param approvalId
	 * @return
	 */
	@Transactional
	public MomoidChangeMainDto signOffFlowChart(String approvalId) {

		MomoidChangeMainDto signOffFlowChart = momoidChangeRepo.signOffFlowChart(approvalId);
		return signOffFlowChart;
	}

}
