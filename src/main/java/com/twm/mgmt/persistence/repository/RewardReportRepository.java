package com.twm.mgmt.persistence.repository;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.dao.RewardReportDao;

import com.twm.mgmt.persistence.entity.RewardReportEntity;

public interface RewardReportRepository extends JpaRepository<RewardReportEntity, BigDecimal>, RewardReportDao {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE RewardReportEntity rr SET rr.status = :status, rr.updateDate = SYSDATE WHERE rr.reportId = :reportId AND rr.status = :oriStatus")
	int updateStatusByReportId(@Param("reportId")BigDecimal reportId, @Param("status")String status, @Param("oriStatus")String oriStatus);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE RewardReportEntity rr SET rr.status = :status, rr.updateDate = SYSDATE , rr.amountValidityDate = :amountValidityDate, rr.eventInfoDoneDate = SYSDATE  WHERE rr.reportId = :reportId")
	int updateStatusByReportId(@Param("reportId")BigDecimal reportId, @Param("status")String status, @Param("amountValidityDate")Date amountValidityDate);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE RewardReportEntity rr SET rr.status = :status, rr.updateDate = SYSDATE , rr.amountValidityDate = :amountValidityDate, rr.momoEventNo = :momoEventNo, rr.payAccount = :payAccount, rr.orderNumber = :orderNumber, rr.amountValidityStartDate = :amountValidityStartDate, rr.requisitionUnit = :requisitionUnit, rr.requisitioner = :requisitioner ,rr.eventInfoDoneDate = SYSDATE  WHERE rr.reportId = :reportId")
	int updateStatusByReportId(@Param("reportId")BigDecimal reportId, @Param("status")String status, @Param("amountValidityDate")Date amountValidityDate, @Param("momoEventNo")String momoEventNo,
			@Param("payAccount")String payAccount, @Param("orderNumber")String orderNumber, @Param("amountValidityStartDate")Date amountValidityStartDate, @Param("requisitionUnit")String requisitionUnit,
			@Param("requisitioner")String requisitioner);

	@Query("SELECT aki FROM RewardReportEntity aki WHERE aki.approvalMainId = :approvalMainId")
	List<RewardReportEntity> findByCampaignDetailId(@Param("approvalMainId")String approvalMainId);

	@Query("SELECT aki FROM RewardReportEntity aki WHERE aki.orderNumber = :approvalMainId")
	RewardReportEntity findByOrderNumber(@Param("approvalMainId")String approvalMainId);

	@Query("SELECT aki FROM RewardReportEntity aki WHERE aki.approvalMainId = :approvalMainId")
	RewardReportEntity findByapprovalMainId(@Param("approvalMainId")String approvalMainId);

	@Query("SELECT aki FROM RewardReportEntity aki WHERE aki.payAccount != null")
	List<RewardReportEntity> findEMpayaccount();

	@Query("SELECT aki FROM RewardReportEntity aki WHERE aki.orderNumber != null")
	List<RewardReportEntity> findEMmomoevent();

	@Query("SELECT aki FROM RewardReportEntity aki WHERE aki.orderNumber = :momoEventNo")
	List<RewardReportEntity> findBymomoEventNo(@Param("momoEventNo")String momoEventNo);

//	List<RewardReportEntity> findByCampaignDetailId(BigDecimal campaignDetailId);

	@Query(value = "select distinct( ac.ROLE_ID ) from CAMPAIGN_DETAIL cd, ACCOUNT ac, REWARD_REPORT rr where rr.CAMPAIGN_DETAIL_ID = cd.CAMPAIGN_DETAIL_ID and cd.CREATE_ACCOUNT = ac.ACCOUNT_ID and rr.ORDER_NUMBER= :ponum", nativeQuery = true)
	int getCampaignofaccountofRoleid(@Param("ponum")String ponum);

	@Query(value = "select sum(rrd.BASIC_AMOUNT+ rrd.FLOAT_AMOUNT+ rrd.PLUS_AMOUNT) from  REWARD_REPORT_DETAIL rrd where rrd.REPORT_ID = :id", nativeQuery = true)
	int getpayaccount_rr_totalmoney(@Param("id")BigDecimal id);

//	@Query(value = "select sum(rrd.BASIC_AMOUNT+ rrd.FLOAT_AMOUNT+ rrd.PLUS_AMOUNT) from  REWARD_REPORT_DETAIL rrd where rrd.REPORT_ID = ?1 and to_char(rrd.CREATE_DATE,'mm')=to_char(sysdate,'mm')", nativeQuery = true)

	@Query(value = "select COALESCE(sum(rrd.BASIC_AMOUNT+ rrd.FLOAT_AMOUNT+ rrd.PLUS_AMOUNT),0) from  REWARD_REPORT_DETAIL rrd where rrd.REPORT_ID = :id and to_char(rrd.CREATE_DATE,'mm')=to_char(sysdate,'mm')", nativeQuery = true)
	int getpayaccount_rr_totalmoney_this_month(@Param("id")BigDecimal id);

	@Query("SELECT distinct aki.requisitionUnit FROM RewardReportEntity aki where  aki.requisitionUnit  is not null")
	List<String> findREQUISITION_UNIT();

}
