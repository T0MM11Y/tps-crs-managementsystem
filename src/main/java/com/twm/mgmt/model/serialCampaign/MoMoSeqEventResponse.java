package com.twm.mgmt.model.serialCampaign;

import java.util.List;

import lombok.Data;

@Data
public class MoMoSeqEventResponse {
	/*--API回應時間--*/
    private String apiTime;  // yyyyMMddHHmmss 格式

    /*--總筆數--*/
    private int resultCount;  // 總筆數 = seqDenoCount加總

    /*--總金額--*/
    private int resultAmout;  // 總金額 = (seqDenoAmt * seqDenoCount) 加總

    /*--活動編號--*/
    private String eventNo;

    /*--活動明細清單--*/
    private List<MoMoSeqEventResObject> data;
}
