package com.twm.mgmt.persistence.dao;

import java.util.List;

import com.twm.mgmt.model.account.MOAccountConditionVo;
import com.twm.mgmt.persistence.dto.MOAccountDto;

public interface MOAccountDao {

	List<MOAccountDto> findByCondition(MOAccountConditionVo condition);

	Integer countByCondition(MOAccountConditionVo condition);

}
