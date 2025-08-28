package com.twm.mgmt.persistence.dao.impl;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import com.twm.mgmt.config.MoDbConfig;

import com.twm.mgmt.model.momoidChange.MomoidChangeVo;
import com.twm.mgmt.persistence.dao.MomoidChangeListDao;
import com.twm.mgmt.persistence.dao.MomoidChangeMainDao;

import com.twm.mgmt.persistence.dto.MomoidChangeMainDto;
import com.twm.mgmt.persistence.entity.MomoidChangeListEntity;


@Repository
public class MomoidChangeListDaoImpl implements MomoidChangeListDao {
	
	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;






}
