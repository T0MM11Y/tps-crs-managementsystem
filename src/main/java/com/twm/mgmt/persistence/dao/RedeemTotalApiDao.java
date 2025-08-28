package com.twm.mgmt.persistence.dao;

import java.util.List;

import com.twm.mgmt.model.account.RedeemTotalApiVo;
import com.twm.mgmt.persistence.dto.RedeemTotalApiDto;

public interface RedeemTotalApiDao{
	
	List<RedeemTotalApiDto> findByCondition(RedeemTotalApiVo condition);
	Integer countByCondition(RedeemTotalApiVo condition);
	void deleteRedeemTotalApi(String moDeptNo, String apiUrl);
	void insertRedeemTotalApi(String departmentId, String apiUrl);
}
