package com.twm.mgmt.persistence.dao.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.model.account.MoAccountMapAccountConditionVo;
import com.twm.mgmt.persistence.dao.MoAccountMapAccountDao;
import com.twm.mgmt.persistence.dto.MoAccountMapAccountDto;

@Repository
public class MoAccountMapAccountDaoImpl implements MoAccountMapAccountDao {
	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Override
	public List<MoAccountMapAccountDto> findByCondition(MoAccountMapAccountConditionVo condition) {
	    String sql = composeSql(condition, false);
	    Query query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class)
	            .setResultTransformer(Transformers.aliasToBean(MoAccountMapAccountDto.class))
	            .addScalar("functionId", StandardBasicTypes.LONG)
	            .addScalar("functionTitle", StandardBasicTypes.STRING)
	            .addScalar("moDeptNo", StandardBasicTypes.STRING)
	            .addScalar("crsAccountId", StandardBasicTypes.LONG)
	            .addScalar("crsUserName", StandardBasicTypes.STRING)
	            .addScalar("departmentId", StandardBasicTypes.LONG)
	            .addScalar("department", StandardBasicTypes.STRING)
	            .addScalar("createDate", StandardBasicTypes.TIMESTAMP)
	            .addScalar("updateDate", StandardBasicTypes.TIMESTAMP);

	    // 綁定參數
	    //解Checkmarx的高風險SQL Injection
	    if (condition.getSearch() != null && !condition.getSearch().isEmpty()) {
	        String searchKeyword = "%" + condition.getSearch().replace(" ", "").replace("\t", "").toLowerCase() + "%";
	        query.setParameter("searchKeyword", searchKeyword);
	    }

	    query.setFirstResult((condition.getNumber() - 1) * condition.getSize());
	    query.setMaxResults(condition.getSize());

	    return query.getResultList();
	}

	@Override
	public Integer countByCondition(MoAccountMapAccountConditionVo condition) {
		String sql = composeSql(condition, true);
		Query query = manager.createNativeQuery(sql);
		
	    // 綁定參數
	    if (condition.getSearch() != null && !condition.getSearch().isEmpty()) {
	        String searchKeyword = "%" + condition.getSearch().replace(" ", "").replace("\t", "").toLowerCase() + "%";
	        query.setParameter("searchKeyword", searchKeyword);
	    }
	    
		return ((BigDecimal) query.getSingleResult()).intValue();
	}
	
	public void deleteMoAccountMapAccount(String functionId, String moDeptNo, String crsAccountId) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".MOACCOUNT_MAP_ACCOUNT ma");
		sb.append(" WHERE ma.FUNCTIONID=:functionId");
		sb.append(" AND   ma.MODEPTNO=:moDeptNo");
		sb.append(" AND   ma.CRSACCOUNTID=:crsAccountId");
		String sql = sb.toString();
		Query query = manager.createNativeQuery(sql);
	    query.setParameter("functionId", functionId);
	    query.setParameter("moDeptNo", moDeptNo);
	    query.setParameter("crsAccountId", crsAccountId);
	    query.executeUpdate();
	}
	
	public void insertMoAccountMapAccount(String functionId, String departmentId, String crsAccountId,String functiontitle) {
		
		//先查出該用戶的TWM部門資訊
		String sql = "SELECT a.ACCOUNT_ID, a.USER_NAME, a.DEPARTMENT_ID, d.DEPARTMENT_NAME  "
				+ "FROM MOMOAPI.ACCOUNT a LEFT JOIN MOMOAPI.DEPARTMENT d ON a.DEPARTMENT_ID = d.DEPARTMENT_ID  "
				+ "WHERE a.ACCOUNT_ID = :crsAccountId";
		//sql查出來的 ACCOUNT_ID, USER_NAME, DEPARTMENT_ID, DEPARTMENT_NAME, 
		//可以放到下面insert敘述裡面的CRSACCOUNTID, CRSUSERNAME, DEPARTMENTID, DEPARTMENT
	    Query query = manager.createNativeQuery(sql)
	            .setParameter("crsAccountId", crsAccountId);
	    Object[] result = (Object[]) query.getSingleResult();
	    String crsAccountId_fromQuery = result[0].toString();
	    String crsUsername_fromQuery = result[1].toString();
	    String departmentId_fromQuery = result[2].toString();
	    String departmentName_fromQuery = result[3].toString();
		
	    //insert敘述
	    Date date = new Date();
	    StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".MOACCOUNT_MAP_ACCOUNT ma");
		sb.append(" (FUNCTIONID, FUNCTIONTITLE, MODEPTNO, CRSACCOUNTID, CRSUSERNAME, DEPARTMENTID, DEPARTMENT, CREATEDATE, UPDATEDATE)");
		sb.append(" VALUES(:functionId, ");
		//FUNCTIONTITLE
		sb.append(" :functionTitle, ");
		//MODEPTNO
		if(!departmentId.contains("back"))//在dev,uat可能有一些momo部門名稱有加上_back
			departmentId = departmentId.split("_")[0];
		else
			departmentId = departmentId.split("_")[0]+"_back";
		sb.append(" :departmentId, ");
		 // CRSACCOUNTID, CRSUSERNAME, DEPARTMENTID, DEPARTMENT
		sb.append(" :crsAccountId_fromQuery, ")
	    .append(" :crsUsername_fromQuery, ")
	    .append(" :departmentId_fromQuery, ")
	    .append(" :departmentName_fromQuery, ")
	    .append(" :createdDate, ")
	    .append(" :updatedDate)");
	    
	    // 寫入此筆資料
	    Query insertQuery = manager.createNativeQuery(sb.toString())
	    .setParameter("functionId", functionId)
	    .setParameter("functionTitle", functiontitle)
	    .setParameter("departmentId", departmentId)
        .setParameter("crsAccountId_fromQuery", crsAccountId_fromQuery)
        .setParameter("crsUsername_fromQuery", crsUsername_fromQuery)
        .setParameter("departmentId_fromQuery", departmentId_fromQuery)
        .setParameter("departmentName_fromQuery", departmentName_fromQuery)
        .setParameter("createdDate", date)
        .setParameter("updatedDate", date);
	    
	    insertQuery.executeUpdate();
	}

	private String composeSql(MoAccountMapAccountConditionVo condition, boolean isCount) {
		//解Checkmarx的高風險SQL Injection
	    StringBuilder sb = new StringBuilder();

	    sb.append("SELECT");

	    if (isCount) {
	        sb.append(" COUNT(*)");
	    } else {
	        sb.append(" ma.FUNCTIONID AS functionId, ma.FUNCTIONTITLE AS functionTitle, ")
	                .append("ma.MODEPTNO AS moDeptNo, ma.CRSACCOUNTID AS crsAccountId, ")
	                .append("ma.CRSUSERNAME AS crsUserName, ma.DEPARTMENTID AS departmentId, ")
	                .append("ma.DEPARTMENT AS department, ma.CREATEDATE AS createDate, ma.UPDATEDATE AS updateDate");
	    }

	    sb.append(" FROM ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".MOACCOUNT_MAP_ACCOUNT ma");

	    // 添加搜尋邏輯
	    if (condition.getSearch() != null && !condition.getSearch().isEmpty()) {
	        sb.append(" WHERE (lower(ma.FUNCTIONID) LIKE :searchKeyword")
	          .append(" OR lower(ma.FUNCTIONTITLE) LIKE :searchKeyword")
	          .append(" OR lower(ma.MODEPTNO) LIKE :searchKeyword")
	          .append(" OR lower(ma.CRSUSERNAME) LIKE :searchKeyword")
	          .append(" OR lower(ma.DEPARTMENT) LIKE :searchKeyword)");
	    }

	    // 添加排序邏輯
	    if (condition.getName() != null && condition.getOrder() != null) {
	        // 避免直接使用用戶輸入，對排序欄位進行驗證
	        String orderByField = sanitizeOrderByField(condition.getName());
	        String order = sanitizeOrder(condition.getOrder());
	        if (orderByField != null && order != null) {	        	
	            sb.append(" ORDER BY ma.").append(orderByField).append(" ").append(order);
	        }
	    }

	    return sb.toString();
	}
	
	private String sanitizeOrderByField(String field) {
		//解Checkmarx的高風險SQL Injection
	    List<String> validFields = Arrays.asList("FUNCTIONID", "FUNCTIONTITLE", "MODEPTNO", "CRSACCOUNTID", "CRSUSERNAME", "DEPARTMENTID", "DEPARTMENT", "CREATEDATE", "UPDATEDATE");
	    return validFields.stream()
	        .filter(validField -> validField.equalsIgnoreCase(field))
	        .findFirst()
	        .orElse(null);
	}
	
	private String sanitizeOrder(String order) {
		//解Checkmarx的高風險SQL Injection
	    List<String> validFields = Arrays.asList("ASC", "DESC");
	    
	    return validFields.stream()
		        .filter(validField -> validField.equalsIgnoreCase(order))
		        .findFirst()
		        .orElse(null);

	}

}
