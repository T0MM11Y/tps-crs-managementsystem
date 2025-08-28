package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.persistence.entity.pk.MomoidChangeListPk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@IdClass(MomoidChangeListPk.class)
@Table(name = "MOMOID_CHANGE_LIST", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class MomoidChangeListEntity implements Serializable {

	@Id
	@Column(name = "MOMOID_CHANGE_MAIN_ID")
	private BigDecimal momoidChangeMainId;
	
	@Id
	@Column(name = "PROJECT_CODE")
	private String projectCode;
	
	@Id
	@Column(name = "PROJECT_SEQ_NBR")
	private String projectSeqNbr;
	
	@Id
	@Column(name = "SUBID")
	private String subid;
	
	@Id
	@Column(name = "MOMOID_CHANGE_LIST_ID")
	private BigDecimal momoidChangeListId;
	
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	
	@Column(name = "SEND_REASON")
	private String sendReason;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	@Column(name = "MOMOID_CHANGE_SMS_ID")
	private BigDecimal momoidChangeSmsId;
	
	@Column(name = "WARNING_ID")
	private BigDecimal warningId;
	
	@Column(name = "MOMO_MEMBER_ID")
	private String momoMemberId;
	

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "MOMOID_CHANGE_SMS_ID", insertable = false, updatable = false)
	private MomoidChangeSmsEntity momoidChangeSmsEntity;

}
