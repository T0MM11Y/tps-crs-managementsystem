package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twm.mgmt.persistence.dao.SerialCampaignDetailDao;
import com.twm.mgmt.persistence.dao.SerialCampaignWhileListDao;
import com.twm.mgmt.persistence.entity.SerialCampaignDetailEntity;

public interface SerialCampaignDetailRepository extends JpaRepository<SerialCampaignDetailEntity, BigDecimal>,SerialCampaignDetailDao,SerialCampaignWhileListDao {

	@Query(value = "select SERIAL_CAMPAIGN_DETAIL_ID.nextval from dual", nativeQuery = true)
	BigDecimal getSerialCampaignDetailId();

	@Query(value = "select *  from SERIAL_CAMPAIGN_DETAIL where status in ('INCOMPLETE','WAIT_SIGN_FOR')", nativeQuery = true)
	List<SerialCampaignDetailEntity> findByStatus();
}
