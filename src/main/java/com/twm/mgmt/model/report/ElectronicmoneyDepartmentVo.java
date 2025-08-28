package com.twm.mgmt.model.report;

import java.io.Serializable;
import java.math.BigDecimal;

import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.entity.RewardReportEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class ElectronicmoneyDepartmentVo implements Serializable {

	private BigDecimal departmentId;

	private String departmentName;

	private ActionType action;
//	requisitionUnit
	public ElectronicmoneyDepartmentVo(RewardReportEntity entity) {
		this.departmentId = entity.getReportId();
		this.departmentName = entity.getRequisitionUnit();
	}

}
