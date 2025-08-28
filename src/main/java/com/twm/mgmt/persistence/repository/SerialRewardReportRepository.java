package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.entity.SerialRewardReportEntity;

public interface SerialRewardReportRepository extends JpaRepository<SerialRewardReportEntity, BigDecimal> {

	@Query(value = "SELECT " + "  srr.REPORT_ID, " + "  scd.PROJECT_TYPE, " + "  scd.CAMPAIGN_NAME, "
			+ "  (srrd.BASIC_AMOUNT + srrd.ACCT_TYPE_AMOUNT + srrd.BC_ID_AMOUNT) AS 面額, "
			+ "  COUNT(srrd.REPORT_DETAIL_ID) AS 待發幣人數, "
			+ "  (srrd.BASIC_AMOUNT + srrd.ACCT_TYPE_AMOUNT + srrd.BC_ID_AMOUNT) * COUNT(srrd.REPORT_DETAIL_ID) AS 總額 "
			+ "FROM MOMOAPI.SERIAL_REWARD_REPORT srr " + "LEFT JOIN MOMOAPI.SERIAL_CAMPAIGN_DETAIL scd "
			+ "  ON srr.CAMPAIGN_DETAIL_ID = scd.CAMPAIGN_DETAIL_ID "
			+ "LEFT JOIN MOMOAPI.SERIAL_REWARD_REPORT_DETAIL srrd " + "  ON srr.REPORT_ID = srrd.REPORT_ID "
			+ "  AND srrd.STATUS = 'WAIT_MOMO_EVENT_NO' " + "WHERE srr.STATUS = 'WAIT_MOMO_EVENT_NO' "
			+ "  AND scd.CREATE_ACCOUNT = :accountId " + "  AND srr.CREATE_DATE >= TRUNC(SYSDATE, 'MM') "
			+ "  AND srr.CREATE_DATE <= TO_DATE(TO_CHAR(LAST_DAY(SYSDATE), 'YYYY-MM-DD') || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS') "
			+ "GROUP BY " + "  srr.REPORT_ID, " + "  scd.PROJECT_TYPE, " + "  scd.CAMPAIGN_NAME, "
			+ "  (srrd.BASIC_AMOUNT + srrd.ACCT_TYPE_AMOUNT + srrd.BC_ID_AMOUNT) "
			+ "ORDER BY srr.REPORT_ID", nativeQuery = true)
	List<Object[]> findRewardSummaryByAccountId(@Param("accountId") Long accountId);

	@Query(value = "SELECT srr.report_id, srr.reward_date, srr.amount_validity_start_date, srr.amount_validity_end_date "
			+ "FROM momoapi.serial_reward_report srr "
			+ "LEFT JOIN momoapi.serial_campaign_detail scd ON scd.campaign_detail_id = srr.campaign_detail_id "
			+ "WHERE srr.status = 'DONE_REWARD' AND srr.total_reward_users <> 0 AND scd.create_account = :account "
			+ "AND (:reportId IS NULL OR srr.report_id = :reportId) "
			+ "AND (:rewardDate IS NULL OR TO_CHAR(srr.reward_date, 'YYYY-MM') = TO_CHAR(:rewardDate, 'YYYY-MM')) "
			+ "ORDER BY srr.report_id DESC", nativeQuery = true)
	List<Map<String, Object>> findReportsNative(@Param("account") Long account, @Param("reportId") BigDecimal reportId,
			@Param("rewardDate") Date rewardDate);

	@Modifying
	@Query("UPDATE SerialRewardReportEntity srr SET srr.amountValidityStartDate = :startDate, srr.amountValidityEndDate = :endDate WHERE srr.reportId IN :reportIds")
	void updateAmountValidityDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
			@Param("reportIds") List<BigDecimal> reportIds);

	@Query("SELECT s.rewardDate FROM SerialRewardReportEntity s WHERE s.reportId = :reportId")
	Date findRewardDateByReportId(@Param("reportId") BigDecimal reportId);

}
