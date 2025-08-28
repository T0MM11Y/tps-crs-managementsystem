package com.twm.mgmt.model.report;

import java.io.Serializable;
import java.math.BigDecimal;
import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.model.common.PaginationVo;
import com.twm.mgmt.persistence.dto.CheckbillDto;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.RewardReportEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;
import java.math.BigDecimal;

import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.model.common.PaginationVo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuppressWarnings("serial")
public class CheckbillVo  extends PaginationVo {

	
	private String checkpaymethod;
	private String departmentId;
	private String ReceiveStartDate;
	private String ReceiveEndDate;
	private String CancelStartDate;
	private String CancelEndDate;

//	//收款日期
//	private String receivedate;
//	//銷帳日期
//	private String canceldate;
//	//筆數
//	private String quantity;
//	//mo幣金額
//	private String momoney;
	
	private ActionType action = ActionType.UNKNOWN;

}
