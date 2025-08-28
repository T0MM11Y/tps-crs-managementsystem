package com.twm.mgmt.model.account;

import java.io.Serializable;

import com.twm.mgmt.persistence.dto.APAccountDto;
import com.twm.mgmt.utils.DateUtilsEx;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class APAccountResultVo implements Serializable {

	private Long departmentId;

	private String departmentName;

	private String sourceId;

	private String enabled;

	private String createDate;

	public APAccountResultVo(APAccountDto dto) {
		this.departmentId = dto.getDepartmentId();
		this.sourceId = dto.getSourceId();
		this.departmentName = dto.getDepartmentName();
		this.enabled = dto.getEnabled();
		this.createDate = DateUtilsEx.formatDate(dto.getCreateDate());
	}

}
