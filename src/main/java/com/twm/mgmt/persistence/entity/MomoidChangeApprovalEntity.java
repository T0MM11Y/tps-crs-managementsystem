package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.persistence.entity.pk.APAccountPk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "MOMOID_CHANGE_APPROVAL", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class MomoidChangeApprovalEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MOMOID_CHANGE_APPROVAL_ID")
	private BigDecimal momoidChangeApprovalId;
	
	@Column(name = "MOMOID_CHANGE_MAIN_ID")
	private BigDecimal momoidChangeMainId;
	
	@Column(name = "ACCOUNT_ID")
	private Long accountId;
	
	@Column(name = "COMMENT_INFO")
	private String commentInfo;
		
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "APPROVAL_DATE")
	private Date approvalDate;
	
	@Column(name = "OPINION")
	private Integer opinion;
	

	

}
