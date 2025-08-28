package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.twm.mgmt.persistence.entity.AccountPermissionProgramEntity;

public interface AccountPermissionProgramRepository extends JpaRepository<AccountPermissionProgramEntity, Long> {

	List<AccountPermissionProgramEntity> findByAccountId(Long accountId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM AccountPermissionProgramEntity a WHERE a.accountId = ?1")
	int deleteByAccountId(Long id);

}
