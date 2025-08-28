package com.twm.mgmt.persistence.dao.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.twm.mgmt.persistence.dao.CheckbillDao;
import com.twm.mgmt.persistence.dto.CheckbillDto;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.StringUtilsEx;

@Repository
public class CheckbillDaoImpl implements CheckbillDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	private Encoder encode=ESAPI.encoder();
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CheckbillDto> findByCondition(CheckbillVo condition,List<String> momoDepartmentStr_List) {
		String sql = composeSql(condition,momoDepartmentStr_List, false);
		
		//System.out.println("SQLContext:"+sql);
		Map<String, Object> params = composeParams(condition,momoDepartmentStr_List);
		
		Query query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(CheckbillDto.class))
				.addScalar("departmentId", StandardBasicTypes.STRING)
				.addScalar("receivedate", StandardBasicTypes.STRING)
				.addScalar("quantity", StandardBasicTypes.STRING)
				.addScalar("momoney", StandardBasicTypes.STRING);
		
		
			for (Entry<String, Object> entry : params.entrySet()) {
				//System.out.println("params:"+entry.getKey()+", "+entry.getValue().toString());
				query.setParameter(entry.getKey(), entry.getValue());
			}			
		

//			query.setFirstResult((condition.getNumber() - 1) * condition.getSize());
			//
//			query.setMaxResults(condition.getSize());
	//		//System.out.println("findByCondition1:"+(condition.getNumber() - 1) * condition.getSize());
			//System.out.println("findByCondition1:"+(condition.getNumber() - 1) +", "+ condition.getSize());
			query.setFirstResult((condition.getNumber() - 1) * condition.getSize());
			query.setMaxResults(countByCondition(condition,momoDepartmentStr_List));
			//System.out.println("findByCondition2:"+(query.getResultList().size()));
			return query.getResultList();
	}



	private String composeSql(CheckbillVo condition,List<String> momoDepartmentStr_List, boolean isCount ) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT");
		
		if(isCount) {
			sb.append(" COUNT (trh.TRANSACTION_REDEEM_ID)  from TRANSACTION_REDEEM_HISTORY trh, MO_ACCOUNT moac");					
		}else {
			sb.append(" to_char(trh.MO_TX_TIME,'yyyy-mm-dd') as receivedate, count(to_char(trh.MO_TX_TIME,'yyyy-mm-dd')) as quantity, sum(trh.AMOUNT) as momoney,trh.DEPT_NO as departmentId  from TRANSACTION_REDEEM_HISTORY trh, MO_ACCOUNT moac");	
//			sb.append(" to_char(trh.MO_TX_TIME,'yyyy-mm-dd') as receivedate, trh.AMOUNT as momoney , trh.DEPT_NO as departmentId  from TRANSACTION_REDEEM_HISTORY trh, MO_ACCOUNT moac");	
		}
	

		sb.append(" where 1=1");
		
		sb.append(" and moac.DEPT_NO = trh.DEPT_NO");
		
		if(condition.getCheckpaymethod()!=null) {
		if (!(condition.getCheckpaymethod().equals(""))) {
			//System.out.println("aa0");

			sb.append(" and trh.CHARGE_TYPE  = :checkpaymethod");
		}
		}
		if (!(condition.getDepartmentId().equals(""))) {
			//System.out.println("aa1");
			sb.append(" and moac.DEPT_NO = :departmentId");
		}else {
			sb.append(" and moac.DEPT_NO in (:departmentId)");
		}


		//Receive
		if (StringUtilsEx.isNotBlank(condition.getReceiveStartDate()) && StringUtilsEx.isNotBlank(condition.getReceiveEndDate())) {
			//System.out.println("aa3Receive");
			sb.append(" AND trh.MO_TX_TIME BETWEEN :receiveStartDate AND :receiveEndDate");
		}
		//Cancel (其實不是cancel取消 而是銷帳日期)
		if (StringUtilsEx.isNotBlank(condition.getCancelStartDate()) && StringUtilsEx.isNotBlank(condition.getCancelEndDate())) {
			//System.out.println("aa3Cancel");
			sb.append(" AND trh.MO_TX_TIME BETWEEN :cancelStartDate AND :cancelEndDate");
		}
		
		if(isCount) {
//			sb.append(" COUNT (trh.TRANSACTION_REDEEM_ID)  from TRANSACTION_REDEEM_HISTORY trh, MO_ACCOUNT moac");					
		}else {
			
			
				sb.append(" AND trh.STATUS = 'SUCCESS_REDEEM' ");
		
			
			
			sb.append(" GROUP BY trh.DEPT_NO,TO_CHAR(trh.MO_TX_TIME,'yyyy-mm-dd') ");	
			sb.append(" ORDER BY departmentId,receivedate ");					
		}
		
		return sb.toString();
	}

	private Map<String, Object> composeParams(CheckbillVo condition,List<String> momoDepartmentStr_List) {
		Map<String, Object> params = new HashMap<>();
		
		if (!(condition.getCheckpaymethod().equals(""))) {
			//System.out.println("aa00:"+condition.getCheckpaymethod());
			params.put("checkpaymethod", Integer.valueOf(condition.getCheckpaymethod()));
		}
	

		if (!(condition.getDepartmentId().equals(""))) {
			//System.out.println("aa11:"+condition.getDepartmentId());
			
			String[] split = condition.getDepartmentId().split("_");
			
			params.put("departmentId", encode.encodeForHTML(split[0]));
			split=null;
		}else {
			params.put("departmentId", momoDepartmentStr_List);
		}

		
		if (StringUtilsEx.isNotBlank(condition.getReceiveStartDate()) && StringUtilsEx.isNotBlank(condition.getReceiveEndDate())) {
			//System.out.println("aa33getReceiveDate");
			params.put("receiveStartDate", DateUtilsEx.startDate(condition.getReceiveStartDate()));
			params.put("receiveEndDate", DateUtilsEx.endDate(condition.getReceiveEndDate()));
		}
		
		//(cancel其實不是cancel取消 而是銷帳日期)
		if (StringUtilsEx.isNotBlank(condition.getCancelStartDate()) && StringUtilsEx.isNotBlank(condition.getCancelEndDate())) {
			//System.out.println("aa33getCancelDate");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cs = Calendar.getInstance();
			Calendar ce = Calendar.getInstance();
			try{
			   cs.setTime(sdf.parse(condition.getCancelStartDate()));
			   ce.setTime(sdf.parse(condition.getCancelEndDate()));
			}catch(ParseException e){
			   e.printStackTrace();
			 }
			//Incrementing the date by 1 day
			cs.add(Calendar.DAY_OF_MONTH, -1);  
			ce.add(Calendar.DAY_OF_MONTH, -1); 
			String newDateS = sdf.format(cs.getTime());  
			String newDateE = sdf.format(ce.getTime());  

			params.put("cancelStartDate", DateUtilsEx.startDate(newDateS));//(cancel其實不是cancel取消 而是銷帳日期)
			params.put("cancelEndDate", DateUtilsEx.endDate(newDateE));//(cancel其實不是cancel取消 而是銷帳日期)
		}
		
		//System.out.println("composeParams2:"+(params.size()));

		return params;
	}

	@Override
	public Integer countByCondition(CheckbillVo condition,List<String> momoDepartmentStr_List) {
		String sql = composeSql(condition,momoDepartmentStr_List, true);

		Map<String, Object> params = composeParams(condition,momoDepartmentStr_List);

		Query query = manager.createNativeQuery(sql);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
//		return query.getResultList();
		return ((BigDecimal) query.getSingleResult()).intValue();
	}
	
	/**
	 * @param forTotal 這是為了BOMD營管處想要顯示   "總兌幣金額:$5,101/總取消兌幣金額:$453/兌幣累計:$4,648" 在核帳報表頁面
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CheckbillDto> findDetailByCondition(CheckbillVo condition,List<String> momoDeptNoList, String queryType) {
		String sql = composeDetailSql(condition,queryType);
		//System.out.println("SQL Detail:"+sql);
		Map<String, Object> params = composeParams(condition,momoDeptNoList);
		
		Query query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(CheckbillDto.class))
				.addScalar("COMPANY_NUMBER", StandardBasicTypes.STRING)
				.addScalar("COMPANY", StandardBasicTypes.STRING)
				.addScalar("INVOICE_NUMBER", StandardBasicTypes.STRING)
				.addScalar("DEPT_NO", StandardBasicTypes.STRING)
				.addScalar("DEPT_NAME", StandardBasicTypes.STRING)
				.addScalar("CHANNEL_NAME", StandardBasicTypes.STRING)
				.addScalar("STORE_ID", StandardBasicTypes.STRING)
				.addScalar("STORE_NAME", StandardBasicTypes.STRING)
				.addScalar("ORDER_NOTE", StandardBasicTypes.STRING)
				.addScalar("MO_CHARGE_ID", StandardBasicTypes.STRING)
				.addScalar("O_ORDER_NUMBER", StandardBasicTypes.STRING)
				.addScalar("T_ORDER_NUMBER", StandardBasicTypes.STRING)
				.addScalar("TWM_UUID", StandardBasicTypes.STRING)
				.addScalar("TWM_UID", StandardBasicTypes.STRING)
				.addScalar("ORDER_DATE", StandardBasicTypes.STRING)
				.addScalar("MO_TX_TIME", StandardBasicTypes.STRING)
				.addScalar("AMOUNT", StandardBasicTypes.STRING)
				.addScalar("MO_REFUND_TX_TIME", StandardBasicTypes.STRING)
				.addScalar("CANCEL_AMOUNT", StandardBasicTypes.STRING);//(cancel其實不是cancel取消 而是銷帳日期)
		
		
			for (Entry<String, Object> entry : params.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}			
			return query.getResultList();
	}
	
	/**
	 * 
	 * @param condition
	 * @param queryType 這是為了BOMD營管處想要顯示   "總兌幣金額:$5,101/總取消兌幣金額:$453/兌幣累計:$4,648" 在核帳報表頁面
	 * 	值如果是: totalRedeem是要為了核帳報表頁面顯示總兌幣,totalCancelRedeem是要為了核帳報表頁面顯示總取消兌幣, detail是為了明細報表,
	 * @return
	 */
	private String composeDetailSql(CheckbillVo condition, String queryType) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT DISTINCT");
		sb.append(" '97176270' AS COMPANY_NUMBER,");	
		sb.append(" '台灣大哥大股份有限公司' AS COMPANY,");
		sb.append(" '97176270' AS INVOICE_NUMBER,");
		sb.append(" trh.DEPT_NO AS DEPT_NO,");	
		sb.append(" dep.DEPARTMENT_NAME  AS DEPT_NAME,");
		sb.append(" trh.CHANNEL_NAME AS CHANNEL_NAME,");
		sb.append(" trh.STORE_ID AS STORE_ID,");	
		sb.append(" trh.STORE_NAME AS STORE_NAME,");
		sb.append(" trh.ORDER_NOTE AS ORDER_NOTE,");
		sb.append(" trh.MO_CHARGE_ID AS MO_CHARGE_ID,");	
		sb.append(" trh.ORDER_NUMBER AS O_ORDER_NUMBER,");
		sb.append(" trh.ORDER_NUMBER AS T_ORDER_NUMBER,");
		//sb.append(" trh.MOMO_UUID AS MOMO_UUID,");如有需要再打開
		sb.append(" trh.TWM_UUID AS TWM_UUID,");
		sb.append(" trh.TWM_UID AS TWM_UID,");
		sb.append(" trh.ORDER_DATE AS ORDER_DATE,");
		sb.append(" trh.MO_TX_TIME AS MO_TX_TIME,");	
		sb.append(" trh.AMOUNT AS AMOUNT,");
		sb.append(" trh.MO_REFUND_TX_TIME AS MO_REFUND_TX_TIME,");
		sb.append(" CASE\r\n" + 
				  "	WHEN trh.STATUS = 'SUCCESS_REDEEM' THEN '-' " + 
				  "	WHEN trh.STATUS = 'CANCEL_REDEEM' THEN TO_CHAR(trh.AMOUNT) " + 
				  " END CANCEL_AMOUNT ");	
		sb.append(" from MOMOAPI.TRANSACTION_REDEEM_HISTORY trh, MOMOAPI.MO_ACCOUNT moac, MOMOAPI.DEPARTMENT dep ");
		sb.append(" where");
		sb.append(" trh.STATUS != 'FAIL_REDEEM'");
		sb.append(" AND moac.DEPT_NO = trh.DEPT_NO");
		sb.append(" AND moac.DEPARTMENT_ID = dep.DEPARTMENT_ID");
		
		if(condition.getCheckpaymethod()!=null) {
			if (!(condition.getCheckpaymethod().equals(""))) {
				sb.append(" AND trh.CHARGE_TYPE  = :checkpaymethod");
			}
		}
		if (!(condition.getDepartmentId().equals(""))) {
			sb.append(" AND moac.DEPT_NO = :departmentId");
		}else {
			sb.append(" AND moac.DEPT_NO in (:departmentId)");
		}
		
		if(queryType.equals("detail")) { //為了明細報表
			//Receive
			if (StringUtilsEx.isNotBlank(condition.getReceiveStartDate()) && StringUtilsEx.isNotBlank(condition.getReceiveEndDate())) {
				sb.append(" AND ((trh.MO_TX_TIME BETWEEN :receiveStartDate AND :receiveEndDate )");
			}
			//Receive
			if (StringUtilsEx.isNotBlank(condition.getReceiveStartDate()) && StringUtilsEx.isNotBlank(condition.getReceiveEndDate())) {
				sb.append(" OR ( trh.MO_REFUND_TX_TIME BETWEEN :receiveStartDate AND :receiveEndDate ))");
			}
			//Cancel(其實不是cancel取消 而是銷帳日期)
			if (StringUtilsEx.isNotBlank(condition.getCancelStartDate()) && StringUtilsEx.isNotBlank(condition.getCancelEndDate())) {
				sb.append(" AND ((trh.MO_TX_TIME BETWEEN :cancelStartDate AND :cancelEndDate )");
			}
			//Cancel(其實不是cancel取消 而是銷帳日期)
			if (StringUtilsEx.isNotBlank(condition.getCancelStartDate()) && StringUtilsEx.isNotBlank(condition.getCancelEndDate())) {
				sb.append(" OR ( trh.MO_REFUND_TX_TIME BETWEEN :cancelStartDate AND :cancelEndDate ))");
			}
		}else if(queryType.equals("totalRedeem")){//totalRedeem 為了核帳報表 選擇BOMD 營管處 要顯示的總金額
			//Receive
			if (StringUtilsEx.isNotBlank(condition.getReceiveStartDate()) && StringUtilsEx.isNotBlank(condition.getReceiveEndDate())) {
				sb.append(" AND (trh.MO_TX_TIME BETWEEN :receiveStartDate AND :receiveEndDate )");
			}
			//Cancel(其實不是cancel取消 而是銷帳日期)
			if (StringUtilsEx.isNotBlank(condition.getCancelStartDate()) && StringUtilsEx.isNotBlank(condition.getCancelEndDate())) {
				sb.append(" AND (trh.MO_TX_TIME BETWEEN :cancelStartDate AND :cancelEndDate )");
			}
		}else if(queryType.equals("totalCancelRedeem")){//totalCancelRedeem 為了核帳報表 選擇BOMD 營管處 要顯示的總取消兌幣金額
			//Receive
			if (StringUtilsEx.isNotBlank(condition.getReceiveStartDate()) && StringUtilsEx.isNotBlank(condition.getReceiveEndDate())) {
				sb.append(" AND (trh.MO_REFUND_TX_TIME BETWEEN :receiveStartDate AND :receiveEndDate )");
			}
			//Cancel(其實不是cancel取消 而是銷帳日期)
			if (StringUtilsEx.isNotBlank(condition.getCancelStartDate()) && StringUtilsEx.isNotBlank(condition.getCancelEndDate())) {
				sb.append(" AND (trh.MO_REFUND_TX_TIME BETWEEN :cancelStartDate AND :cancelEndDate )");
			}
		}
		
		
		sb.append(" ORDER BY  DEPT_NO");					
		
		return sb.toString();
	}
	
	/**
	 * @param forTotal 這是為了BOMD營管處想要顯示   "總兌幣金額:$5,101/總取消兌幣金額:$453/兌幣累計:$4,648" 在核帳報表頁面
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String findCheckbillForBOMD(CheckbillVo condition, List<String> momoDeptNoList) {
		
		//藉由取得明細報表的sql來取得  取消兌幣  總金額
		List<CheckbillDto> dtosForCancel = findDetailByCondition(condition,momoDeptNoList,"totalCancelRedeem");
		int cancelSum = dtosForCancel.stream()
			    .mapToInt(dto -> {
			        String cancelAmount = dto.getCANCEL_AMOUNT();
			        if (cancelAmount.equals("-")) { //因為db裡面 取消兌幣是0時 會寫入一槓 -  
			            return 0; 
			        }
			        return Integer.parseInt(cancelAmount);
			    })
			    .sum();
		
		//藉由取得明細報表的sql(時間調整成不含取消兌幣的時間)來取得  兌幣  總金額)
		List<CheckbillDto> dtosForRedeem = findDetailByCondition(condition,momoDeptNoList,"totalRedeem");
		int redeemSum = dtosForRedeem.stream().mapToInt(dto -> Integer.parseInt(dto.getAMOUNT()))
                .sum();
		
		return String.format("總兌幣金額:$%d / 總取消兌幣金額:$%d / 兌幣累計:$%d", redeemSum, cancelSum, redeemSum - cancelSum);
		
	}
	
}
