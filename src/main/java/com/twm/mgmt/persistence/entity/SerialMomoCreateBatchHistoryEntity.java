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
@Table(name = "SERIAL_MOMO_CREATE_BATCH_HISTORY", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@SuppressWarnings("serial")
public class SerialMomoCreateBatchHistoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOMO_CREATE_HISTORY_ID", nullable = false)
    private Long momoCreateHistoryId;

    /** 關聯 MOMO_CREATE 操作的 ID */
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

    /** 記錄建立時間 */
    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;
    
    @Column(name = "TYPE")
    private String type;
}

