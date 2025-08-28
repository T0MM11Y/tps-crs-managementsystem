package com.twm.mgmt.model.account;

import java.io.Serializable;

import com.twm.mgmt.persistence.dto.MOAccountDto;
import com.twm.mgmt.utils.DateUtilsEx;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class MOAccountResultVo implements Serializable {

	private String merchantId;

	private String deptNo;

	private String updateDate;

	private String contactUserName;

	private String departmentName;

	public MOAccountResultVo(MOAccountDto dto) {
		this.merchantId = dto.getMerchantId();
		this.deptNo = dto.getDeptNo();
		this.departmentName = dto.getDepartmentName();
		this.contactUserName = dto.getContactUserName();
		this.updateDate = DateUtilsEx.formatDate(dto.getUpdateDate());
	}

}
