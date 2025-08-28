package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.dao.MomoidChangeListDao;
import com.twm.mgmt.persistence.entity.ContractInfoEntity;
import com.twm.mgmt.persistence.entity.MomoidChangeListEntity;


public interface ContractInfoRepository
		extends JpaRepository<ContractInfoEntity, BigDecimal>{

	@Query(value = "select * from ( select ci.contract_id from contract_info ci where ci.project_seq_nbr = :projectSeqNbr and ci.project_code = :projectCode and ci.subid = :subid order by ci.bt_create_date desc,ci.contract_id desc ) where  rownum <= 1", nativeQuery = true)
	BigDecimal getContractId(@Param("projectSeqNbr")String projectSeqNbr,@Param("projectCode")String projectCode,@Param("subid")String subid);

	@Query(value = "select ci.momo_member_id from contract_info ci where ci.project_seq_nbr = :projectSeqNbr and ci.project_code = :projectCode and ci.subid = :subid and ci.contract_id = :contractId order by ci.service_date desc,ci.bt_create_date desc", nativeQuery = true)
	String getMomoMemberId(@Param("projectSeqNbr")String projectSeqNbr,@Param("projectCode")String projectCode,@Param("subid")String subid,@Param("contractId")BigDecimal contractId);

	
	@Query(value = "select * from ( select ci.contract_id from contract_info ci where ci.project_seq_nbr = :projectSeqNbr and ci.project_code = :projectCode and ci.subid = :subid and  ci.momo_member_id =:momoMemberId  order by ci.bt_create_date desc,ci.contract_id desc ) where  rownum <= 1", nativeQuery = true)
	BigDecimal getContractId(@Param("projectSeqNbr")String projectSeqNbr,@Param("projectCode")String projectCode,@Param("subid")String subid,@Param("momoMemberId")String momoMemberId );
	
	
	

	

}
