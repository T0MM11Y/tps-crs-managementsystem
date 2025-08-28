package com.twm.mgmt.persistence.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.model.report.CheckbillVo;
import com.twm.mgmt.model.usermanage.ExchangecurrencyVo;
import com.twm.mgmt.persistence.dao.ExchangecurrencyDao;
import com.twm.mgmt.persistence.dto.ExchangecurrencyDto;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.StringUtilsEx;

@Repository
public class ExchangecurrencyDaoImpl implements ExchangecurrencyDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Override
	public List<ExchangecurrencyDto> findByCondition(ExchangecurrencyVo condition) {
		
		if (!(condition.getDepartmentId().equals(""))) {
			//System.out.println("aa11:"+condition.getDepartmentId());
			
			String[] split = condition.getDepartmentId().split("_");
			
			
			condition.setDepartmentId(split[0]);
			split=null;
		}		
		
		String sql = composeSql(condition, false);
		//System.out.println("findByCondition0:"+(condition.getStatus().equals("checkboxA")));
		//System.out.println("SQLContext:"+sql);
		Map<String, Object> params = composeParams(condition);
		Query query = null;
		if(condition.getStatus().equals("checkboxB")) {
			 query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(ExchangecurrencyDto.class))
					 .addScalar("twmuid", StandardBasicTypes.STRING)
					 .addScalar("departmentId", StandardBasicTypes.STRING)
					 .addScalar("exchangeitems", StandardBasicTypes.STRING)
						.addScalar("getmo", StandardBasicTypes.STRING)
						.addScalar("exchangemo", StandardBasicTypes.STRING)					 
					;
		}
		if(condition.getStatus().equals("checkboxA")) {
			 query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(ExchangecurrencyDto.class))
					 .addScalar("campaignDetailID", StandardBasicTypes.STRING)
					 .addScalar("twmuid", StandardBasicTypes.STRING)
					.addScalar("momenberid", StandardBasicTypes.STRING)
					.addScalar("campaignName", StandardBasicTypes.STRING)
					.addScalar("campaignInfo", StandardBasicTypes.STRING)
					.addScalar("exchangeitems", StandardBasicTypes.STRING)
					.addScalar("departmentId", StandardBasicTypes.STRING)
					.addScalar("getmo", StandardBasicTypes.STRING)
					.addScalar("exchangemo", StandardBasicTypes.STRING)
					.addScalar("remard", StandardBasicTypes.STRING)
					.addScalar("remardstatus", StandardBasicTypes.STRING)
					;
		}
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
//		query.setMaxResults(5000);
		//System.out.println("findByCondition2:"+(query.getResultList().size()));
		return query.getResultList();


	}



	private String composeSql(ExchangecurrencyVo condition, boolean isCount ) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT ");
		if(condition.getStatus().equals("checkboxA")) {
			
			if(isCount) {
				sb.append(" count(toh.TWM_UID)  from TRANSACTION_OFFER_HISTORY toh, MOMOAPI.REWARD_REPORT_DETAIL rrd, CAMPAIGN_DETAIL cd, account ac, DEPARTMENT department, MO_ACCOUNT moac, REWARD_MEMO rm");								
			}else {
				sb.append(" rrd.CAMPAIGN_DETAIL_ID as campaignDetailID, toh.STATUS as remardstatus, rm.MEMO_INFO as remard, toh.MOMO_MEMBER_ID as momenberid, toh.TWM_UID as twmuid, to_char(toh.MO_TX_TIME,'yyyy-mm-dd hh24:mi') as exchangemo, toh.AMOUNT as getmo, cd.CAMPAIGN_NAME as campaignName, toh.ORDER_NOTE as campaignInfo, toh.ORDER_NOTE as exchangeitems, department.DEPARTMENT_NAME as departmentId  from TRANSACTION_OFFER_HISTORY toh, MOMOAPI.REWARD_REPORT_DETAIL rrd, CAMPAIGN_DETAIL cd, account ac, DEPARTMENT department, MO_ACCOUNT moac, REWARD_MEMO rm");							
			}
			sb.append(" where toh.REPORT_DETAIL_ID = rrd.REPORT_DETAIL_ID");
			sb.append(" and toh.TRANSACTION_OFFER_ID = rm.TRANSACTION_ID");	
			sb.append(" and rrd.CAMPAIGN_DETAIL_ID = cd.CAMPAIGN_DETAIL_ID");
			sb.append(" and cd.CREATE_ACCOUNT = ac.ACCOUNT_ID ");
			sb.append(" and ac.DEPARTMENT_ID = department.DEPARTMENT_ID");
			sb.append(" and rrd.CAMPAIGN_DETAIL_ID = cd.CAMPAIGN_DETAIL_ID");
			sb.append(" and cd.CREATE_ACCOUNT = ac.ACCOUNT_ID ");
			sb.append(" and moac.DEPARTMENT_ID = department.DEPARTMENT_ID ");
		}
		if(condition.getStatus().equals("checkboxB")) {
			if(isCount) {
				sb.append(" count(*)  from TRANSACTION_REDEEM_HISTORY toh ,MO_ACCOUNT moac ,DEPARTMENT department ");				
			}else {
				sb.append(" toh.TWM_UID AS twmuid ,department.DEPARTMENT_NAME AS departmentId ,toh.ORDER_NOTE AS exchangeitems ,toh.AMOUNT AS getmo ,to_char(toh.MO_TX_TIME,'yyyy-mm-dd hh24:mi') AS exchangemo from TRANSACTION_REDEEM_HISTORY toh ,MO_ACCOUNT moac ,DEPARTMENT department");		
			}
			
			sb.append(" WHERE toh.STATUS = 'SUCCESS_REDEEM'");
			sb.append(" and moac.DEPT_NO = toh.DEPT_NO");
			sb.append(" AND department.DEPARTMENT_ID = moac.DEPARTMENT_ID");
		}		
		
		

		if (StringUtils.isNoneBlank(condition.getMomenberid())) {
			if(condition.getStatus().equals("checkboxA")) {
				sb.append(" and toh.MOMO_MEMBER_ID = :momenberid");
			}
		}

		if (StringUtils.isNoneBlank(condition.getTwmuid())) {

			sb.append(" and toh.TWM_UID = :twmuid");
		}
		
		if (StringUtils.isNoneBlank(condition.getTwmuid1())) {

			sb.append(" and toh.TWM_UID = :twmuid");
		}
		
		if (StringUtils.isNoneBlank(condition.getCampaignName())) {

//			sb.append(" and cd.CAMPAIGN_NAME = :campaignName");
			sb.append(" and cd.CAMPAIGN_NAME like :campaignName");
		}
		
		if (StringUtils.isNoneBlank(condition.getExchangeitems())) {

			//System.out.println("SDFSDFSD:"+condition.getExchangeitems());
			sb.append(" and toh.order_note = :exchangeitems");
		}
		
		if (StringUtils.isNoneBlank(condition.getDepartmentId())) {

			sb.append(" and moac.DEPARTMENT_ID = :departmentId");
		}
		
		if (StringUtils.isNoneBlank(condition.getRemard())) {

			sb.append(" and rm.MEMO_TYPE = :remard");
		}
		
		//sendStartDate
		if (StringUtilsEx.isNotBlank(condition.getSendStartDate()) && StringUtilsEx.isNotBlank(condition.getSendEndDate())) {
			
			sb.append(" AND toh.MO_TX_TIME BETWEEN :sendStartDate AND :sendEndDate");
		}
		sb.append(" order by toh.MO_TX_TIME desc ");
		
		return sb.toString();
	}

	private Map<String, Object> composeParams(ExchangecurrencyVo condition) {
		Map<String, Object> params = new HashMap<>();
		//System.out.println("composeParams0:"+condition.getMomenberid());
		
		if (StringUtils.isNoneBlank(condition.getMomenberid())) {
			params.put("momenberid", condition.getMomenberid());
		}
		
		if(condition.getStatus().equals("checkboxA")) {
			if (StringUtils.isNoneBlank(condition.getTwmuid())) {
				//System.out.println("entergetTwmuid");
				params.put("twmuid", condition.getTwmuid());
			}			
		}
		
		if(condition.getStatus().equals("checkboxB")) {
			if (StringUtils.isNoneBlank(condition.getTwmuid1())) {
				//System.out.println("entergetTwmuid1");
				params.put("twmuid", condition.getTwmuid1());
			}			
		}		
		
		if (StringUtils.isNoneBlank(condition.getCampaignName())) {
			params.put("campaignName", "%"+condition.getCampaignName()+"%");
		}
		
		if (StringUtils.isNoneBlank(condition.getExchangeitems())) {
			params.put("exchangeitems", condition.getExchangeitems());
		}
		
		if (StringUtils.isNoneBlank(condition.getDepartmentId())) {
			//System.out.println("composeParams2:"+(condition.getDepartmentId()));
			params.put("departmentId", condition.getDepartmentId());
		}
		
		if (StringUtils.isNoneBlank(condition.getRemard())) {
			params.put("remard", condition.getRemard());
		}
		
		if (StringUtilsEx.isNotBlank(condition.getSendStartDate()) && StringUtilsEx.isNotBlank(condition.getSendEndDate())) {
			
			params.put("sendStartDate", DateUtilsEx.startDate(condition.getSendStartDate()));
			params.put("sendEndDate", DateUtilsEx.endDate(condition.getSendEndDate()));
		}
		//System.out.println("composeParams2:"+(params.size()));

		return params;
	}

	@Override
	public Integer countByCondition(ExchangecurrencyVo condition) {
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
