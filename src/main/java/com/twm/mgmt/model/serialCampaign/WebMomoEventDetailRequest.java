package com.twm.mgmt.model.serialCampaign;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class WebMomoEventDetailRequest {
    
    /** 活動代碼，固定 16 碼 */
    @NotBlank(message = "eventNo 為必填")
    @Size(min = 16, max = 16, message = "eventNo 長度須為 16 碼")
    private String eventNo;
    
    /** 單位代號，4 碼大寫英文字 */
    @NotBlank(message = "deptNo 為必填")
    @Size(min = 4, max = 4, message = "deptNo 長度須為 4 碼")
    @Pattern(regexp = "^[A-Z]{4}$", message = "deptNo 必須為 4 碼大寫英文字母")
    private String deptNo;
}
