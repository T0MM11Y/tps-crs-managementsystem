package com.twm.mgmt.persistence.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import org.springframework.stereotype.Repository;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import com.twm.mgmt.config.MoDbConfig;
import com.twm.mgmt.persistence.dao.SerialCampaignWhileListDao;

import com.twm.mgmt.persistence.dto.SerialCampaignWhileListDto;
import com.twm.mgmt.utils.DateUtilsEx;

@Repository
public class SerialCampaignWhileListDaoImpl implements SerialCampaignWhileListDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@Override
	public List<SerialCampaignWhileListDto> findWhileList(Map<String, String> requestData, Long accountId) {
		String sql = findWhileListSql(requestData);
		Map<String, Object> params = findWhileListParams(requestData, accountId);
		Query query = null;
		query = manager.createNativeQuery(sql).unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.aliasToBean(SerialCampaignWhileListDto.class))
				.addScalar("LEVEL_NO", StandardBasicTypes.STRING).addScalar("BATCH_ID", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("CAMPAIGN_STATUS", StandardBasicTypes.STRING)
				.addScalar("REPORT_STATUS", StandardBasicTypes.STRING)
				.addScalar("BTN_STATUS", StandardBasicTypes.STRING)
				.addScalar("MOMO_EVENT_NO", StandardBasicTypes.STRING)
				.addScalar("SYS_ID", StandardBasicTypes.BIG_DECIMAL).addScalar("REWARD_DATE", StandardBasicTypes.DATE)
				.addScalar("PAY_ACCOUNT", StandardBasicTypes.STRING)
				.addScalar("ORDER_NUMBER", StandardBasicTypes.STRING)
				.addScalar("TOTAL_REWARD_USERS", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("TOTAL_REWARD_AMOUNT", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("APPLYNAME", StandardBasicTypes.STRING).addScalar("CAMPAIGN_NAME", StandardBasicTypes.STRING)
				.addScalar("CAMPAIGN_INFO", StandardBasicTypes.STRING).addScalar("SEND_DATE", StandardBasicTypes.STRING)
				.addScalar("SIGNNAME", StandardBasicTypes.STRING)
				.addScalar("FIRST_USER_NAME", StandardBasicTypes.STRING)
				.addScalar("SECOND_USER_NAME", StandardBasicTypes.STRING).addScalar("STATUS", StandardBasicTypes.STRING)
				.addScalar("CAMPAIGN_DETAIL_ID", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("APPLYID", StandardBasicTypes.LONG).addScalar("FIRST_ACCOUNT_ID", StandardBasicTypes.LONG).addScalar("SECOND_ACCOUNT_ID", StandardBasicTypes.LONG);
		

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return query.getResultList();
	}

	private Map<String, Object> findWhileListParams(Map<String, String> requestData, Long accountId) {
		Map<String, Object> params = new HashMap<>();
		params.put("USER_ID", accountId); // 假設 userId 是一個變量或者方法參數
		if (StringUtils.isNotBlank(requestData.get("applyId"))) {
			params.put("APPLY_ID", requestData.get("applyId")); // 假設 applyId 是一個變量或者方法參數
		}
		if(StringUtils.isNotBlank(requestData.get("sys_id"))) {
			params.put("sys_id", requestData.get("sys_id"));
		}
		if (StringUtils.isNotBlank(requestData.get("campaignName"))) {
			params.put("campaignName", "%"+requestData.get("campaignName").trim().toLowerCase()+"%");
		}
		if (StringUtils.isNotBlank(requestData.get("status"))) {
			params.put("status", requestData.get("status")); // 假設 status 是一個變量或者方法參數
		}
		if (StringUtils.isNotBlank(requestData.get("submitSendStartDate"))
				&& StringUtils.isNotBlank(requestData.get("submitSendEndDate"))) {
			params.put("submit_send_start_date", DateUtilsEx.startDate(requestData.get("submitSendStartDate"))); // 假設 submitSendStartDate
																							// 是一個變量或者方法參數
			params.put("submit_send_end_date", DateUtilsEx.endDate(requestData.get("submitSendEndDate")));
		}

		return params;
	}

	private String findWhileListSql(Map<String, String> requestData) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append("CASE ");
		sb.append("WHEN sab.L1_ACCOUNT = act_first.ACCOUNT_ID AND sab.L1_STATUS = 'WAIT_APPROVAL' THEN '1' ");
		sb.append("WHEN sab.L2_ACCOUNT = act_second.ACCOUNT_ID AND sab.L2_STATUS = 'WAIT_APPROVAL' THEN '2' ");
		sb.append("END AS level_no, ");
		sb.append("sab.APPROVAL_BATCH_ID AS batch_id, ");
		sb.append("scd.STATUS as campaign_status, ");
		sb.append("srr.STATUS as report_status, ");
		sb.append("CASE ");
		sb.append(
				"WHEN ((scd.STATUS = 'RECEIVED' AND srr.STATUS = 'WAIT_APPROVAL') AND sab.L1_STATUS = 'WAIT_APPROVAL') THEN '名單下載、催簽、撤回、簽核、查看' ");
		sb.append("WHEN (scd.STATUS = 'RECEIVED' AND srr.STATUS = 'APPROVAL_ING') THEN '名單下載、簽核、查看' ");
		sb.append("WHEN (scd.STATUS = 'INCOMPLETE' AND srr.STATUS = 'WAIT_SIGN_FOR') THEN '名單下載、編輯、查看' ");
		sb.append(
				"WHEN ((scd.STATUS = 'RECEIVED' AND srr.STATUS = 'WAIT_APPROVAL') AND sab.L1_STATUS = 'AGREE') THEN '名單下載、催簽、簽核、查看' ");		
		sb.append("WHEN (scd.STATUS = 'CAMPAIGN_ACTIVE_E' AND (srr.STATUS = 'WAIT_REWARD' OR srr.STATUS = 'DONE_REWARD')) THEN '名單下載、查看' ");
		sb.append("END AS btn_status, ");
		sb.append("srr.MOMO_EVENT_NO, ");
		sb.append("srr.REPORT_ID AS sys_id, ");
		sb.append("srr.REWARD_DATE, ");
		sb.append("srr.PAY_ACCOUNT, ");
		sb.append("srr.ORDER_NUMBER, ");
		sb.append("srr.TOTAL_REWARD_USERS, ");
		sb.append("srr.TOTAL_REWARD_AMOUNT, ");
		sb.append("act.USER_NAME AS applyName, ");
		sb.append("act.ACCOUNT_ID AS applyId, ");								
		sb.append("scd.CAMPAIGN_NAME, ");
		sb.append("scd.CAMPAIGN_INFO, ");
		sb.append("to_char(early_ssh.SEND_DATE , 'YYYY/MM/DD HH24:MI') AS SEND_DATE, ");
		sb.append("CASE ");
		sb.append("WHEN (sab.STATUS = 'WAIT_APPROVAL' AND sab.L1_STATUS = 'WAIT_APPROVAL') THEN act_first.USER_ID ");
		sb.append("WHEN (sab.STATUS = 'WAIT_APPROVAL' AND sab.L1_STATUS = 'AGREE') THEN act_second.USER_ID ");
		sb.append("WHEN (sab.STATUS = 'AGREE') THEN act_second.USER_ID ");
		sb.append("WHEN (sab.STATUS = 'REJECT' AND sab.L1_STATUS = 'REJECT') THEN act_first.USER_ID ");
		sb.append("WHEN (sab.STATUS = 'REJECT' AND sab.L1_STATUS = 'AGREE') THEN act_second.USER_ID ");
		sb.append("WHEN (sab.STATUS = 'EXPIRED' OR sab.STATUS = 'ROLLBACK') THEN '--' ");
		sb.append("END AS signName, ");
		sb.append("act_first.USER_NAME AS FIRST_USER_NAME, ");
		sb.append("act_second.USER_NAME AS SECOND_USER_NAME, ");		
		sb.append("act_first.ACCOUNT_ID AS FIRST_ACCOUNT_ID, ");
		sb.append("act_second.ACCOUNT_ID AS SECOND_ACCOUNT_ID, ");		
		sb.append("sab.STATUS, ");
		sb.append("scd.CAMPAIGN_DETAIL_ID ");
		sb.append("FROM MOMOAPI.SERIAL_CAMPAIGN_DETAIL scd ");
		sb.append("LEFT JOIN MOMOAPI.CAMPAIGN_MAIN cm ON cm.CAMPAIGN_MAIN_ID = scd.CAMPAIGN_MAIN_ID ");
		sb.append("LEFT JOIN MOMOAPI.SERIAL_REWARD_REPORT srr ON scd.campaign_detail_id = srr.campaign_detail_id ");
		sb.append("LEFT JOIN MOMOAPI.ACCOUNT act ON scd.CREATE_ACCOUNT = act.ACCOUNT_ID ");
		sb.append("LEFT JOIN (SELECT sad.CAMPAIGN_DETAIL_ID, ");
		sb.append("sad.APPROVAL_BATCH_ID, ");
		sb.append("sad.REPORT_ID, ");
		sb.append("sad.STATUS, ");
		sb.append("sad.CREATE_DATE, ");
		sb.append("ROW_NUMBER() OVER(PARTITION BY sad.CAMPAIGN_DETAIL_ID ORDER BY sad.CREATE_DATE DESC) AS RN ");
		sb.append(
				"FROM MOMOAPI.SERIAL_APPROVAL_DETAIL sad) latest_sad ON scd.CAMPAIGN_DETAIL_ID = latest_sad.CAMPAIGN_DETAIL_ID AND srr.report_id = latest_sad.report_id AND latest_sad.rn = 1 ");
		sb.append(
				"LEFT JOIN momoapi.SERIAL_APPROVAL_BATCH sab ON latest_sad.APPROVAL_BATCH_ID = sab.APPROVAL_BATCH_ID ");
		sb.append("LEFT JOIN momoapi.ACCOUNT act_first ON act_first.ACCOUNT_ID = sab.L1_ACCOUNT ");
		sb.append("LEFT JOIN momoapi.ACCOUNT act_second ON act_second.ACCOUNT_ID = sab.L2_ACCOUNT ");
		sb.append("LEFT JOIN (SELECT ssh.SEND_DATE, ");
		sb.append("ssh.SERIAL_REWARD_REPORT_ID, ");
		sb.append("ROW_NUMBER() OVER(PARTITION BY ssh.SERIAL_REWARD_REPORT_ID ORDER BY ssh.SEND_DATE) AS RN ");
		sb.append(
				"FROM MOMOAPI.SERIAL_SMS_HISTORY ssh) early_ssh ON srr.REPORT_ID = early_ssh.SERIAL_REWARD_REPORT_ID AND early_ssh.rn = 1 ");
		sb.append("WHERE cm.CAMPAIGN_KIND = 10 ");
		sb.append("AND sab.APPROVAL_TYPE = 'WHITELIST_REPORT' ");
		sb.append("AND (act.ACCOUNT_ID = :USER_ID OR sab.L1_ACCOUNT = :USER_ID OR sab.l2_account = :USER_ID) ");

		if (StringUtils.isNotBlank(requestData.get("applyId"))) {
			sb.append("AND act.ACCOUNT_ID = :APPLY_ID ");
		}
		if(StringUtils.isNotBlank(requestData.get("sys_id"))) {
			sb.append("AND srr.REPORT_ID = :sys_id ");
		}
		if (StringUtils.isNotBlank(requestData.get("campaignName"))) {
			sb.append("AND LOWER(scd.CAMPAIGN_NAME) like LOWER(").append(":campaignName").append(") ");
		}
		if (StringUtils.isNotBlank(requestData.get("status"))) {
			sb.append("AND sab.STATUS = :status ");
		}
		if (StringUtils.isNotBlank(requestData.get("submitSendStartDate"))
				&& StringUtils.isNotBlank(requestData.get("submitSendEndDate"))) {
			sb.append(
					"AND (early_ssh.SEND_DATE >= :submit_send_start_date and (early_ssh.SEND_DATE <= :submit_send_end_date)) ");
		}

		sb.append("ORDER BY sab.CREATE_DATE desc");
		return sb.toString();
	}

}
