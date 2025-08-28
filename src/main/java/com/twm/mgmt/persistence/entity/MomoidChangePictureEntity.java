package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "MOMOID_CHANGE_PICTURE", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class MomoidChangePictureEntity implements Serializable {

	@Id
	@Column(name = "MOMOID_CHANGE_MAIN_ID")
	private BigDecimal momoidChangeMainId;
	
	@Column(name = "PICTURE1_NAME")
	private String picture1Name;
	

	@Column(name = "PICTURE1_FILE")
	private byte[] picture1File;
	
	@Column(name = "PICTURE2_NAME")
	private String picture2Name;
	

	@Column(name = "PICTURE2_FILE")
	private byte[] picture2File;
	
	@Column(name = "PICTURE3_NAME")
	private String picture3Name;
	

	@Column(name = "PICTURE3_FILE")
	private byte[] picture3File;
	
	@Column(name = "PICTURE4_NAME")
	private String picture4Name;
	

	@Column(name = "PICTURE4_FILE")
	private byte[] picture4File;
	
	@Column(name = "PICTURE5_NAME")
	private String picture5Name;
	

	@Column(name = "PICTURE5_FILE")
	private byte[] picture5File;
	
	@Column(name = "PICTURE6_NAME")
	private String picture6Name;
	

	@Column(name = "PICTURE6_FILE")
	private byte[] picture6File;
	
	@Column(name = "PICTURE7_NAME")
	private String picture7Name;
	

	@Column(name = "PICTURE7_FILE")
	private byte[] picture7File;
	
	@Column(name = "PICTURE8_NAME")
	private String picture8Name;
	

	@Column(name = "PICTURE8_FILE")
	private byte[] picture8File;
	
	@Column(name = "PICTURE9_NAME")
	private String picture9Name;
	

	@Column(name = "PICTURE9_FILE")
	private byte[] picture9File;
	
	@Column(name = "PICTURE10_NAME")
	private String picture10Name;
	

	@Column(name = "PICTURE10_FILE")
	private byte[] picture10File;
	
	@Column(name = "PICTURE11_NAME")
	private String picture11Name;
	

	@Column(name = "PICTURE11_FILE")
	private byte[] picture11File;
	
	@Column(name = "PICTURE12_NAME")
	private String picture12Name;
	

	@Column(name = "PICTURE12_FILE")
	private byte[] picture12File;
	
	@Column(name = "PICTURE13_NAME")
	private String picture13Name;
	

	@Column(name = "PICTURE13_FILE")
	private byte[] picture13File;
	
	@Column(name = "PICTURE14_NAME")
	private String picture14Name;
	

	@Column(name = "PICTURE14_FILE")
	private byte[] picture14File;
	
	@Column(name = "PICTURE15_NAME")
	private String picture15Name;
	

	@Column(name = "PICTURE15_FILE")
	private byte[] picture15File;
		
	@Column(name = "CREATE_DATE")
	private Date createDate;
	

}
