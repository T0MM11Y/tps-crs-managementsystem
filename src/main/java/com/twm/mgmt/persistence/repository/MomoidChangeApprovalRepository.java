package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twm.mgmt.persistence.entity.MomoidChangeApprovalEntity;


public interface MomoidChangeApprovalRepository
		extends JpaRepository<MomoidChangeApprovalEntity, BigDecimal>{


	MomoidChangeApprovalEntity findByMomoidChangeMainId(BigDecimal momoidChangeMainId);
	

}
