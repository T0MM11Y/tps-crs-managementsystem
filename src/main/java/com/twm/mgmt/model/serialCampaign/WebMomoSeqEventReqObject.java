package com.twm.mgmt.model.serialCampaign;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class WebMomoSeqEventReqObject {
    @NotNull(message = "seqDenoAmt 為必填")
    @Min(value = 1, message = "seqDenoAmt 最少要 1 元")
    private Integer seqDenoAmt;

    @NotNull(message = "seqDenoCount 為必填")
    @Min(value = 1, message = "seqDenoCount 最少要 1 筆")
    private Integer seqDenoCount;
}
