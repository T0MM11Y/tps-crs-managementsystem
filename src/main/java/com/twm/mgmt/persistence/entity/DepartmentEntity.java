package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "DEPARTMENT")
@Entity
@SuppressWarnings("serial")
public class DepartmentEntity implements Serializable {

	@Id
	@Column(name = "DEPARTMENT_ID")
	private Long departmentId;

	@Column(name = "DEPARTMENT_NAME")
	private String departmentName;

	@Column(name = "ENABLED")
	private String enabled = "Y";

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "BU_TAG")
	private String buTag;

}
