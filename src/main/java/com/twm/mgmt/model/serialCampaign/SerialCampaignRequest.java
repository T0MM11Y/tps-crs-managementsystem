package com.twm.mgmt.model.serialCampaign;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SerialCampaignRequest {
    private String eventNo;
    private Integer requestCount;
    private BigDecimal requestAmount;
    private String payAccount;
    private String orderNumber;
    private BigDecimal eventNoAmount;
    private BigDecimal remainingAmt;
    private String amountValidityStartDate;
    private String amountValidityEndDate;
    private String accountId;
    private LocalDate rewardDate;
    private List<ReportDetail> reportIdDetail;
    private List<Condition> conditions;
    
    
    @Data
    public static class Condition {
        private BigDecimal seqDenoAmt;
        private Integer seqDenoCount;
    }
}
