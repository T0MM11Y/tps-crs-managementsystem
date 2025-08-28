package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.entity.CampaignMainEntity;

public interface CampaignMainRepository extends JpaRepository<CampaignMainEntity, BigDecimal> {
	CampaignMainEntity findByCampaignKind(BigDecimal campaignKind);
	CampaignMainEntity findBycampaignMainId(BigDecimal campaignMainId);
	CampaignMainEntity findByCampaignMainName(String campaignMainName);
	
	@Query("SELECT p FROM CampaignMainEntity p ORDER BY p.campaignKind ASC")
	List<CampaignMainEntity> getAllData();
	
	
	@Query(value = "Select campaign_main_id, campaign_main_name,campaign_kind From campaign_main Where campaign_kind != 8", nativeQuery = true)
	List  getCampaignMainIdAndCampaignMainName();
	
	@Query(value = "SELECT CAMPAIGN_MAIN_ID FROM MOMOAPI.CAMPAIGN_MAIN WHERE CAMPAIGN_KIND = :CAMPAIGN_KIND",
			nativeQuery = true)
	public int getCampaignMainId(@Param("CAMPAIGN_KIND")int CAMPAIGN_KIND);
}
