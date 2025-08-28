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

import com.twm.mgmt.config.MoDbConfig;

import lombok.Data;

@Data
@Table(name = "AP_KEY_IV", schema = MoDbConfig.ACCOUNT_SCHEMA)
@Entity
@SuppressWarnings("serial")
public class APKeyIvEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apKeyIvSeq")
	@SequenceGenerator(name = "apKeyIvSeq", sequenceName = "AP_KEY_IV_SEQ", allocationSize = 1)
	@Column(name = "KEY_IV_ID")
	private Long keyIvId;

	@Column(name = "SOURCE_ID")
	private String sourceId;

	@Column(name = "KEY")
	private String key;

	@Column(name = "IV")
	private String iv;

	@Column(name = "EXPIRED_DATE")
	private Date expiredDate;

	@Column(name = "CREATE_DATE")
	private Date createDate;

}
