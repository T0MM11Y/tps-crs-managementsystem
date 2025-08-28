package com.twm.mgmt.model.account;

import java.io.Serializable;
import java.util.Date;

import com.twm.mgmt.persistence.dto.MoAccountMapAccountDto;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class MoAccountMapAccountResultVo implements Serializable {
	Long functionId;
	String functionTitle;
	String moDeptNo;
	Long crsAccountId;
	String crsUserName;
	Long departmentId;
	String department;
	Date createDate;
	Date updateDate;
	public MoAccountMapAccountResultVo(MoAccountMapAccountDto dto) {
		this.functionId = dto.getFunctionId();
		this.functionTitle = dto.getFunctionTitle();
		this.moDeptNo = dto.getMoDeptNo();
		this.crsAccountId = dto.getCrsAccountId();
		this.crsUserName = dto.getCrsUserName();
		this.departmentId = dto.getDepartmentId();
		this.department = dto.getDepartment();
		this.createDate = dto.getCreateDate();//DateUtilsEx.formatDate(dto.getCreateDate());
		this.updateDate = dto.getUpdateDate();//DateUtilsEx.formatDate(dto.getUpdateDate());
	}
}
