package com.twm.mgmt.model.report;

import java.io.Serializable;

import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class momoDepartmentVo implements Serializable {

	private Long departmentId;

	private String departmentName;

	private ActionType action;

	public momoDepartmentVo(MOAccountEntity entity) {
		this.departmentId = entity.getDepartmentId();
		this.departmentName = entity.getDeptNo();
	}

}
