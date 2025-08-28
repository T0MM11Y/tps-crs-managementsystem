package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.dao.MomoidChangeListDao;
import com.twm.mgmt.persistence.entity.MomoidChangeListEntity;


public interface MomoidChangeListRepository
		extends JpaRepository<MomoidChangeListEntity, BigDecimal> ,MomoidChangeListDao{

	@Query(value = "select * from momoid_change_list mcl,momoid_change_sms mcs where mcl.momoid_change_sms_id = mcs.momoid_change_sms_id and mcl.momoid_change_main_id = :momoidChangeMainId  order by mcl.momoid_change_list_id asc", nativeQuery = true)
	List<MomoidChangeListEntity> findByMomoidChangeMainId(@Param("momoidChangeMainId")BigDecimal momoidChangeMainId);

	@Query(value = "select * from momoid_change_list where warning_id = :warningId", nativeQuery = true)
	MomoidChangeListEntity findByWarningId(@Param("warningId")BigDecimal warningId);
	
	@Query(value = "select MOMOID_CHANGE_LIST_SEQ.nextval from dual", nativeQuery = true)
	BigDecimal getMomoidChangeListId();

	

}
