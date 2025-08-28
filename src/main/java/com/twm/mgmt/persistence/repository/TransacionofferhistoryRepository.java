package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import com.twm.mgmt.persistence.dao.ExchangecurrencyDao;
import com.twm.mgmt.persistence.entity.TransactionofferhistoryEntity;

public interface TransacionofferhistoryRepository extends JpaRepository<TransactionofferhistoryEntity, BigDecimal>, ExchangecurrencyDao {


	
}
