package com.twm.mgmt.model.account;

import java.io.Serializable;
import java.util.List;

import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.persistence.entity.APAccountEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class APAccountVo implements Serializable {

	private Long departmentId;

	private String sourceId;

	private String enabled;

	private List<APKeyIvVo> keyIvs;

	private ActionType action;
	
	private String requestId;
	
	private String contactAccountId;

	public APAccountVo(APAccountEntity entity) {
		this.departmentId = entity.getDepartmentId();
		this.sourceId = entity.getSourceId();
		this.enabled = entity.getEnabled();
	}

}
