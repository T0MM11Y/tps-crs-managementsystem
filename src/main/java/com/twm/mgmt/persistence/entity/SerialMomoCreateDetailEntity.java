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
@Table(name = "SERIAL_MOMO_CREATE_DETAIL", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@SuppressWarnings("serial")
public class SerialMomoCreateDetailEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOMO_CREATE_DETAIL_ID", nullable = false)
    private Long momoCreateDetailId;

    /** 活動代碼 */
    @Column(name = "EVENTNO", nullable = false, length = 100)
    private String eventNo;

    /** 活動明細編號 */
    @Column(name = "EVENTDTID", nullable = false)
    private Long eventDtId;

    /** 回報序號 ID */
    @Column(name = "REPORT_ID")
    private Long reportId;

    /** 專案代碼 */
    @Column(name = "PROJECT_TYPE", length = 100)
    private String projectType;

    /** 活動名稱 */
    @Column(name = "CAMPAIGN_NAME", nullable = false, length = 100)
    private String campaignName;

    /** 總金額 */
    @Column(name = "TOTAL_AMOUNT")
    private Long totalAmount;

    /** 面額 */
    @Column(name = "AMOUNT")
    private Long amount;

    /** 待發幣人數 */
    @Column(name = "PENDINGUSER")
    private Long pendingUser;

    /** 記錄建立時間 */
    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    /** 建立人員編號 */
    @Column(name = "CREATE_ACCOUNT")
    private Long createAccount;
    
    @Column(name = "TYPE")
    private String type;
}
