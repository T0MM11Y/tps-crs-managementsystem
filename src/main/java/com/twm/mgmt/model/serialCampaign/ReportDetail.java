package com.twm.mgmt.model.serialCampaign;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReportDetail {
    private String reportId;
    private String projectType;
    private String campaignName;
    private BigDecimal totalAmount;
    private BigDecimal amount;
    private Integer pendingUser;
}

