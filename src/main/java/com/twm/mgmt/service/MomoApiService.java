package com.twm.mgmt.service;





import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twm.mgmt.controller.RedirectController;
import com.twm.mgmt.model.common.MailVo;
import com.twm.mgmt.model.common.ReceiveInfoVo;
import com.twm.mgmt.model.momoApi.MomoUpdateIdResponse;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeApprovalEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeListEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeMainEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeSmsEntity;
import com.twm.mgmt.persistence.entity.WarningContractEntity;
import com.twm.mgmt.persistence.repository.MomoidChangeApprovalRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeListRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeMainRepository;
import com.twm.mgmt.persistence.repository.MomoidChangeSmsRepository;
import com.twm.mgmt.persistence.repository.WarningContractRepository;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.MailUtils;

@Service
public class MomoApiService extends BaseService {

	@Autowired
	private MomoidChangeListRepository momoidChangeListRepo;
	
	@Autowired
	private MomoidChangeMainRepository momoidChangeMain;
	
	@Autowired
	private MomoidChangeSmsRepository momoidChangeSmsRepo;
	@Autowired
	private MomoidChangeApprovalRepository momoidChangeApprovalRepo;
	
	@Autowired
	private WarningContractRepository warningContractRepository;

	@Autowired
	private MailUtils mailUtils;
	@Transactional(rollbackFor = { Exception.class })
	public void getMomoidChangeSms(List<MomoUpdateIdResponse> momoList,HttpServletRequest request) throws Exception {
		for (MomoUpdateIdResponse momoUpdateIdResponse : momoList) {
			BigDecimal warning_ID = momoUpdateIdResponse.getWARNING_ID();
			MomoidChangeListEntity momoidChangeListEntity = momoidChangeListRepo.findByWarningId(warning_ID);			
			BigDecimal momoidChangeMainId = momoidChangeListEntity.getMomoidChangeMainId();
			momoUpdateIdResponse.setMOMOID_CHANGE_MAIN_ID(momoidChangeMainId);
			
			BigDecimal momoidChangeSmsId = momoidChangeListEntity.getMomoidChangeSmsId();
			MomoidChangeSmsEntity momoidChangeSmsEntity = momoidChangeSmsRepo.getById(momoidChangeSmsId);
			Integer status = momoUpdateIdResponse.getSTATUS();
			momoidChangeSmsEntity.setStatus(status);
			Date parseDate = DateUtilsEx.parseDate(momoUpdateIdResponse.getSMS_DATE(),DateUtilsEx.DATETIME_PATTERN);
			momoidChangeSmsEntity.setSmsDate(parseDate);
			momoidChangeSmsRepo.save(momoidChangeSmsEntity);
			
			WarningContractEntity warningContractEntity = warningContractRepository.getById(warning_ID);
			warningContractEntity.setSmsLastSent(parseDate);
			Date updateDate = new Date();
			warningContractEntity.setUpdateDate(updateDate);
			warningContractRepository.save(warningContractEntity);
		}
		
		
		Map<BigDecimal, List<MomoUpdateIdResponse>> momoUpdateIdResponseByMOMOID_CHANGE_MAIN_ID = momoList.stream().collect(Collectors.groupingBy(MomoUpdateIdResponse::getMOMOID_CHANGE_MAIN_ID));
		
		for (Map.Entry<BigDecimal, List<MomoUpdateIdResponse>> entry : momoUpdateIdResponseByMOMOID_CHANGE_MAIN_ID.entrySet()) {
			BigDecimal key = entry.getKey();
			MomoidChangeMainEntity momoidChangeMainEntity = momoidChangeMain.getById(key);
			momoidChangeMainEntity.setStatus(4);
			momoidChangeMain.save(momoidChangeMainEntity);
			
			int a = 0;
			int b = 0;
			for (MomoUpdateIdResponse momoUpdateIdResponse : entry.getValue()) {
				if(momoUpdateIdResponse.getSTATUS() == 4) {
					a++;
				}
				
				if (momoUpdateIdResponse.getSTATUS() == 5) {
					b++;
				}
			}
			
			URL url = new URL(request.getRequestURL().toString());
			
			Map<String, Object> map = new HashMap<>();
			String name1 = "客編異動簡訊發送完成通知";
			String message1 = "您送簽之用戶 momo 客編異動簡訊已發送完成，共計" + a + "筆成功、" + b + "筆失敗，系統單號為："+key.toString()+"。";
			String message2 = "請盡速進入系統確認用戶 momo 客編異動簡訊發送結果。";
			String message3 = "客編異動簡訊發送紀錄查詢清單";
			map.put("name", name1);
			map.put("message1", message1);
			map.put("message2", message2);
			map.put("message3", message3);

			map.put("CustomerRewardSystem",url.getProtocol()+"://"+url.getHost()+"/momoidChange/findMomoidChangeRecord");
			

			// send mail 有 bug
			try {
				 MomoidChangeApprovalEntity momoidChangeApprovalEntity = momoidChangeApprovalRepo.findByMomoidChangeMainId(key);
				BigDecimal momoidChangeApprovalId = momoidChangeApprovalEntity.getMomoidChangeApprovalId();
				var v = ReceiveInfoVo.builder().id(key.toString()).level(1).type(momoidChangeApprovalId.toString()).build();
				// System.out.println("campaignSetting6Save mail1");
				var jsonStr = JsonUtil.objectToJson(v);
				var eJ = AESUtil.encryptStr(jsonStr, "9bJHnMNKU5WS7OV5II9JXgUIAGhUZ0Ao", "9NkKmHvnTKw7ywC9");
				var content = this.fullUrl(String.format("%s%s?parm=%s", RedirectController.REDIRECT_URI,
						RedirectController.APPRV_URI, URLEncoder.encode(eJ, "UTF-8")), request);
				
				Long accountId = momoidChangeMainEntity.getAccountId();
				AccountEntity accountEntity = accountRepo.findByAccountID1(accountId);					
				String toEmail = accountEntity.getEmail();
				var mailVo = MailVo.builder().subject("客編異動簡訊發送完成通知").from("crs@taiwanmobile.com")// .from(campaignService.fromEmail)
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
		
	
	

	
	
	
	}
	

	


}
