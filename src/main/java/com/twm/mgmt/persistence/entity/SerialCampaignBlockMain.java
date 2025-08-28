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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name="MOMOAPI.SERIAL_CAMPAIGN_BLOCK_MAIN")
@Data
public class SerialCampaignBlockMain implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "block_id")
	@SequenceGenerator(name = "block_id", initialValue = 1, allocationSize = 1, sequenceName = "SERIAL_CAMPAIGN_BLOCK_MAIN_ID")
	@Column(name="BLOCK_ID")
	private int blockId;
	
	@Column(name="CAMPAIGN_MAIN_ID")
	private int campaignMainId;
	
	@Column(name="CREATE_ACCOUNT")
	private int createAccount;
	
	@Column(name="IS_ACTIVE")
	private boolean isActive;
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_DATE")
	private Date createDate;
}
