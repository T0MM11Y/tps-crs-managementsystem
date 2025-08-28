package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.entity.SerialRewardReportDetailEntity;
;


public interface SerialRewardReportDetailRepository extends JpaRepository<SerialRewardReportDetailEntity, BigDecimal> {

	/**
	 * 發幣用戶總數
	 * @param reportId
	 * @return
	 */
	@Query(value = "Select COALESCE(count(rrd.report_detail_id),0) From SERIAL_REWARD_REPORT_DETAIL rrd Where rrd.report_id = :reportId  and rrd.status not like 'FAIL_%' and rrd.status not like '%_ERROR'  and (BASIC_AMOUNT != 0 or ACCT_TYPE_AMOUNT != 0  or BC_ID_AMOUNT != 0)", nativeQuery = true)
	int countreportId(@Param("reportId")BigDecimal reportId);
	
	/**
	 * 發幣總額
	 * @param reportId
	 * @return
	 */
	@Query(value = "Select COALESCE((sum(rrd.BASIC_AMOUNT) + sum(rrd.ACCT_TYPE_AMOUNT) + sum(rrd.BC_ID_AMOUNT)),0) From SERIAL_REWARD_REPORT_DETAIL rrd Where rrd.report_id = :reportId and rrd.status not like 'FAIL_%' and rrd.status not like '%_ERROR'   and (BASIC_AMOUNT != 0 or ACCT_TYPE_AMOUNT != 0 or BC_ID_AMOUNT != 0)", nativeQuery = true)
	int sumBasicAmountAddSumAccTypeAmountAddSumBcIdAmount(@Param("reportId")BigDecimal reportId);

	List<SerialRewardReportDetailEntity> findByReportIdAndCampaignDetailId(BigDecimal reportId,
			BigDecimal campaignDetailId);

	void deleteByReportIdAndCampaignDetailId(BigDecimal reportId, BigDecimal campaignDetailId);
}
