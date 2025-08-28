package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.twm.mgmt.persistence.entity.RolePermissionProgramEntity;

public interface RolePermissionProgramRepository extends JpaRepository<RolePermissionProgramEntity, Long> {

	List<RolePermissionProgramEntity> findByRoleId(Long roleId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM RolePermissionProgramEntity WHERE roleId = ?1")
	public int deleteByRoleId(Long id);

}
