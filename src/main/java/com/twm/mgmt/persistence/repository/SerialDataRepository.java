package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import com.twm.mgmt.persistence.entity.SerialDataEntity;

public interface SerialDataRepository extends JpaRepository<SerialDataEntity, BigDecimal> {

	void deleteBySerialRewardReportIdAndSerialRewardReportDetailIdAndSerialTransactionOfferId(BigDecimal reportId,
			BigDecimal reportDetailId, BigDecimal transactionOfferId);

    

}