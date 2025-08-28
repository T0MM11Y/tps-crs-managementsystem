package com.twm.mgmt.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.twm.mgmt.persistence.entity.SerialCampaignBlockMain;

@Transactional
@Repository
public interface SerialCampaignBlockMainRepository extends JpaRepository<SerialCampaignBlockMain, Integer>{

	@Query(value = "SELECT scbm.BLOCK_ID "
			+ "FROM MOMOAPI.SERIAL_CAMPAIGN_BLOCK_MAIN scbm, MOMOAPI.CAMPAIGN_MAIN cm "
			+ "WHERE scbm.IS_ACTIVE = 1 AND cm.CAMPAIGN_KIND = :campaignKind "
			+ "AND scbm.CREATE_ACCOUNT = :accountId "
			+ "AND scbm.CAMPAIGN_MAIN_ID = cm.CAMPAIGN_MAIN_ID ",
			nativeQuery = true)
	public Integer getActiveSerialBlockIdByKindId(@Param("campaignKind") int campaignKind, @Param("accountId") Long accountId);
}
