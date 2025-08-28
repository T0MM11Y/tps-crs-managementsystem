package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twm.mgmt.persistence.entity.SerialApprovalDetailEntity;

public interface SerialApprovalDetailRepository extends JpaRepository<SerialApprovalDetailEntity, BigDecimal> {
	List<SerialApprovalDetailEntity> findByApprovalBatchId(BigDecimal approvalBatchId);

	@Query(value = "SELECT sab.APPROVAL_BATCH_ID, sad.REPORT_ID, sab.L1_STATUS, sab.L2_STATUS, sad.CAMPAIGN_DETAIL_ID "
			+ "FROM MOMOAPI.SERIAL_APPROVAL_BATCH sab, " + "     MOMOAPI.SERIAL_APPROVAL_DETAIL sad, "
			+ "     SERIAL_REWARD_REPORT srr " + "WHERE sab.APPROVAL_TYPE = 'WHITELIST_REPORT' "
			+ "AND sab.STATUS = 'WAIT_APPROVAL' "
			+ "AND (sab.L1_STATUS = 'WAIT_APPROVAL' OR sab.L2_STATUS = 'WAIT_APPROVAL') "
			+ "AND sad.APPROVAL_BATCH_ID = sab.APPROVAL_BATCH_ID " + "AND srr.REPORT_ID = sad.REPORT_ID "
			+ "AND srr.REWARD_DATE <= sysdate", nativeQuery = true)
	List<Object[]> findApprovalDetailsForWhitelistReport();
}
