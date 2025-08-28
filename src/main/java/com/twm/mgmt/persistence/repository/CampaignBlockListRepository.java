package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.entity.CampaignBlockListEntity;

public interface CampaignBlockListRepository extends JpaRepository<CampaignBlockListEntity, BigDecimal> {

	List<CampaignBlockListEntity> findByCampaignDetailId(BigDecimal campaignDetailId);
	
	// <v20210621.M1.M_X> campaignDetailID 的 黑名單筆數
	@Query(value = "select COUNT(*) from CAMPAIGN_BLOCK_LIST where CAMPAIGN_DETAIL_ID = :campaignDetailId", nativeQuery = true)
	int currentBlocklistnum(@Param("campaignDetailId")BigDecimal campaignDetailId);
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "DELETE from CAMPAIGN_BLOCK_LIST where CAMPAIGN_DETAIL_ID = ?1", nativeQuery = true)
	int deleteBLOCK_LIST(BigDecimal campaignDetailId);
	
	// <v20210621.M1.M_X> campaignDetailID 的 黑名單筆數
	@Query(value = "select COUNT(*) from CAMPAIGN_BLOCK_LIST where CAMPAIGN_DETAIL_ID = :campaignDetailId and SUBID = :subid", nativeQuery = true)
	int ExchangeCurrencynum(@Param("campaignDetailId")BigDecimal campaignDetailId, @Param("subid")String subid);

}
