package com.twm.mgmt.model.serialCampaign;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.springframework.web.multipart.MultipartFile;

import com.twm.mgmt.model.common.PaginationVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")

public class SerialCampaignSettingVo  extends PaginationVo  implements Serializable  {
	
	/*step1*/
	private BigDecimal  SerialCampaignDetailId;
	private String campaignName;
	private String campaignInfo;
	private String campaignStartDate;
	private String campaignEndDate;
	private Integer isChooseBest;
	private String[] campaignApplyType;
	private String campaignApplySeed;
	private Integer isCheckConstruct;
	private String constructedStartDate;
	private String constructedEndDate;
	private transient MultipartFile projectFile;
	private String contextsignOffFile;
	private String btn_status;	
	private Integer campaignApplyTypeNO;
	private Integer campaignApplySeedNO;
	private String edit_campaign_Sdate;
	private String edit_campaign_Edate;
	
	/*step2*/	
	private String notifyAccountCBG;
	private String notifyAccountEBG;
	private String notifyAccountSMG;
	
	/*step3*/	
	private String projectPeriod;
	private Integer hasSolid;
	private BigDecimal ruleGroupId;
	
	/*step4*/
	private Integer enabledAcctType;
	private Integer countAcctType;
	private Integer enabledBcId;	
	private String acctTypeRewardAmount;
	private transient MultipartFile projectFile2;
	private String contextsignOffFile2;
	
	/*發幣條件查詢*/
	private String campaignDoneDate;
	private String status;
	private String changeRemark;
	private Long accountId;
	private Integer aq;
	private Integer np;
	private Integer rt;
	
	/*發幣活動資訊查詢*/
	private String projectCode;
	private String reportDoneDate;
	private String updateDate;
	private String rewardDate;
	private BigDecimal reportId;
	
	/*發幣活動資訊新增*/
	private String momoEventNo;
	private String payAccount;
	private transient MultipartFile projectFile3;
	private String orderNumber;
	private String requisitionUnit;
	private String requisitioner;
	private List<Integer> reportIds;
	
	

}
