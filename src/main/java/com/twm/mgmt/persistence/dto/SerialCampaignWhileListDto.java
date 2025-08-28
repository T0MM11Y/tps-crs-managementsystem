package com.twm.mgmt.persistence.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public  class SerialCampaignWhileListDto {
	private String LEVEL_NO;
	private BigDecimal BATCH_ID;
	private String CAMPAIGN_STATUS;
	private String REPORT_STATUS;
	private String BTN_STATUS;
	private String MOMO_EVENT_NO;
	private BigDecimal SYS_ID;
	private Date REWARD_DATE;
	private String PAY_ACCOUNT;
	private String ORDER_NUMBER;
	private BigDecimal TOTAL_REWARD_USERS;
	private BigDecimal TOTAL_REWARD_AMOUNT;
	private String APPLYNAME;
	private String CAMPAIGN_NAME;
	private String CAMPAIGN_INFO;
	private String SEND_DATE;
	private String SIGNNAME;
	private String FIRST_USER_NAME;
	private String SECOND_USER_NAME;
	private String STATUS;
	private BigDecimal CAMPAIGN_DETAIL_ID;
	private Long APPLYID;	
	private Long FIRST_ACCOUNT_ID;
	private Long SECOND_ACCOUNT_ID;
	
}
