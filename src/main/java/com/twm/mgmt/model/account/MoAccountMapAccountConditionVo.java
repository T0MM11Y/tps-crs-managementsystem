package com.twm.mgmt.model.account;

import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.model.common.PaginationVo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuppressWarnings("serial")
public class MoAccountMapAccountConditionVo extends PaginationVo {

	private ActionType action = ActionType.UNKNOWN;
}
