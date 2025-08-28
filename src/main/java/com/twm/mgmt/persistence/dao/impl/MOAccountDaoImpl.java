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
import com.twm.mgmt.model.account.MOAccountConditionVo;
import com.twm.mgmt.persistence.dao.MOAccountDao;
import com.twm.mgmt.persistence.dto.MOAccountDto;

@Repository
public class MOAccountDaoImpl implements MOAccountDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Override
	public List<MOAccountDto> findByCondition(MOAccountConditionVo condition) {
		String sql = composeSql(condition, false);

		Map<String, Object> params = composeParams(condition);

		Query query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(MOAccountDto.class)).addScalar("merchantId", StandardBasicTypes.STRING)
				.addScalar("deptNo", StandardBasicTypes.STRING).addScalar("updateDate", StandardBasicTypes.TIMESTAMP).addScalar("contactUserName", StandardBasicTypes.STRING).addScalar("departmentName", StandardBasicTypes.STRING);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		query.setFirstResult((condition.getNumber() - 1) * condition.getSize());

		query.setMaxResults(condition.getSize());

		return query.getResultList();
	}

	@Override
	public Integer countByCondition(MOAccountConditionVo condition) {
		String sql = composeSql(condition, true);

		Map<String, Object> params = composeParams(condition);

		Query query = manager.createNativeQuery(sql);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return ((BigDecimal) query.getSingleResult()).intValue();
	}

	private String composeSql(MOAccountConditionVo condition, boolean isCount) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT");

		if (isCount) {
			sb.append(" COUNT(ma.MERCHANT_ID)");
		} else {
			sb.append(" ma.MERCHANT_ID as merchantId, ma.DEPT_NO as deptNo, ma.UPDATE_DATE as updateDate,");

			sb.append(" a.USER_NAME as contactUserName, d.DEPARTMENT_NAME as departmentName");
		}

		sb.append(" FROM ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".MO_ACCOUNT ma");

		sb.append(" LEFT JOIN ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".ACCOUNT a ON a.ACCOUNT_ID = ma.CONTACT_ACCOUNT_ID");

		sb.append(" LEFT JOIN ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".DEPARTMENT d ON d.DEPARTMENT_ID = ma.DEPARTMENT_ID");

		sb.append(" WHERE 1 = 1");

		sb.append(" ORDER BY ma.UPDATE_DATE DESC");

		return sb.toString();
	}

	private Map<String, Object> composeParams(MOAccountConditionVo condition) {
		Map<String, Object> params = new HashMap<>();

		return params;
	}

}
