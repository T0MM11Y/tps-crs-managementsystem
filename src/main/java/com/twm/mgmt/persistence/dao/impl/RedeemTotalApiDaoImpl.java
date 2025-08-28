package com.twm.mgmt.persistence.dao.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.model.account.RedeemTotalApiVo;
import com.twm.mgmt.persistence.dao.RedeemTotalApiDao;
import com.twm.mgmt.persistence.dto.RedeemTotalApiDto;

import io.micrometer.core.instrument.util.StringUtils;

@Repository
public class RedeemTotalApiDaoImpl implements RedeemTotalApiDao {
	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Override
	public List<RedeemTotalApiDto> findByCondition(RedeemTotalApiVo condition) {
	    String sql = composeSql(condition, false);
	    Query query = manager.createNativeQuery(sql)
	                         .unwrap(NativeQueryImpl.class)
	                         .setResultTransformer(Transformers.aliasToBean(RedeemTotalApiDto.class))
	                         .addScalar("moDeptNo", StandardBasicTypes.STRING)
	                         .addScalar("apiUrl", StandardBasicTypes.STRING)
	                         .addScalar("createDate", StandardBasicTypes.TIMESTAMP);

	    //解Checkmarx的高風險SQL Injection
	    // 綁定參數
	    if (condition.getSearch() != null && !condition.getSearch().isEmpty()) {
	        String searchKeyword = condition.getSearch().replace(" ", "").replace("    ", "").toLowerCase();
	        query.setParameter("searchKeyword", "%" + searchKeyword + "%");
	    }

	    query.setFirstResult((condition.getNumber() - 1) * condition.getSize());
	    query.setMaxResults(condition.getSize());
	    return query.getResultList();
	}

	@Override
	public Integer countByCondition(RedeemTotalApiVo condition) {
	    String sql = composeSql(condition, true);
	    Query query = manager.createNativeQuery(sql);

	    // 綁定參數
	    if (condition.getSearch() != null && !condition.getSearch().isEmpty()) {
	        String searchKeyword = condition.getSearch().replace(" ", "").replace("    ", "").toLowerCase();
	        query.setParameter("searchKeyword", "%" + searchKeyword + "%");
	    }

	    return ((BigDecimal) query.getSingleResult()).intValue();
	}
	@Override
	public void deleteRedeemTotalApi(String moDeptNo, String apiUrl) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".REDEEM_TOTAL_API ma");
		sb.append(" WHERE ma.MODEPTNO=:moDeptNo");
		sb.append(" AND   ma.APIURL=:apiUrl");
		String sql = sb.toString();
		Query query = manager.createNativeQuery(sql);
	    query.setParameter("moDeptNo", moDeptNo);
	    query.setParameter("apiUrl", apiUrl);
	    query.executeUpdate();
	}
	@Override
	public void insertRedeemTotalApi(String departmentId, String apiUrl) {
		
		//先查出該用戶的TWM部門資訊
		//String sql = "SELECT a.ACCOUNT_ID, a.USER_NAME, a.DEPARTMENT_ID, d.DEPARTMENT_NAME  "
			//	+ "FROM MOMOAPI.ACCOUNT a LEFT JOIN MOMOAPI.DEPARTMENT d ON a.DEPARTMENT_ID = d.DEPARTMENT_ID  "
			//	+ "WHERE a.ACCOUNT_ID = :crsAccountId";
		//sql查出來的 ACCOUNT_ID, USER_NAME, DEPARTMENT_ID, DEPARTMENT_NAME, 
		//可以放到下面insert敘述裡面的CRSACCOUNTID, CRSUSERNAME, DEPARTMENTID, DEPARTMENT
	    //Query query = manager.createNativeQuery(sql)
	    //        .setParameter("apiUrl", apiUrl);
	    //Object[] result = (Object[]) query.getSingleResult();
	   // String crsAccountId_fromQuery = result[0].toString();
	    //String crsUsername_fromQuery = result[1].toString();
	    //String departmentId_fromQuery = result[2].toString();
	    //String departmentName_fromQuery = result[3].toString();
		
	    //insert敘述
	    Date date = new Date();
	    StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".REDEEM_TOTAL_API ma");
		sb.append(" ( MODEPTNO, APIURL, CREATEDATE)");
		sb.append(" VALUES( ");
		
		//MODEPTNO
		if(!departmentId.contains("back"))//在dev,uat可能有一些momo部門名稱有加上_back
			departmentId = departmentId.split("_")[0];
		else
			departmentId = departmentId.split("_")[0]+"_back";
		sb.append(" :departmentId, ");
		 // APIURL
		sb.append(" :apiUrl_fromQuery, ")
	    .append(" :createdDate)");
	    
	    // 寫入此筆資料
	    Query insertQuery = manager.createNativeQuery(sb.toString())
	    .setParameter("departmentId", departmentId)
        .setParameter("apiUrl_fromQuery", apiUrl)
        .setParameter("createdDate", date);
	    
	    insertQuery.executeUpdate();
	}

	private String composeSql(RedeemTotalApiVo condition, boolean isCount) {
		//解Checkmarx的高風險SQL Injection
	    StringBuilder sb = new StringBuilder();

	    sb.append("SELECT");

	    if (isCount) {
	        sb.append(" COUNT(*)");
	    } else {
	        sb.append(" ma.MODEPTNO AS moDeptNo, ma.APIURL AS apiUrl, ")
	          .append("ma.CREATEDATE AS createDate");
	    }

	    sb.append(" FROM ").append(MoDbConfig.ACCOUNT_SCHEMA).append(".REDEEM_TOTAL_API ma");

	    // 添加搜尋邏輯
	    if (condition.getSearch() != null && !condition.getSearch().isEmpty()) {
	        sb.append(" WHERE (lower(ma.moDeptNo) LIKE :searchKeyword")
	          .append(" OR lower(ma.apiUrl) LIKE :searchKeyword)");
	    }

	    // 添加排序邏輯
	    List<String> allowedColumns = Arrays.asList("moDeptNo", "apiUrl", "createDate");
	    List<String> allowedOrders = Arrays.asList("ASC", "DESC");

	    String orderByColumn = allowedColumns.contains(condition.getName()) ? condition.getName() : "moDeptNo";
	    String order = (condition.getOrder() != null) ? condition.getOrder().toUpperCase() : "ASC";
	    String orderByDirection = allowedOrders.contains(order) ? order : "ASC";
	    
	    sb.append(" ORDER BY ma.");
	    
	    if("moDeptNo".equals(orderByColumn)) {
	    	sb.append("moDeptNo");
	    } else if ("apiUrl".equals(orderByColumn)) {
	    	sb.append("apiUrl");
	    } else if ("createDate".equals(orderByColumn)) {
	    	sb.append("createDate");
	    }

	    sb.append(" ");
	    
	    if("ASC".equals(orderByDirection)) {
	    	sb.append("ASC");
	    } else if ("DESC".equals(orderByDirection)) {
	    	sb.append("DESC");
	    }
	    
	    return sb.toString();
	}

}
