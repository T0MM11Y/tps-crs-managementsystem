package com.twm.mgmt.persistence.dao;

import java.util.List;

import com.twm.mgmt.model.report.CheckbillVo;
import com.twm.mgmt.persistence.dto.CheckbillDto;

public interface CheckbillDao {

	List<CheckbillDto> findByCondition(CheckbillVo condition,List<String> momoDepartmentStr_List);
	
	String findCheckbillForBOMD(CheckbillVo condition,List<String> momoDeptNoList);

	Integer countByCondition(CheckbillVo condition,List<String> momoDepartmentStr_List);
	
	List<CheckbillDto> findDetailByCondition(CheckbillVo condition,List<String> momoDeptNoList, String queryType);
	
}
