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

import com.twm.mgmt.persistence.dao.MomoidChangeMainDao;

import com.twm.mgmt.persistence.dto.MomoidChangeMainDto;

@Repository
public class MomoidChangeMainDaoImpl implements MomoidChangeMainDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@Override
	public List<MomoidChangeMainDto> findByMomoidChange(MomoidChangeVo momoidChangeVo) {
		String sql = composeSql(momoidChangeVo);

		Map<String, Object> params = composeParams(momoidChangeVo);

		Query query = null;

		query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.aliasToBean(MomoidChangeMainDto.class))
				.addScalar("MOMOID_CHANGE_MAIN_ID", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("BTN_STATUS", StandardBasicTypes.STRING)
				.addScalar("MOMOID_CHANGE_MAIN_ID_TYPE", StandardBasicTypes.STRING)
				.addScalar("DEPARTMENT_NAME", StandardBasicTypes.STRING)
				.addScalar("USER_NAME", StandardBasicTypes.STRING).addScalar("CREATE_DATE", StandardBasicTypes.STRING)
				.addScalar("APPROVAL_DATE", StandardBasicTypes.STRING).addScalar("SMS_DATE", StandardBasicTypes.STRING)
				.addScalar("STATUS", StandardBasicTypes.STRING);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return query.getResultList();

	}

	private String composeSql(MomoidChangeVo momoidChangeVo) {
		StringBuilder sb = new StringBuilder();

		sb.append("select distinct");
		sb.append(" mcm.momoid_change_main_id,");
		sb.append("CASE");
		sb.append("    WHEN mcm.status = 2 THEN '查看'");
		sb.append("    WHEN mca.approval_date is null and mcm.account_id = :accountId THEN '撤回'");
		sb.append("    WHEN mca.approval_date is null and mcm.account_id != :accountId THEN '查看'");
		sb.append("    WHEN mca.approval_date is not null THEN '查看'");
		sb.append("END Btn_Status,");
		sb.append("CASE");
		sb.append("    WHEN mcm.momoid_change_main_id_type = 0 THEN '客編異動'");
		sb.append("END momoid_change_main_id_type,");
		sb.append("dt.department_name,");
		sb.append("act.user_id user_name,");
		sb.append("NVL2(mca.create_date,to_char(mca.create_date,'yyyy/mm/dd'),'--')create_date,");
		sb.append("NVL2(mca.approval_date,to_char(mca.approval_date,'yyyy/mm/dd'),'--')approval_date,");
		sb.append("NVL2(mcs.sms_date,to_char(mcs.sms_date,'yyyy/mm/dd'),'--')sms_date,");
		sb.append("CASE");
		sb.append("    WHEN mcm.status = 0 THEN '待簽核'");
		sb.append("    WHEN mcm.status = 1 THEN '簽核駁回'");
		sb.append("    WHEN mcm.status = 2 THEN '撤回作廢'");
		sb.append("    WHEN mcm.status = 3 THEN '待發送'");
		sb.append("    WHEN mcm.status = 4 THEN '已發送'");
		sb.append("    WHEN mcm.status = 5 THEN '簽核同意'");
		sb.append("END status");
		sb.append(
				" from momoid_change_main mcm , momoid_change_approval mca,account act,department dt,momoid_change_sms mcs");
		sb.append(
				" where mcm.momoid_change_main_id = mca.momoid_change_main_id and mcm.ACCOUNT_ID = act.ACCOUNT_ID and act.department_id = dt.department_id and mcm.momoid_change_main_id = mcs.momoid_change_main_id");

		if (StringUtils.isNotBlank(momoidChangeVo.getDepartmentId())) {
			sb.append(" and dt.department_id =:departmentId");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getUserName())) {
			sb.append(" and lower(act.user_id)  like lower(:userName)");// 畫面條件
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getStatus())) {
			sb.append(" and  mcm.status =:status");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getMomoidChangeMainId())) {
			sb.append(" and mcm.momoid_change_main_id like :momoidChangeMainId");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getCreateDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getCreateDateEnd())) {
			sb.append(
					" and mca.create_date between to_date(:createDateStart,'yyyy/mm/dd HH24:mi:ss') and to_date(:createDateEnd,'yyyy/mm/dd HH24:mi:ss')");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getSmsDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getSmsDateEnd())) {
			sb.append(
					" and mcs.sms_date between to_date(:smsDateStart,'yyyy/mm/dd HH24:mi:ss') and to_date(:SmsDateEnd,'yyyy/mm/dd HH24:mi:ss')");
		}

		//sb.append(" order by case when Btn_Status = '撤回' then 1 end,momoid_change_main_id desc");
		sb.append(" order by create_date desc");

		return sb.toString();
	}

	private Map<String, Object> composeParams(MomoidChangeVo momoidChangeVo) {
		Map<String, Object> params = new HashMap<>();
		
		params.put("accountId", momoidChangeVo.getAccountId());

		if (StringUtils.isNotBlank(momoidChangeVo.getDepartmentId())) {

			params.put("departmentId", momoidChangeVo.getDepartmentId());
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getUserName())) {
			params.put("userName", "%" + momoidChangeVo.getUserName().replace(" ", "") + "%");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getStatus())) {
			params.put("status", momoidChangeVo.getStatus());
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getMomoidChangeMainId())) {

			params.put("momoidChangeMainId", "%" + momoidChangeVo.getMomoidChangeMainId().trim() + "%");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getCreateDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getCreateDateEnd())) {

			params.put("createDateStart", momoidChangeVo.getCreateDateStart() + " 00:00:00");
			params.put("createDateEnd", momoidChangeVo.getCreateDateEnd() + " 23:59:59");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getSmsDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getSmsDateEnd())) {

			params.put("smsDateStart", momoidChangeVo.getSmsDateStart() + " 00:00:00");
			params.put("SmsDateEnd", momoidChangeVo.getSmsDateEnd() + " 23:59:59");
		}

		return params;
	}

	@Override
	public List<MomoidChangeMainDto> findMomoidChangeApproval(MomoidChangeVo momoidChangeVo) {
		String sql = composeSql2(momoidChangeVo);
		Map<String, Object> params = composeParams2(momoidChangeVo);
		Query query = null;
		query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.aliasToBean(MomoidChangeMainDto.class))
				.addScalar("MOMOID_CHANGE_MAIN_ID", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("BTN_STATUS", StandardBasicTypes.STRING)
				.addScalar("USER_NAME", StandardBasicTypes.STRING).addScalar("CREATE_DATE", StandardBasicTypes.STRING)
				.addScalar("APPROVAL_DATE", StandardBasicTypes.STRING).addScalar("SMS_DATE", StandardBasicTypes.STRING)
				.addScalar("STATUS", StandardBasicTypes.STRING).addScalar("PICTURE", StandardBasicTypes.STRING);
		
		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return query.getResultList();
	}

	private String composeSql2(MomoidChangeVo momoidChangeVo) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select distinct");
		sb.append(" mcm.momoid_change_main_id,");
		sb.append(" CASE");
		sb.append("    WHEN mca.opinion = 0 THEN '簽核'");
		sb.append("    WHEN mca.opinion = 1 THEN '同意'");
		sb.append("    WHEN mca.opinion = 2 THEN '駁回'");
		sb.append(" END Btn_Status,");
		sb.append(" act.user_id user_name,");
		sb.append(" NVL2(mca.create_date,to_char(mca.create_date,'yyyy/mm/dd'),'--')create_date,");
		sb.append(" NVL2(mca.approval_date,to_char(mca.approval_date,'yyyy/mm/dd'),'--')approval_date,");
		sb.append(" NVL2(mcs.sms_date,to_char(mcs.sms_date,'yyyy/mm/dd'),'--')sms_date,");
		sb.append(" CASE");
		sb.append("    WHEN mcm.status = 0 THEN '待簽核'");
		sb.append("    WHEN mcm.status = 1 THEN '簽核駁回'");
		sb.append("    WHEN mcm.status = 2 THEN '撤回作廢'");
		sb.append("    WHEN mcm.status = 3 THEN '待發送'");
		sb.append("    WHEN mcm.status = 4 THEN '已發送'");
		sb.append("    WHEN mcm.status = 5 THEN '簽核同意'");
		sb.append(" END status, ");
		sb.append(" mcp.momoid_change_main_id picture ");		
		sb.append(
				" from momoid_change_main mcm left join momoid_change_picture mcp on mcm.momoid_change_main_id = mcp.momoid_change_main_id, momoid_change_approval mca,account act,department dt,momoid_change_sms mcs ");
		sb.append(
				" where mcm.momoid_change_main_id = mca.momoid_change_main_id and mcm.ACCOUNT_ID = act.ACCOUNT_ID and act.department_id = dt.department_id and mcm.momoid_change_main_id = mcs.momoid_change_main_id");
		sb.append(" and mca.account_id =:accountId");

		if (StringUtils.isNotBlank(momoidChangeVo.getStatus())) {
			sb.append(" and  mcm.status =:status");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getMomoidChangeMainId())) {
			sb.append(" and mcm.momoid_change_main_id like :momoidChangeMainId");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getCreateDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getCreateDateEnd())) {
			sb.append(
					" and mca.create_date between to_date(:createDateStart,'yyyy/mm/dd HH24:mi:ss') and to_date(:createDateEnd,'yyyy/mm/dd HH24:mi:ss')");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getSmsDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getSmsDateEnd())) {
			sb.append(
					" and mcs.sms_date between to_date(:smsDateStart,'yyyy/mm/dd HH24:mi:ss') and to_date(:SmsDateEnd,'yyyy/mm/dd HH24:mi:ss')");
		}

		sb.append(" and mcm.status not in (2)");
		//sb.append(" order by case when status = '待簽核' then 1 end,momoid_change_main_id desc");
		sb.append(" order by create_date desc");
		return sb.toString();

	}

	private Map<String, Object> composeParams2(MomoidChangeVo momoidChangeVo) {
		
		Map<String, Object> params = new HashMap<>();

		params.put("accountId", momoidChangeVo.getAccountId());

		if (StringUtils.isNotBlank(momoidChangeVo.getStatus())) {
			params.put("status", momoidChangeVo.getStatus());
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getMomoidChangeMainId())) {
			params.put("momoidChangeMainId", "%"+momoidChangeVo.getMomoidChangeMainId().trim()+"%");
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getCreateDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getCreateDateEnd())) {
			params.put("createDateStart", momoidChangeVo.getCreateDateStart() + " 00:00:00");
			params.put("createDateEnd", momoidChangeVo.getCreateDateEnd() + " 23:59:59");
		
		}

		if (StringUtils.isNotBlank(momoidChangeVo.getSmsDateStart())
				&& StringUtils.isNotBlank(momoidChangeVo.getSmsDateEnd())) {
			params.put("smsDateStart", momoidChangeVo.getSmsDateStart() + " 00:00:00");
			params.put("SmsDateEnd", momoidChangeVo.getSmsDateEnd() + " 23:59:59");
		}
		
		return params;
	}
	
	@Override
	public MomoidChangeMainDto signOffFlowChart(String approvalId) {
		String sql = composeSql3(approvalId);
		Map<String, Object> params = composeParams3(approvalId);
		Query query = null;
		query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.aliasToBean(MomoidChangeMainDto.class))
				.addScalar("MOMOID_CHANGE_MAIN_ID", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("MCM_USER_NAME", StandardBasicTypes.STRING)
				.addScalar("DEPARTMENT_NAME", StandardBasicTypes.STRING).addScalar("OPINION", StandardBasicTypes.STRING)
				.addScalar("MCA_USER_NAME", StandardBasicTypes.STRING).addScalar("COMMENT_INFO", StandardBasicTypes.STRING);
		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return (MomoidChangeMainDto) query.getResultList().get(0);
	}
	
	
	private String composeSql3(String approvalId) {
		StringBuilder sb = new StringBuilder();
		sb.append("select mcm.momoid_change_main_id,ac.user_id mcm_user_name,dp.department_name,");
		sb.append(" CASE");
		sb.append("		WHEN mca.opinion = 0  and  mcm.status != 2 THEN '待簽核'");
		sb.append("		WHEN mca.opinion = 0 and  mcm.status = 2 THEN '撤回作廢'");
		sb.append("		WHEN mca.opinion = 1 THEN '同意'");
		sb.append("		WHEN mca.opinion = 2 THEN '駁回'");
		sb.append(" END opinion,");
		sb.append(" (select ac.user_id from momoid_change_main mcm,momoid_change_approval mca,account ac  where mcm.momoid_change_main_id = mca.momoid_change_main_id and mca.account_id = ac.account_id and mcm.momoid_change_main_id = :momoidChangeMainId)");
		sb.append(" mca_user_name,");
		sb.append(" mca.comment_info");
		sb.append(" from momoid_change_main mcm,momoid_change_approval mca,account ac ,department dp");
		sb.append(" where  mcm.momoid_change_main_id = mca.momoid_change_main_id");
		sb.append(" and mcm.account_id = ac.account_id");
		sb.append(" and ac.department_id = dp.department_id");
		sb.append(" and mcm.momoid_change_main_id = :momoidChangeMainId");

		return sb.toString();

	}
	
	private Map<String, Object> composeParams3(String approvalId) {
		
		Map<String, Object> params = new HashMap<>();

		params.put("momoidChangeMainId", new BigDecimal(approvalId));
		
		return params;
	}

}
