package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "SERIAL_DATA", schema = MoDbConfig.REWARD_SCHEMA)
@Entity
@SuppressWarnings("serial")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerialDataEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serial_data_id")
	@SequenceGenerator(name = "serial_data_id", initialValue = 1, allocationSize = 1, sequenceName = "SERIAL_DATA_ID")
	@Column(name = "SERIAL_DATA_ID")
	private BigDecimal serialDataId;
	//SERIAL_DATA_ID沒有關聯到序號增長,因此採用這種寫法

	@Column(name = "SERIAL_REWARD_REPORT_ID")
	private BigDecimal serialRewardReportId;

	@Column(name = "SERIAL_REWARD_REPORT_DETAIL_ID")
	private BigDecimal serialRewardReportDetailId;

	@Column(name = "SERIAL_TRANSACTION_OFFER_ID")
	private BigDecimal serialTransactionOfferId;

	@Column(name = "SUBID")
	private String subId;

	@Column(name = "SERIALID")
	private String serialId;

	@Column(name = "SERIALNO")
	private String serialNo;

	@Column(name = "PLUS_AMOUNT")
	private BigDecimal plusAmount;

	@Column(name = "SHORTURL")
	private String shortUrl;

	@Column(name = "DEADLINE_DATE")
	private Date deadlineDate;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	@Column(name = "UPDATE_ACCOUNT")
	private String updateAccount;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "CREATE_ACCOUNT")
	private String createAccount;

}