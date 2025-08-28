package com.twm.mgmt.model.account;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class APKeyIvVo implements Serializable {

	private Long keyIvId;

	private String key;

	private String iv;

	private String expiredDate;

}
