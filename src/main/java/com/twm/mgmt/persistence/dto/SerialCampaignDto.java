package com.twm.mgmt.persistence.dto;



import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import lombok.Data;

@Data
public class SerialCampaignDto {

	private String BTN_STATUS;
	private BigDecimal CAMPAIGN_DETAIL_ID;
	private String CAMPAIGN_NAME;
	private String APPLY_TYPE;
	private String CAMPAIGN_DONE_DATE;
	private String CAMPAIGN_START_DATE;
	private String CAMPAIGN_END_DATE;
	private String STATUS_STR;
	private String CHANGE_REMARK;
	private String STATUS;
	private Date CAMPAIGN_SDATE;
	private Date CAMPAIGN_EDATE;
	private BigDecimal REF_CAMPAIGN_DETAIL_ID;
	private BigDecimal EDIT_CAMPAIGN_DETAIL_ID;
	private String EDIT_STATUS;
	private Date EDIT_CAMPAIGN_SDATE;	
	private Date EDIT_CAMPAIGN_EDATE;
	private BigDecimal REPORT_ID;
	private String PROJECT_CODE;
	private String REWARD_MONTH;
	private String REPORT_DONE_DATE;
	private String BUTTON;
	private String PROJECT_TYPE;
	private String REPORT_STATUS_DATE;
	private String DELAY;
	private String EVENTNO;
	private BigDecimal EVENTDTID;
	
	

}
