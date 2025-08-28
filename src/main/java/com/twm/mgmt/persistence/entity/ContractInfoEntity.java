package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.persistence.entity.pk.ContractInfoPk;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@IdClass(ContractInfoPk.class)
@Table(name = "CONTRACT_INFO", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class ContractInfoEntity implements Serializable {

	@Id
	@Column(name = "SUBID")
	private String subid;
	
	@Id
	@Column(name = "PROJECT_CODE")
	private String projectCode;
	
	@Id
	@Column(name = "PROJECT_SEQ_NBR")
	private String projectSeqNbr;
	
	@Id
	@Column(name = "CONTRACT_ID")
	private BigDecimal contractId;
	
	
	@Column(name = "MOMO_MEMBER_ID")
	private String momoMemberId;
	
	
}
