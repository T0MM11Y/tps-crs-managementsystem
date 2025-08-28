package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twm.mgmt.persistence.dao.MoAccountMapAccountDao;
import com.twm.mgmt.persistence.entity.MoAccountMapAccountEntity;

public interface MoAccountMapAccountRepository extends JpaRepository<MoAccountMapAccountEntity, String>, MoAccountMapAccountDao {
	@Query(value = "select * from MOACCOUNT_MAP_ACCOUNT", nativeQuery = true)
	List<MoAccountMapAccountEntity> findMoAccountMapAccount();
}
