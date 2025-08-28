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
@Table(name = "ROLE_PERMISSION_PROGRAM", schema = MoDbConfig.ACCOUNT_SCHEMA)
@Entity
@SuppressWarnings("serial")
public class RolePermissionProgramEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolePermissionProgramSeq")
	@SequenceGenerator(name = "rolePermissionProgramSeq", sequenceName = "ROLE_PERMISSION_PROGRAM_SEQ", allocationSize = 1)
	@Column(name = "PERMISSION_ID")
	private Long permissionId;

	@Column(name = "ROLE_ID")
	private Long roleId;

	@Column(name = "PROGRAM_ID")
	private Long programId;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "CREATE_ACCOUNT")
	private Long createAccount;

}
