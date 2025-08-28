package com.twm.mgmt.persistence.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ExchangecurrencyDto {

	/**  */
	private String twmuid ;
	private String twmuid1 ;
	//會員UID
	private String momenberid;
	//類型
	private String status;
	//會員UID
	private String approvalType;
	//部門別
	private String departmentId;
	//發幣活動id
	private String campaignDetailID;
	//發幣活動名稱
	private String campaignName;
	//發幣活動說明
	private String campaignInfo;
	//查詢備註
	private String remard;
	//備註說明
	private String remardstatus;
	//兌幣品項
	private String exchangeitems;
	//是否黑名單
	private String isblock;
	//取得/使用mo幣
	private String getmo;
	//發送/兌換日期
	private String exchangemo;

}
