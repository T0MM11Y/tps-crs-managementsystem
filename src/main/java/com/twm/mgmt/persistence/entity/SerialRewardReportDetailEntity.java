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
@Table(name = "SERIAL_REWARD_REPORT_DETAIL", schema = MoDbConfig.REWARD_SCHEMA)
@Entity
@SuppressWarnings("serial")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerialRewardReportDetailEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REPORT_DETAIL_ID")
	private BigDecimal reportDetailId;

	@Column(name = "REPORT_ID")
	private BigDecimal reportId;

	@Column(name = "CAMPAIGN_DETAIL_ID")
	private BigDecimal campaignDetailId;

	@Column(name = "SUBID")
	private String subId;
	
	@Column(name = "SERIALID")
	private String serialid;

	@Column(name = "PROJECT_SEQ_NBR")
	private String projectSeqNbr;

	@Column(name = "PROJECT_CODE")
	private String projectCode;
	
	@Column(name = "TRANSACTION_ID")
	private BigDecimal transactionId;


	@Column(name = "STATUS")
	private String status;

	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	
	@Column(name = "PHONE_STATUS")
	private String phoneStatus;
	
	@Column(name = "CONTRACT_STATUS")
	private String contractStatus;
	
	
	@Column(name = "CONTRACT_END_REASON")
	private String contractEndReason;

	@Column(name = "CONSTRUCT_STATUS")
	private String constructStatus;
	
	@Column(name = "BASIC_AMOUNT")
	private BigDecimal basicAmount;
	
	@Column(name = "ACCT_TYPE_AMOUNT")
	private BigDecimal acctTypeAmount;
	
	@Column(name = "BC_ID_AMOUNT")
	private BigDecimal bdIdAmount;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "CONTRACT_ID")
	private BigDecimal contractId;
	


}
