package com.twm.mgmt.persistence.dto;

import java.util.Date;

import lombok.Data;

@Data
public class APAccountDto {

	private Long departmentId;

	private String departmentName;

	private String sourceId;

	private String enabled;

	private Date createDate;

}
