package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.dao.MomoidChangeMainDao;
import com.twm.mgmt.persistence.dto.MomoidChangeMainDto;
import com.twm.mgmt.persistence.entity.MomoidChangeMainEntity;

public interface MomoidChangeMainRepository
		extends JpaRepository<MomoidChangeMainEntity, BigDecimal>,MomoidChangeMainDao{

	@Query(value = "select NVL(max(substr(momoid_change_main_id,9,3)),'000') momoid_change_main_id from momoid_change_main where substr(momoid_change_main_id,1,8)=:dateStr", nativeQuery = true)
	String getMaxMomoidChangeMainIdByDateStr(@Param("dateStr")String dateStr);

	@Query(value = "select  mcm.momoid_change_main_id,mca.momoid_change_approval_id,act.user_name,dep.department_name from momoid_change_main mcm, momoid_change_approval mca, account act,department dep where mcm.account_id = act.account_id and act.department_id=dep.department_id and mcm.momoid_change_main_id = mca.momoid_change_main_id and  mcm.momoid_change_main_id =:id", nativeQuery = true)
	Map  getUserNameAndDepartmentName(@Param("id")BigDecimal id);

}
