package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.entity.MomoidChangePictureEntity;


public interface MomoidChangePictureRepository
		extends JpaRepository<MomoidChangePictureEntity, BigDecimal>{

	//@Query(value="SELECT PICTURE1_NAME,PICTURE2_NAME,PICTURE3_NAME,PICTURE4_NAME,PICTURE5_NAME,PICTURE6_NAME,PICTURE7_NAME,PICTURE8_NAME,PICTURE9_NAME,PICTURE10_NAME,PICTURE11_NAME,PICTURE12_NAME,PICTURE13_NAME,PICTURE14_NAME,PICTURE15_NAME FROM MOMOID_CHANGE_PICTURE WHERE MOMOID_CHANGE_MAIN_ID = :momoidChangeMainId",nativeQuery=true)
	//MomoidChangePictureEntity findById(@Param("momoidChangeMainId")BigDecimal momoidChangeMainId);
	
	@Query(value="SELECT PICTURE1_NAME,PICTURE2_NAME,PICTURE3_NAME,PICTURE4_NAME,PICTURE5_NAME,PICTURE6_NAME,PICTURE7_NAME,PICTURE8_NAME,PICTURE9_NAME,PICTURE10_NAME,PICTURE11_NAME,PICTURE12_NAME,PICTURE13_NAME,PICTURE14_NAME,PICTURE15_NAME FROM MOMOID_CHANGE_PICTURE WHERE MOMOID_CHANGE_MAIN_ID = :momoidChangeMainId",nativeQuery=true)
	Map findByMomoidChangeMainId(@Param("momoidChangeMainId")BigDecimal momoidChangeMainId);


}
