package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twm.mgmt.persistence.entity.WarningContractEntity;

public interface WarningContractRepository
		extends JpaRepository<WarningContractEntity, BigDecimal> {



	

}
