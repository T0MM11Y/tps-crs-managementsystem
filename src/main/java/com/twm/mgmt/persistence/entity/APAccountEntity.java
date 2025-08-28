package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.persistence.entity.pk.APAccountPk;

import lombok.Data;

@Data
@IdClass(APAccountPk.class)
@Table(name = "AP_ACCOUNT", schema = MoDbConfig.ACCOUNT_SCHEMA)
@Entity
@SuppressWarnings("serial")
public class APAccountEntity implements Serializable {

	@Id
	@Column(name = "DEPARTMENT_ID")
	private Long departmentId;

	@Id
	@Column(name = "SOURCE_ID")
	private String sourceId;

	@Column(name = "ENABLED")
	private String enabled = "Y";

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;

}
