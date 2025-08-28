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
public class PayaccountVo implements Serializable {


	private String payAccount;

	public PayaccountVo(RewardReportEntity entity) {
		if(entity.getPayAccount() != null) {
			this.payAccount = entity.getPayAccount();
		}
		
	}
}
