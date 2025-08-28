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
@Table(name = "SERIAL_APPROVAL_BATCH", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class SerialApprovalBatchEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "APPROVAL_BATCH_ID")
	private BigDecimal approvalBatchId;
	
	@Column(name = "APPROVAL_TYPE")
	private String approvalType;
	
	@Column(name = "STATUS")
	private String status;
	
	@Lob
	@Column(name = "GROUP_ID",columnDefinition="CLOB")
	private String groupId;

	@Column(name = "L1_ACCOUNT")
	private Long l1Account;
	
	@Column(name = "L1_STATUS")
	private String l1Status;
	
	@Column(name = "L1_COMMENT_INFO")
	private String l1CommentInfo;
	
	@Column(name = "L1_CHECK_DATE")
	private Date l1CheckDate;
	
	@Column(name = "L2_ACCOUNT")
	private Long l2Account;
	
	@Column(name = "L2_STATUS")
	private String l2Status;
	
	@Column(name = "L2_COMMENT_INFO")
	private String l2CommentInfo;
	
	@Column(name = "L2_CHECK_DATE")
	private Date l2CheckDate;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "CREATE_ACCOUNT")
	private Long createAccount;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "UPDATE_ACCOUNT")
	private Long updateAccount;

	

}