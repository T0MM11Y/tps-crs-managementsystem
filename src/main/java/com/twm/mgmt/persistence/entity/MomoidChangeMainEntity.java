package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "MOMOID_CHANGE_MAIN", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class MomoidChangeMainEntity implements Serializable {

	@Id
	@Column(name = "MOMOID_CHANGE_MAIN_ID")
	private BigDecimal momoidChangeMainId;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "MOMOID_CHANGE_MAIN_ID_TYPE")
	private Integer momoidChangeMainIdType;
		
	@Column(name = "ACCOUNT_ID")
	private Long accountId;
	
	@Column(name = "STATUS")
	private Integer status;
}
