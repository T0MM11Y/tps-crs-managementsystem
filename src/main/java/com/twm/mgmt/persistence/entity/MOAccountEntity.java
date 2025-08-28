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
@Table(name = "MO_ACCOUNT", schema = MoDbConfig.ACCOUNT_SCHEMA)
@IdClass(MOAccountEntity.class)
@Entity
@SuppressWarnings("serial")
public class MOAccountEntity implements Serializable {

	@Id
	@Column(name = "MERCHANT_ID")
	private String merchantId;

	@Column(name = "DEPARTMENT_ID")
	private Long departmentId;

	@Column(name = "CONTACT_ACCOUNT_ID")
	private Long contactAccountId;

	@Id
	@Column(name = "DEPT_NO")
	private String deptNo;

	@Column(name = "API_KEY")
	private String apiKey;

	@Column(name = "ENCRYPTED_KEY")
	private String encryptedKey;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	
	public MOAccountEntity(String merchantId, String deptNo) {
		this.merchantId = merchantId;
		this.deptNo = deptNo; 
	}
	
	
	public MOAccountEntity() {

	}

}
