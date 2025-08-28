package com.twm.mgmt.persistence.dao;

import java.math.BigDecimal;
import java.util.List;


import com.twm.mgmt.model.usermanage.ExchangecurrencyVo;
import com.twm.mgmt.persistence.dto.ExchangecurrencyDto;

public interface ExchangecurrencyDao {

	List<ExchangecurrencyDto> findByCondition(ExchangecurrencyVo condition);

	Integer countByCondition(ExchangecurrencyVo condition);


}
