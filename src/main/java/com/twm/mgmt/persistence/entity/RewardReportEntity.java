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
@Table(name = "REWARD_REPORT", schema = MoDbConfig.REWARD_SCHEMA)
@Entity
@SuppressWarnings("serial")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardReportEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REPORT_ID")
	private BigDecimal reportId;

	@Column(name = "CAMPAIGN_DETAIL_ID")
	private BigDecimal campaignDetailId;

	@Column(name = "CAMPAIGN_INFO")
	private String campaignInfo;

	@Column(name = "APPROVAL_MAIN_ID")
	private String approvalMainId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "TOTAL_REWARD_USERS")
	private BigDecimal totalRewardUsers;

	@Column(name = "TOTAL_REWARD_AMOUNT")
	private BigDecimal totalRewardAmount;

	@Column(name = "REWARD_DATE")
	private Date rewardDate;

	@Column(name = "CHOOSE_BEST_DATE")
	private Date chooseBestDate;

	@Column(name = "SEND_APPROVAL_DATE")
	private Date sendApprovalDate;

	@Column(name = "MOMO_EVENT_NO")
	private String momoEventNo;

	@Column(name = "AMOUNT_VALIDITY_DATE")
	private Date amountValidityDate;
	
	@Column(name = "ORDER_NUMBER")
	private String orderNumber;	
	
	@Column(name = "PAY_ACCOUNT")
	private String payAccount;	
	
	
	@Column(name = "REQUISITIONER")
	private String requisitioner;	
	
	@Column(name = "REQUISITION_UNIT")
	private String requisitionUnit;	

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "EVENT_INFO_DONE_DATE")
	private Date eventInfoDoneDate;
	
	@Column(name = "AMOUNT_VALIDITY_START_DATE")
	private Date amountValidityStartDate;

}
