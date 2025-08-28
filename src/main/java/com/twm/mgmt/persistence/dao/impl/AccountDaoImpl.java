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
import com.twm.mgmt.model.account.AccountConditionVo;
import com.twm.mgmt.persistence.dao.AccountDao;
import com.twm.mgmt.persistence.dto.AccountDto;
import com.twm.mgmt.utils.StringUtilsEx;

@Repository
public class AccountDaoImpl implements AccountDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Override
	public List<AccountDto> findByCondition(AccountConditionVo condition) {
		String sql = composeSql(condition, false);

		Map<String, Object> params = composeParams(condition);

		Query query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(AccountDto.class)).addScalar("accountId", StandardBasicTypes.LONG)
				.addScalar("userName", StandardBasicTypes.STRING).addScalar("email", StandardBasicTypes.STRING).addScalar("roleId", StandardBasicTypes.LONG).addScalar("roleName", StandardBasicTypes.STRING)
				.addScalar("departmentId", StandardBasicTypes.LONG).addScalar("departmentName", StandardBasicTypes.STRING).addScalar("enabled", StandardBasicTypes.STRING);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		query.setFirstResult((condition.getNumber() - 1) * condition.getSize());

		query.setMaxResults(condition.getSize());

		return query.getResultList();
	}

	@Override
	public Integer countByCondition(AccountConditionVo condition) {
		String sql = composeSql(condition, true);

		Map<String, Object> params = composeParams(condition);

		Query query = manager.createNativeQuery(sql);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return ((BigDecimal) query.getSingleResult()).intValue();
	}

	private String composeSql(AccountConditionVo condition, boolean isCount) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT");

		if (isCount) {
			sb.append(" COUNT(a.ACCOUNT_ID)");
		} else {
			sb.append(" a.ACCOUNT_ID as accountId, a.USER_NAME as userName, a.EMAIL as email,");

			sb.append(" a.ROLE_ID as roleId, b.ROLE_NAME as roleName,");

			sb.append(" a.DEPARTMENT_ID as departmentId, c.DEPARTMENT_NAME as departmentName,");

			sb.append(" a.ENABLED as enabled ");
		}

		sb.append(" FROM ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".ACCOUNT a");

		sb.append(" LEFT JOIN ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".ROLE b on a.ROLE_ID = b.ROLE_ID");

		sb.append(" LEFT JOIN ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".DEPARTMENT c on a.DEPARTMENT_ID = c.DEPARTMENT_ID");

		sb.append(" WHERE 1 = 1");

		if (StringUtilsEx.isNotBlank(condition.getDepartmentId())) {
			sb.append(" AND a.DEPARTMENT_ID = :departmentId ");
		}

		if (StringUtilsEx.isNotBlank(condition.getRoleId())) {
			sb.append(" AND a.ROLE_ID = :roleId ");
		}

		return sb.toString();
	}

	private Map<String, Object> composeParams(AccountConditionVo condition) {
		Map<String, Object> params = new HashMap<>();

		if (StringUtilsEx.isNotBlank(condition.getDepartmentId())) {
			params.put("departmentId", condition.getDepartmentId());
		}

		//if (condition.getRoleId() != null) {
		if (StringUtilsEx.isNotBlank(condition.getRoleId()) ) {
			params.put("roleId", condition.getRoleId());
		}

		return params;
	}

}
