package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;



import org.springframework.data.jpa.repository.JpaRepository;


import com.twm.mgmt.persistence.entity.SerialCampaignFileEntity;




public interface SerialCampaignFileRepository extends JpaRepository<SerialCampaignFileEntity, BigDecimal> {


}
