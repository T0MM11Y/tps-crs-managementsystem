package com.twm.mgmt.model.serialCampaign;

import lombok.Data;

@Data
public class MoMoSeqEventResObject {
    /*--活動編號--*/
    private String eventNo;

    /*--活動明細編號--*/
    private Integer eventDtId;

    /*--序號金額--*/
    private Integer seqDenoAmt;

    /*--序號筆數--*/
    private Integer seqDenoCount;

    /*--序號是否已經產生--*/
    private String seqDenoStatus;  // 0:未產生 1:已產生

    /*--覆核狀態--*/
    private String auditStatus;  // 0:待覆核 1:同意 2:不同意
}

