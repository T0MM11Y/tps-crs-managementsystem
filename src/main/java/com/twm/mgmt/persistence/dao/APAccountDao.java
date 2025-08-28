package com.twm.mgmt.persistence.dao;

import java.util.List;

import com.twm.mgmt.model.account.APAccountConditionVo;
import com.twm.mgmt.persistence.dto.APAccountDto;

public interface APAccountDao {

	List<APAccountDto> findByCondition(APAccountConditionVo condition);

	Integer countByCondition(APAccountConditionVo condition);

}
