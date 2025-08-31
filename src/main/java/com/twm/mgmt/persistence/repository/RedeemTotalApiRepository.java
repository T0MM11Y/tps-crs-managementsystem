package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twm.mgmt.persistence.dao.RedeemTotalApiDao;
import com.twm.mgmt.persistence.entity.RedeemTotalApiEntity;

public interface RedeemTotalApiRepository
		extends JpaRepository<RedeemTotalApiEntity, RedeemTotalApiEntity>, RedeemTotalApiDao {
	@Query(value = "select * from REDEEM_TOTAL_API", nativeQuery = true)
	List<RedeemTotalApiEntity> findRedeemTotalApi();
}
