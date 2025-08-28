package com.twm.mgmt.persistence.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ElectronicmoneyDto {


	//po單號
	private String momoeventId;
	//戶頭代碼
	private String payaccountId;
	//請購單位
	private String departmentId;
	//下單開始
	private String sendStartDate;
	//下單結束
	private String sendEndDate;

	
	//訂單日期
	private String orderdate;
	//po單號
	private String ponum;
	//請購單位
	private String prRequestorDept;
	//請購人
	private String prRequestorName;	
	//訂單數量
	private String poQty;		
	//驗收金額
	private String rcvAmountWithTax;		
	//訂單金額
	private String poAmountWithTax;
	//戶頭代碼
	private String payaccount;	
	//mo幣效期
	private String amountValidityPeriod;

	//下訂單日期範圍 起
	private String sendStartDate1;
	//下訂單日期範圍 訖
	private String sendEndDate1;

	//該戶頭可發送總額
	private String itemA;
	//該戶頭當月累計發送金額
	private String itemB;
	//該戶頭累計發送金額
	private String itemC;
	//該戶頭剩餘可發送金額
	private String itemD;
	
/*
	//po單號
	private String momoeventId;
	//戶頭代碼
	private String payaccountId;
	//請購單位
	private String departmentId;
	//下單開始
	private String sendStartDate;
	//下單結束
	private String sendEndDate;
*/	
	/*
		//po單號
		private String momoEventNo;
		//戶頭代碼
		private String payAccount;
		//請購單位
		private String requisitionUnit;
		//下單開始
		private String sendStartDate;
		//下單結束
		private String sendEndDate;
	*/
}



