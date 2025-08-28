package com.twm.mgmt.persistence.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class CheckbillDto {

	
	private String checkpaymethod;
	private String departmentId;
	private String ReceiveStartDate;
	private String ReceiveEndDate;
	private String CancelStartDate;
	private String CancelEndDate;

	private String receivedate;
	private String canceldate;
	private String quantity;
	private String momoney;
	
	//下面這些都是為了明細報表
	private String COMPANY_NUMBER;
	private String COMPANY;
	private String INVOICE_NUMBER;
	private String DEPT_NO;
	private String DEPT_NAME;
	private String CHANNEL_NAME;
	private String STORE_ID;
	private String STORE_NAME;
	private String ORDER_NOTE;
	private String MO_CHARGE_ID;
	private String O_ORDER_NUMBER;
	private String T_ORDER_NUMBER;
	private String MOMO_UUID;
	private String TWM_UID;
	private String TWM_UUID;
	private String ORDER_DATE;
	private String MO_TX_TIME;
	private String AMOUNT;
	private String MO_REFUND_TX_TIME;
	private String CANCEL_AMOUNT;
}



