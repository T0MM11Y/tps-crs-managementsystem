package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "SERIAL_CAMPAIGN_FILE", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class SerialCampaignFileEntity implements Serializable {

	@Id
	@Column(name = "CAMPAIGN_DETAIL_ID")
	private BigDecimal campaignDetailId;
	
	@Column(name = "FILE_NAME")
	private String fileName;

	@Lob
	@Column(name = "FILE_CONTENT")
	private byte[] fileContent;

	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	


}
