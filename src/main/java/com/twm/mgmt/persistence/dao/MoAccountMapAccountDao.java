package com.twm.mgmt.persistence.dao;

import java.util.List;

import com.twm.mgmt.model.account.MoAccountMapAccountConditionVo;
import com.twm.mgmt.persistence.dto.MoAccountMapAccountDto;

public interface MoAccountMapAccountDao{
	
	List<MoAccountMapAccountDto> findByCondition(MoAccountMapAccountConditionVo condition);
	Integer countByCondition(MoAccountMapAccountConditionVo condition);
	void deleteMoAccountMapAccount(String functionId, String moDeptNo, String crsAccountId);
	void insertMoAccountMapAccount(String functionId,String departmentId, String crsAccountId,String functiontitle);
}
