package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SERIAL_MOMO_CREATE_BATCH", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@SuppressWarnings("serial")
public class SerialMomoCreateBatchEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOMO_CREATE_ID", nullable = false)
    private Long momoCreateId;

    /** 活動代碼 */
    @Column(name = "EVENTNO", nullable = false, length = 100)
    private String eventNo;

    /** 活動明細編號 */
    @Column(name = "EVENTDTID", nullable = false)
    private Long eventDtId;

    @Column(name = "REPORTIDS", columnDefinition = "CLOB", nullable = false)
    private String reportIds;

    @Column(name = "STATUS", nullable = false, length = 100)
    private String status;

    /** 戶頭代碼 */
    @Column(name = "PAY_ACCOUNT", length = 20)
    private String payAccount;

    /** PO單號 */
    @Column(name = "ORDER_NUMBER", length = 10)
    private String orderNumber;

    /** 發幣金額 */
    @Column(name = "EVENTNOAMT", nullable = false)
    private Long eventNoAmt;

    /** 發幣餘額 */
    @Column(name = "REMAININGAMT", nullable = false)
    private Long remainingAmt;

    /** 總金額 */
    @Column(name = "REQUESTAMOUNT", nullable = false)
    private Long requestAmount;

    /** 總筆數 */
    @Column(name = "REQUESTCOUNT", nullable = false)
    private Long requestCount;

    /** 金額1:筆數1,金額2:筆數2 */
    @Column(name = "AMOUNTCOUNTMAP", columnDefinition = "CLOB", nullable = false)
    private String amountCountMap;

    /** 效期起日 */
    @Column(name = "AMOUNT_VALIDITY_START_DATE")
    private LocalDateTime amountValidityStartDate;

    /** 效期訖日 */
    @Column(name = "AMOUNT_VALIDITY_END_DATE")
    private LocalDateTime amountValidityEndDate;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    /** 建立PM */
    @Column(name = "CREATE_ACCOUNT")
    private Long createAccount;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    /** momo回饋序號建立時間 */
    @Column(name = "INSERTTIME")
    private LocalDateTime insertTime;
    
    @Column(name = "TYPE")
    private String type;
}

