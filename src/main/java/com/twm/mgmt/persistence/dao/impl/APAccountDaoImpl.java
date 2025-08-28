package com.twm.mgmt.persistence.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.model.account.APAccountConditionVo;
import com.twm.mgmt.persistence.dao.APAccountDao;
import com.twm.mgmt.persistence.dto.APAccountDto;

@Repository
public class APAccountDaoImpl implements APAccountDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Override
	public List<APAccountDto> findByCondition(APAccountConditionVo condition) {
		String sql = composeSql(condition, false);

		Map<String, Object> params = composeParams(condition);

		Query query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(APAccountDto.class)).addScalar("departmentId", StandardBasicTypes.LONG)
				.addScalar("departmentName", StandardBasicTypes.STRING).addScalar("sourceId", StandardBasicTypes.STRING).addScalar("enabled", StandardBasicTypes.STRING).addScalar("createDate", StandardBasicTypes.TIMESTAMP);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		query.setFirstResult((condition.getNumber() - 1) * condition.getSize());

		query.setMaxResults(condition.getSize());

		return query.getResultList();
	}

	@Override
	public Integer countByCondition(APAccountConditionVo condition) {
		String sql = composeSql(condition, true);

		Map<String, Object> params = composeParams(condition);

		Query query = manager.createNativeQuery(sql);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return ((BigDecimal) query.getSingleResult()).intValue();
	}

	private String composeSql(APAccountConditionVo condition, boolean isCount) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT");

		if (isCount) {
			sb.append(" COUNT(aa.DEPARTMENT_ID)");
		} else {
			sb.append(" aa.DEPARTMENT_ID as departmentId, aa.SOURCE_ID as sourceId, aa.ENABLED as enabled, aa.CREATE_DATE as createDate,");

			sb.append(" d.DEPARTMENT_NAME as departmentName");
		}

		sb.append(" FROM ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".AP_ACCOUNT aa");

		sb.append(" LEFT JOIN ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".DEPARTMENT d on d.DEPARTMENT_ID = aa.DEPARTMENT_ID");

		sb.append(" WHERE 1 = 1");

		if (condition.getDepartmentId() != null) {
			sb.append(" AND aa.DEPARTMENT_ID = :departmentId");
		}

		sb.append(" ORDER BY aa.CREATE_DATE");

		return sb.toString();
	}

	private Map<String, Object> composeParams(APAccountConditionVo condition) {
		Map<String, Object> params = new HashMap<>();

		if (condition.getDepartmentId() != null) {
			params.put("departmentId", condition.getDepartmentId());
		}

		return params;
	}

}
