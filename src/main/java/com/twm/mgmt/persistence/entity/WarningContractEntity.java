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
@Table(name = "WARNING_CONTRACT", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class WarningContractEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WARNING_ID")
	private BigDecimal warningId;
	
	@Column(name = "SUBID")
	private String subId;
	
	@Column(name = "PROJECT_SEQ_NBR")
	private String projectSeqNbr;
	
	@Column(name = "PROJECT_CODE")
	private String projectCode;
	
	@Column(name = "CONTRACT_ID")
	private BigDecimal contractId;
	
	@Column(name = "REWARD_TYPE")
	private Integer rewardType;
	
	@Column(name = "REWARD_ID")
	private String rewardId;
	
	@Column(name = "WARN_TYPE")
	private String warnType;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "SMS_LAST_SENT")
	private Date smsLastSent;
	

}
