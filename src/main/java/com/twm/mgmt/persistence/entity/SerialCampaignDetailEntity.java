package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "SERIAL_CAMPAIGN_DETAIL", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class SerialCampaignDetailEntity implements Serializable {

	@Id
	@Column(name = "CAMPAIGN_DETAIL_ID")
	private BigDecimal campaignDetailId;

	@Column(name = "CAMPAIGN_MAIN_ID")
	private BigDecimal campaignMainId;

	@Column(name = "APPROVAL_MAIN_ID")
	private String approvalMainId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CAMPAIGN_NAME")
	private String campaignName;

	@Column(name = "CAMPAIGN_INFO")
	private String campaignInfo;

	@Column(name = "PROJECT_FILE_PATH")
	private String projectFilePath;

	@Column(name = "SIGN_OFF_FILE_PATH")
	private String signOffFilePath;

	@Column(name = "REWARD_MONTH_RANGE")
	private BigDecimal rewardMonthRange;

	@Column(name = "REWARD_DAY_OF_MONTH")
	private BigDecimal rewardDayOfMonth;

	@Column(name = "REPORT_DAY_OF_MONTH")
	private BigDecimal reportDayOfMonth;

	@Column(name = "CAMPAIGN_START_DATE")
	private Date campaignStartDate;

	@Column(name = "CAMPAIGN_END_DATE")
	private Date campaignEndDate;

	@Column(name = "IS_CHOOSE_BEST")
	private Integer isChooseBest;

	@Column(name = "IS_CHECK_CONTRACT")
	private Integer isCheckContract;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "CREATE_ACCOUNT")
	private Long createAccount;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	@Column(name = "UPDATE_ACCOUNT")
	private Long updateAccount;
	


	@Column(name = "IS_CHECK_MOBILE_STATUS")
	private Integer isCheckMobileStatus;
	
	@Column(name = "IS_AUTO_MONTH_INFO")
	private Integer isAutoMonthInfo;
	
	
	@Column(name = "IS_CHECK_CONSTRUCT")
	private Integer isCheckConstruct;
	
	@Column(name = "PROJECT_TYPE")
	private String projectType;
	
	@Column(name = "REF_CAMPAIGN_DETAIL_ID")
	private BigDecimal refCampaignDetailId;

	@Column(name = "IS_BC_ACCT_REWARD_SETTING")
	private Integer isBcAcctRewardSetting;
	

}
