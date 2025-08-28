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
@Table(name = "CAMPAIGN_BLOCK_LIST", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class CampaignBlockListEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ROW_ID")
	private BigDecimal rowId;

	@Column(name = "CAMPAIGN_DETAIL_ID")
	private BigDecimal campaignDetailId;

	@Column(name = "SUBID")
	private String subId;

	@Column(name = "PROJECT_CODE")
	private String projectCode;
	
	@Column(name = "MEMO")
	private String memo;

	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "APPLY_DATE")
	private Date applydate;//<v20210812.M1.M_Comment> ADD

}
