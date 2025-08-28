package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twm.mgmt.persistence.entity.SerialTransactionOfferHistoryEntity;

public interface SerialTransactionOfferHistoryRepository extends JpaRepository<SerialTransactionOfferHistoryEntity, BigDecimal> {

	List<SerialTransactionOfferHistoryEntity> findByReportDetailId(BigDecimal reportDetailId);

	void deleteByReportDetailId(BigDecimal reportDetailId);

    

}