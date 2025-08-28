package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "SERIAL_APPROVAL_DETAIL", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class SerialApprovalDetailEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "APPROVAL_DETAIL_ID")
	private BigDecimal approvalDetailId;
	
	@Column(name = "APPROVAL_TYPE")
	private String approvalType;
	
	@Column(name = "APPROVAL_BATCH_ID")
	private BigDecimal approvalBatchId;
	
	@Column(name = "CAMPAIGN_DETAIL_ID")
	private BigDecimal campaignDetailId;
	
	@Column(name = "REPORT_ID")
	private BigDecimal reportId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "CAMPAIGN_START_DATE")
	private Date campaignStartDate;
	
	@Column(name = "CAMPAIGN_END_DATE")
	private Date campaignEndDate;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "CREATE_ACCOUNT")
	private Long createAccount;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "UPDATE_ACCOUNT")
	private Long updateAccount;


}