package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.entity.APKeyIvEntity;

public interface APKeyIvRepository extends JpaRepository<APKeyIvEntity, Long> {

	@Query("SELECT aki FROM APKeyIvEntity aki WHERE ROWNUM <= 2 AND aki.sourceId = :sourceId AND aki.expiredDate > SYSDATE ORDER BY aki.expiredDate")
	List<APKeyIvEntity> find2RowBySourceId(@Param("sourceId")String sourceId);

}
