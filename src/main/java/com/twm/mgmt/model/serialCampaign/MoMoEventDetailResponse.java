package com.twm.mgmt.model.serialCampaign;


import lombok.Data;

@Data
public class MoMoEventDetailResponse {

    /*-- API 回應時間 --*/
    private String apiTime;

    /*-- 活動代碼 --*/
    private String eventNo;

    /*-- 活動名稱 --*/
    private String eventName;

    /*-- 發幣金額 --*/
    private int eventAmt;

    /*-- 發幣餘額 --*/
    private int remainingAmt;

    /*-- 效期起日 --*/
    private String startDate;

    /*-- 效期訖日 --*/
    private String endDate;

    /*-- 備註 --*/
    private String eventNote;

    /*-- 覆核狀態 --*/
    private String auditStatus;

    /*-- 公司編號 --*/
    private String companyId;

    /*-- 公司名稱 --*/
    private String companyName;

    /*-- 廠商代碼 --*/
    private String merchantId;

    /*-- 戶頭代號 --*/
    private String budgetNo;

    /*-- 單位代號 --*/
    private String departmentNo;

    /*-- 單位名稱 --*/
    private String departmentName;

    /*-- 建立時間 --*/
    private String insertTime;

    /*-- 建立者Id --*/
    private String insertId;

    /*-- 異動時間 --*/
    private String modifyTime;

    /*-- 異動者Id --*/
    private String modifyId;
}

