package com.twm.mgmt.persistence.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class AccountActionHistoryDto {


    private String requestId;
    private String executeContent;
    private BigDecimal accountIdA;
    private String userNameA;
    private BigDecimal executeAccountId;
    private String executeUserName;
    private String executeDate;

    
    public AccountActionHistoryDto(String requestId, String executeContent, BigDecimal accountIdA, String userNameA, BigDecimal executeAccountId, String executeUserName, String executeDate) {
        this.requestId = requestId;
        this.executeContent = executeContent;
        this.accountIdA = accountIdA;
        this.userNameA = userNameA;
        this.executeAccountId = executeAccountId;
        this.executeUserName = executeUserName;
        this.executeDate = executeDate;
    }

}
