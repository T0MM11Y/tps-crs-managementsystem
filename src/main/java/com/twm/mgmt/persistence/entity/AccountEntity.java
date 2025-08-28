package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.Data;

@Data
@Table(name = "ACCOUNT", schema = MoDbConfig.ACCOUNT_SCHEMA)
@Entity
@SuppressWarnings("serial")
public class AccountEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSeq")
	@SequenceGenerator(name = "accountSeq", sequenceName = "ACCOUNT_SEQ", allocationSize = 1)
	@Column(name = "ACCOUNT_ID")
	private Long accountId;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "MOBILE")
	private String mobile;

	@Column(name = "ROLE_ID")
	private Long roleId;

	@Column(name = "DEPARTMENT_ID")
	private Long departmentId;

	@Column(name = "APPROVABLE")
	private String approvable = "N";

	@Column(name = "ENABLED")
	private String enabled = "Y";

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "CREATE_ACCOUNT")
	private Long createAccount;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	@Column(name = "UPDATE_ACCOUNT")
	private Long updateAccount;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ROLE_ID", insertable = false, updatable = false)
	private RoleEntity role;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "DEPARTMENT_ID", insertable = false, updatable = false)
	private DepartmentEntity department;

}
