package com.twm.mgmt.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.dao.APAccountDao;
import com.twm.mgmt.persistence.entity.APAccountEntity;
import com.twm.mgmt.persistence.entity.pk.APAccountPk;

public interface APAccountRepository extends JpaRepository<APAccountEntity, APAccountPk>, APAccountDao {

	@Query("SELECT COUNT(aa.sourceId) FROM APAccountEntity aa WHERE UPPER(aa.sourceId) = :sourceId")
	int countBySourceId(@Param("sourceId")String sourceId);

}
