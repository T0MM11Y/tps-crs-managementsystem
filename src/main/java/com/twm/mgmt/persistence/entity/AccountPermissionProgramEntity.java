package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.Data;

@Data
@Table(name = "ACCOUNT_PERMISSION_PROGRAM", schema = MoDbConfig.ACCOUNT_SCHEMA)
@Entity
@SuppressWarnings("serial")
public class AccountPermissionProgramEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountPermissionProgramSeq")
	@SequenceGenerator(name = "accountPermissionProgramSeq", sequenceName = "ACCOUNT_PERMISSION_PROGRAM_SEQ", allocationSize = 1)
	@Column(name = "PERMISSION_ID")
	private Long permissionId;

	@Column(name = "ACCOUNT_ID")
	private Long accountId;

	@Column(name = "PROGRAM_ID")
	private Long programId;

	@Column(name = "ENABLED")
	private String enabled = "N";

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "CREATE_ACCOUNT")
	private Long createAccount;

}
