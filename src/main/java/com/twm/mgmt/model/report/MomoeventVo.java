package com.twm.mgmt.model.report;

import java.io.Serializable;

import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.RewardReportEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class MomoeventVo implements Serializable {


	private String momoenevt;

	public MomoeventVo(RewardReportEntity entity) {
		if(entity.getOrderNumber() != null) {
			this.momoenevt = entity.getOrderNumber();
		}
		
	}
}
