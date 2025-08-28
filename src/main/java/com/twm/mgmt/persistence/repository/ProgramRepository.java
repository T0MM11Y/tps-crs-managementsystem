package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.entity.ProgramEntity;

public interface ProgramRepository extends JpaRepository<ProgramEntity, Long> {

	@Query("SELECT p FROM ProgramEntity p WHERE p.programId IN (:programIds) ORDER BY p.orderNo")
	List<ProgramEntity> findPrograms(@Param("programIds")List<Long> programIds);
	
	@Query(value = "select * from program p where instr(:programUri,p.program_uri)>0", nativeQuery = true)
	ProgramEntity findByProgramUri(@Param("programUri")String programUri);

}
