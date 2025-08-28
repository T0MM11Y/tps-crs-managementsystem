package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.persistence.entity.pk.APAccountMAPAccountPk;


import lombok.Data;

@Data
@IdClass(APAccountMAPAccountPk.class)
@Table(name = "AP_ACCOUNT_MAP_ACCOUNT", schema = MoDbConfig.ACCOUNT_SCHEMA)
@Entity
@SuppressWarnings("serial")
public class APAccountMAPAccountEntity implements Serializable {

	@Id
	@Column(name = "DEPARTMENT_ID")
	private Long departmentId;

	@Id
	@Column(name = "SOURCE_ID")
	private String sourceId;

	@Column(name = "ACCOUNTNAME")
	private String accountname;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;

}
