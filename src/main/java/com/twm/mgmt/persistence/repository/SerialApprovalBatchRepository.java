package com.twm.mgmt.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.twm.mgmt.persistence.entity.SerialApprovalBatchEntity;


public interface SerialApprovalBatchRepository extends JpaRepository<SerialApprovalBatchEntity, BigDecimal> {


}