package com.twm.mgmt.model.serialCampaign;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class WebMomoSeqEventRequest {
    /** 活動代碼 (16 碼) */
    @NotBlank(message = "eventNo 為必填")
    @Size(min = 16, max = 16, message = "eventNo 長度須為 16 碼")
    private String eventNo;

    /** 請求總筆數 (用來檢核 seqDenoCount 加總) */
    @NotNull(message = "requestCount 為必填")
    @Min(value = 1, message = "requestCount 最少要 1 筆")
    private Integer requestCount;

    /** 請求總金額 (用來檢核 seqDenoAmt*seqDenoCount 加總) */
    @NotNull(message = "requestAmout 為必填")
    @Min(value = 1, message = "requestAmout 最少要 1 元")
    private Integer requestAmout;

    /** 條件清單 */
    @NotEmpty(message = "conditions 列表不可為空")
    private List<@Valid WebMomoSeqEventReqObject> conditions;

    /** 單位代號 (4 碼大寫英文字母) */
    @NotBlank(message = "deptNo 為必填")
    @Pattern(regexp = "^[A-Z]{4}$", message = "deptNo 必須是 4 碼大寫英文字母")
    private String deptNo;

}
