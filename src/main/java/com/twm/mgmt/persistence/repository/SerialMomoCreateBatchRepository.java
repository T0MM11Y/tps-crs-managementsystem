package com.twm.mgmt.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twm.mgmt.persistence.entity.SerialMomoCreateBatchEntity;


public interface SerialMomoCreateBatchRepository
		extends JpaRepository<SerialMomoCreateBatchEntity, Long>{

	SerialMomoCreateBatchEntity findByEventNoAndEventDtId(String eventno, Long eventdtid);

}
