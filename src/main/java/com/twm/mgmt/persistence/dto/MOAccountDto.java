package com.twm.mgmt.persistence.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MOAccountDto {

	private String merchantId;

	private String deptNo;

	private Date updateDate;

	private String contactUserName;

	private String departmentName;

}
