package com.twm.mgmt.persistence.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MoAccountMapAccountDto {
	private Long functionId;
	private String functionTitle;
	private String moDeptNo;
	private Long crsAccountId;
	private String crsUserName;
	private Long departmentId;
	private String department;
	private Date createDate;
	private Date updateDate;
}
