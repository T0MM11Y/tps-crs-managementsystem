package com.twm.mgmt.model.account;

import java.io.Serializable;

import com.twm.mgmt.enums.ActionType;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class MOAccountVo implements Serializable {

	private String merchantId;

	private Long departmentId;

	private Long contactAccountId;

	private String deptNo;

	private String apiKey;

	private String encryptedKey;

	private ActionType action;
	
	private String requestId;

}
