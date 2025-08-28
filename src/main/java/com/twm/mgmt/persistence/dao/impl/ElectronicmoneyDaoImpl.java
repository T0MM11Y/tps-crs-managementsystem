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
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.springframework.stereotype.Repository;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.model.report.CheckbillVo;
import com.twm.mgmt.model.report.ElectronicMoneyVo;
import com.twm.mgmt.model.usermanage.ExchangecurrencyVo;
import com.twm.mgmt.persistence.dao.ElectronicmoneyDao;
import com.twm.mgmt.persistence.dao.ExchangecurrencyDao;
import com.twm.mgmt.persistence.dto.ElectronicmoneyDto;
import com.twm.mgmt.persistence.dto.ExchangecurrencyDto;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.StringUtilsEx;

@Repository
public class ElectronicmoneyDaoImpl implements ElectronicmoneyDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;
	
	private Encoder encode=ESAPI.encoder();

	@SuppressWarnings("unchecked")
	@Override
	public List<ElectronicmoneyDto> findByCondition(ElectronicMoneyVo condition) {
		String sql = composeSql(condition, false);
		
		//System.out.println("SQLContext:"+sql);
		Map<String, Object> params = composeParams(condition);
		
		Query query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(ElectronicmoneyDto.class))
				.addScalar("momoeventId", StandardBasicTypes.STRING)
				.addScalar("payaccount", StandardBasicTypes.STRING);
		if(params.size()>0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				//System.out.println("params:"+entry.getKey()+", "+entry.getValue().toString());
				query.setParameter(entry.getKey(), entry.getValue());
			}			
		}

		
//		query.setFirstResult((condition.getNumber() - 1) * condition.getSize());
		//
//		query.setMaxResults(condition.getSize());
//		//System.out.println("findByCondition1:"+(condition.getNumber() - 1) * condition.getSize());
		//System.out.println("findByCondition1:"+(condition.getNumber() - 1) +", "+ condition.getSize());
		query.setFirstResult((condition.getNumber() - 1) * condition.getSize());
		query.setMaxResults(countByCondition(condition));
		//System.out.println("findByCondition2:"+(query.getResultList().size()));
		return query.getResultList();

//		try {
//			List<ElectronicmoneyDto> tmp = query.getResultList();
//			return tmp;
//		} catch (Exception e) {
//			//System.out.println("ErrorS");
//			//System.out.println(e);
//			//System.out.println("ErrorE");
//			throw e;
//		}

//		 return query.getResultList();

	}



	private String composeSql(ElectronicMoneyVo condition , boolean isCount) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT");
	
		if(isCount) {
			sb.append(" count(toh.REPORT_ID) from REWARD_REPORT toh, CAMPAIGN_DETAIL cd , MO_ACCOUNT moac,  DEPARTMENT department, ACCOUNT ac");
						
		}else {
			sb.append(" toh.ORDER_NUMBER as momoeventId, toh.PAY_ACCOUNT as payaccount from REWARD_REPORT toh, CAMPAIGN_DETAIL cd , MO_ACCOUNT moac,  DEPARTMENT department, ACCOUNT ac");
						
		}
		

		sb.append(" where  moac.DEPARTMENT_ID = department.DEPARTMENT_ID");

		sb.append(" and toh.CAMPAIGN_DETAIL_ID = cd.CAMPAIGN_DETAIL_ID");
		
		sb.append(" and cd.CREATE_ACCOUNT = ac.ACCOUNT_ID");

		sb.append(" and ac.DEPARTMENT_ID = department.DEPARTMENT_ID");
		
		
		if (!(condition.getMomoeventId().equals(""))) {
			//System.out.println("aa0");

			sb.append(" and toh.ORDER_NUMBER = :momoeventId");
		}

		if (!(condition.getPayaccountId().equals(""))) {
			//System.out.println("aa1");
			sb.append(" and toh.PAY_ACCOUNT = :payaccountId");
		}
		
		if (!(condition.getDepartmentId().equals(""))) {
			//System.out.println("aa2");
			sb.append(" and toh.REQUISITION_UNIT = :departmentId");
		}

		//sendStartDate
		if (StringUtilsEx.isNotBlank(condition.getSendStartDate()) && StringUtilsEx.isNotBlank(condition.getSendEndDate())) {
			//System.out.println("aa3");
			sb.append(" AND toh.POAPPROVEDDATE BETWEEN :sendStartDate AND :sendEndDate");
		}
		return sb.toString();
	}

	private Map<String, Object> composeParams(ElectronicMoneyVo condition) {
		Map<String, Object> params = new HashMap<>();
		
		if (!(condition.getMomoeventId().equals(""))) {
			//System.out.println("aa00");
			params.put("momoeventId", encode.encodeForHTML(condition.getMomoeventId()));
		}
	

		if (!(condition.getPayaccountId().equals(""))) {
			//System.out.println("aa11");
			params.put("payaccountId", encode.encodeForHTML(condition.getPayaccountId()));
		}
		
		if (!(condition.getDepartmentId().equals(""))) {
			
			
//			String[] split = condition.getDepartmentId().split("_");
//			//System.out.println("aa22:"+split[1]);
			params.put("departmentId", encode.encodeForHTML(condition.getDepartmentId()));
//			params.put("departmentId", condition.getDepartmentId());
		}
		

		
		if (StringUtilsEx.isNotBlank(condition.getSendStartDate()) && StringUtilsEx.isNotBlank(condition.getSendEndDate())) {
			//System.out.println("aa33:"+DateUtilsEx.startDate(condition.getSendStartDate()) +" ~ "+DateUtilsEx.endDate(condition.getSendEndDate()));
			params.put("sendStartDate", DateUtilsEx.startDate(condition.getSendStartDate()));
			params.put("sendEndDate", DateUtilsEx.endDate(condition.getSendEndDate()));
		}
		//System.out.println("composeParams2:"+(params.size()));

		return params;
	}

	
	@Override
	public Integer countByCondition(ElectronicMoneyVo condition) {
		String sql = composeSql(condition, true);

		Map<String, Object> params = composeParams(condition);

		Query query = manager.createNativeQuery(sql);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
//		return query.getResultList();
		return ((BigDecimal) query.getSingleResult()).intValue();
	}
}
