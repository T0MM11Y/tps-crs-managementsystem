package com.twm.mgmt.persistence.dao;

import java.math.BigDecimal;
import java.util.List;


import com.twm.mgmt.model.report.ElectronicMoneyVo;
import com.twm.mgmt.model.usermanage.ExchangecurrencyVo;
import com.twm.mgmt.persistence.dto.ElectronicmoneyDto;
import com.twm.mgmt.persistence.dto.ExchangecurrencyDto;

public interface ElectronicmoneyDao {

	List<ElectronicmoneyDto> findByCondition(ElectronicMoneyVo condition);

	Integer countByCondition(ElectronicMoneyVo condition);


}
