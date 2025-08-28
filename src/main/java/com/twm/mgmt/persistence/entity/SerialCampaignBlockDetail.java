package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.twm.mgmt.persistence.entity.pk.SerialCampaignBlockDetailId;

import lombok.Data;

@Entity
@Table(name="MOMOAPI.SERIAL_CAMPAIGN_BLOCK_DETAIL")
@Data
@IdClass(SerialCampaignBlockDetailId.class)
public class SerialCampaignBlockDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "row_id")
	@SequenceGenerator(name = "row_id", initialValue = 1, allocationSize = 1, sequenceName = "MOMOAPI.SERIAL_CAMPAIGN_BLOCK_DETAIL_ID")
	@Column(name="ROW_ID")
	private int rowId;
	
	@Id
	@Column(name="BLOCK_ID")
	private int blockId;
	
	@Id
	@Column(name="PROJECT_CODE")
	private String projectCode;
	
	@Id
	@Column(name="SUBID")
	private String subId;
	
	@Column(name="PROJECT_NAME")
	private String projectName;
	
	@Column(name="APPLY_DATE")
	private Date applyDate;
	
	@Column(name="MEMO")
	private String memo;
	
	@Column(name="CREATE_DATE")
	private Date createDate;
}