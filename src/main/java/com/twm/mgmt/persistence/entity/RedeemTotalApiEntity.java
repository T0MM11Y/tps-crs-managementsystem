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
@Table(name = "REDEEM_TOTAL_API", schema = MoDbConfig.ACCOUNT_SCHEMA)
@IdClass(RedeemTotalApiEntity.class)
@Entity
@SuppressWarnings("serial")
public class RedeemTotalApiEntity implements Serializable {
	
	@Id
	@Column(name = "MODEPTNO")
	String moDeptNo;
	
	@Column(name = "APIURL")
	String apiUrl;
	
	@Column(name = "CREATEDATE")
	Date createDate;
	
	public RedeemTotalApiEntity(Long functionId, String moDeptNo, String apiUrl) {
		this.moDeptNo = moDeptNo; 
		this.apiUrl = apiUrl; 
	}
	
	public RedeemTotalApiEntity() {
	}
}
