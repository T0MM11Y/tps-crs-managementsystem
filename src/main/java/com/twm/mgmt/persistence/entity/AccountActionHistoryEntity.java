package com.twm.mgmt.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.twm.mgmt.config.MoDbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "ACCOUNT_ACTION_HISTORY", schema = MoDbConfig.CAMPAIGN_SCHEMA)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class AccountActionHistoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_action_history_seq")
    @SequenceGenerator(name = "account_action_history_seq", sequenceName = "ACCOUNT_ACTION_HISTORY_SEQ", allocationSize = 1, schema = MoDbConfig.CAMPAIGN_SCHEMA)
    @Column(name = "ACCOUNT_ACTION_HISTORY_ID")
    private Long accountActionHistoryId;

    @Lob
    @Column(name = "EXECUTE_CONTENT")
    private String executeContent;

    @Column(name = "REQUEST_ID", length = 100)
    private String requestId;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "EXECUTE_ACCOUNT_ID")
    private Long executeAccountId;

    @Column(name = "EXECUTE_DATE")
    private Date executeDate;

}