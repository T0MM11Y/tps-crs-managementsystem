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
@Table(name = "TRANSACTION_OFFER_HISTORY", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class TransactionofferhistoryEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TRANSACTION_OFFER_ID")
	private BigDecimal transactionofferid;
	
	@Column(name = "REPORT_DETAIL_ID")
	private BigDecimal reportDetailId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "FAIL_STATUS")
	private String failStatus;
	
	@Column(name = "REQUEST_ID")
	private String requestId;
	
	@Column(name = "TWM_UID")
	private String twmUid;
	
	@Column(name = "SUBID")
	private String subId;
	
	@Column(name = "MOMO_MEMBER_ID")
	private String momoMemberId;
	
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	
	@Column(name = "MOMO_TX_ID")
	private String momoTxId;
	
	@Column(name = "MO_OFFER_ID")
	private String moOfferId;
	
	@Column(name = "MO_TX_TIME")
	private Date moTxTime;
	
	@Column(name = "ORDER_NOTE")
	private String orderNote;
	
	@Column(name = "AMOUNT")
	private BigDecimal amount;
	
	@Column(name = "IS_SEND_SMS")
	private BigDecimal isSendSms;
	
	@Column(name = "MO_RES_CODE")
	private String moResCode;
	
	@Column(name = "MO_RES_MESSAGE")
	private String moResMessage;
	
	@Column(name = "CAMPAIGN_KIND")
	private BigDecimal campaignKind;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;




}
