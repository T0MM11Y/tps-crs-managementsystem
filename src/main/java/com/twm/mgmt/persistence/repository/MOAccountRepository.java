package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.dao.MOAccountDao;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;

public interface MOAccountRepository extends JpaRepository<MOAccountEntity, String>, MOAccountDao {

	
	@Query(value = "select * from MO_ACCOUNT", nativeQuery = true)
	List<MOAccountEntity> findMOAccount();
	
	@Query("SELECT d.deptNo FROM MOAccountEntity d")
	List<String> findMOAccount1();
	
	@Query("SELECT mo.deptNo FROM MOAccountEntity mo LEFT JOIN MoAccountMapAccountEntity momap ON mo.deptNo = momap.moDeptNo WHERE momap.crsAccountId =:accountId AND momap.functionId=3 ")
	List<String> findMOCustomAccount(Long accountId);
	
	@Query("select d from MOAccountEntity d where d.deptNo = :moac")
	MOAccountEntity findBydepartmentId1(@Param("moac")String moac);
}
