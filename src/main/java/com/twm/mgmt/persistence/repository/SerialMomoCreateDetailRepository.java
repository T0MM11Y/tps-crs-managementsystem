package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twm.mgmt.persistence.entity.SerialMomoCreateDetailEntity;


public interface SerialMomoCreateDetailRepository
		extends JpaRepository<SerialMomoCreateDetailEntity, Long>{

	List<SerialMomoCreateDetailEntity> findByEventNoAndEventDtId(String eventno, Long eventdtid);

}
