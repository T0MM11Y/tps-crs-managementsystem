package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.Data;

@Data
@Table(name = "MOACCOUNT_MAP_ACCOUNT", schema = MoDbConfig.ACCOUNT_SCHEMA)
@IdClass(MoAccountMapAccountEntity.class)
@Entity
@SuppressWarnings("serial")
public class MoAccountMapAccountEntity implements Serializable {
	@Id
	@Column(name = "FUNCTIONID")
	Long functionId;
	
	@Column(name = "FUNCTIONTITLE")
	String functionTitle;
	
	@Id
	@Column(name = "MODEPTNO")
	String moDeptNo;
	
	@Id
	@Column(name = "CRSACCOUNTID")
	Long crsAccountId;
	
	@Column(name = "CRSUSERNAME")
	String crsUserName;
	
	@Column(name = "DEPARTMENTID")
	Long departmentId;
	
	@Column(name = "DEPARTMENT")
	String department;
	
	@Column(name = "CREATEDATE")
	Date createDate;
	
	@Column(name = "UPDATEDATE")
	Date updateDate;
	
	public MoAccountMapAccountEntity(Long functionId, String moDeptNo, Long crsAccountId) {
		this.functionId = functionId;
		this.moDeptNo = moDeptNo; 
		this.crsAccountId = crsAccountId; 
	}
	
	public MoAccountMapAccountEntity() {
	}
}
