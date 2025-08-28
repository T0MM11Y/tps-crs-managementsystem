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
import com.twm.mgmt.model.serialCampaign.SerialCampaignSettingVo;
import com.twm.mgmt.persistence.dao.SerialCampaignDetailDao;
import com.twm.mgmt.persistence.dto.SerialCampaignDto;
import com.twm.mgmt.persistence.dto.SerialCampaignWhileListDto;

@Repository
public class SerialCampaignDetailDaoImpl implements SerialCampaignDetailDao {

	@PersistenceContext(unitName = MoDbConfig.PERSISTENCE_UNIT)
	private EntityManager manager;

	@Override
	public List findReward(SerialCampaignSettingVo serialCampaignSettingVo,Long accountId) {
		// 畫面輸入條件
		String campaignMainName = "序號發幣";
		Long createAccountId = accountId;
		String rewardMonthFilter = serialCampaignSettingVo.getRewardDate();
		String campaignNameFilter = serialCampaignSettingVo.getCampaignName();
		String statusFilter = serialCampaignSettingVo.getStatus();
		String projectCodeFilter = serialCampaignSettingVo.getProjectCode();
		BigDecimal reportIdFilter = serialCampaignSettingVo.getReportId();
		String reportStatusDateFilter = serialCampaignSettingVo.getUpdateDate();
		// 1. 建立 SQL Builder
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ( -- 畫面輸入條件 專案代碼 才需要\n")
		  .append("  SELECT  -- DISTINCT  \n")
		  .append("    CASE  -- ***第一欄的按鈕\n")
		  .append("      WHEN srd.DELAY = '發幣活動已逾期' THEN '' -- 不要有按鈕 \n")
		  .append("      WHEN rr.STATUS = 'WAIT_MOMO_EVENT_NO' THEN '新增發幣活動資訊' \n")
		  .append("      ELSE '查看' -- 其餘情境\n")
		  .append("    END AS Button,\n")
		  .append("    rr.REPORT_ID,                    -- 系統單號\n")
		  .append("    LISTAGG(snp.PROJECT_CODE, '/') WITHIN GROUP (ORDER BY snp.PROJECT_CODE) AS PROJECT_CODE,  -- 專案代碼(AC088/AC197/...)\n")
		  .append("    NVL2(rr.reward_date, TO_CHAR(rr.reward_date, 'YYYY/MM'),'--') AS reward_month,  -- 發幣月份\n")
		  .append("    cd.campaign_name,               -- 發幣活動名稱\n")
		  .append("    cd.campaign_detail_id,\n")
		  .append("    CASE  -- 申請類型\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 0 AND cat.rt = 0 THEN 'AQ'\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 1 AND cat.rt = 0 THEN 'AQ / NP'\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 0 AND cat.rt = 1 THEN 'AQ / RT'\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 1 AND cat.rt = 1 THEN 'AQ / NP / RT'\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 0 AND cat.rt = 0 THEN ' '\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 1 AND cat.rt = 0 THEN 'NP'\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 0 AND cat.rt = 1 THEN 'RT'\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 1 AND cat.rt = 1 THEN 'NP / RT'\n")
		  .append("    END AS Apply_Type,\n")
		  .append("    NVL2(rr.UPDATE_DATE, TO_CHAR(rr.UPDATE_DATE, 'yyyy/mm/dd'),'--') AS REPORT_STATUS_DATE,  -- 活動資訊狀態時間\n")
		  .append("    CASE  -- 活動資訊設定狀態\n")
		  .append("      WHEN srd.DELAY = '發幣活動已逾期' THEN '已處理'\n")
		  .append("      WHEN rr.STATUS = 'WAIT_MOMO_EVENT_NO' THEN '未建立'\n")
		  .append("      WHEN rr.STATUS = 'CREATING' THEN '建立中'\n")
		  .append("      WHEN rr.STATUS = 'CREATED' THEN '已建立'\n")
		  .append("      WHEN rr.STATUS = 'WAIT_REWARD' THEN '已匹配'\n")
		  .append("      WHEN rr.STATUS = 'DONE_REWARD' THEN '已發送'\n")
		  .append("    END AS status,\n")
		  .append("    NVL(srd.DELAY, '') AS DELAY,     -- 活動資訊備註欄\n")
		  .append("    cd.PROJECT_TYPE,                 -- 用戶標籤 CBG,EBG,SMG\n")
		  .append("    smcd.EVENTNO,                    -- 活動代碼\n")
		  .append("    smcd.EVENTDTID                   -- 活動明細編號\n")
		  .append("  FROM momoapi.SERIAL_REWARD_REPORT rr\n")
		  .append("  LEFT JOIN momoapi.SERIAL_CAMPAIGN_DETAIL cd ON rr.CAMPAIGN_DETAIL_ID = cd.CAMPAIGN_DETAIL_ID\n")
		  .append("  LEFT JOIN momoapi.CAMPAIGN_MAIN cm ON cm.CAMPAIGN_MAIN_ID = cd.CAMPAIGN_MAIN_ID\n")
		  .append("  LEFT JOIN (\n")
		  .append("    SELECT CAMPAIGN_DETAIL_ID, PROJECT_CODE\n")
		  .append("    FROM momoapi.SERIAL_NPLUS_PROJECT\n")
		  .append("    GROUP BY CAMPAIGN_DETAIL_ID, PROJECT_CODE\n")
		  .append("  ) snp ON rr.CAMPAIGN_DETAIL_ID = snp.CAMPAIGN_DETAIL_ID\n")
		  .append("  LEFT JOIN momoapi.SERIAL_CAMPAIGN_APPLY_TYPE cat ON rr.CAMPAIGN_DETAIL_ID = cat.CAMPAIGN_DETAIL_ID\n")
		  .append("  LEFT JOIN momoapi.SERIAL_REWARD_DELAY srd ON rr.REPORT_ID = srd.REPORT_ID\n")
		  .append("  LEFT JOIN momoapi.SERIAL_MOMO_CREATE_DETAIL smcd ON rr.REPORT_ID = smcd.REPORT_ID\n")
		  .append("  WHERE cm.CAMPAIGN_MAIN_NAME = :campaignMainName\n")
		  .append("    AND cd.CREATE_ACCOUNT = :createAccountId\n");
		  
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("campaignMainName", campaignMainName);
			paramMap.put("createAccountId", createAccountId);
			
			// 2. 用 Map 儲存對應的參數

			// 動態篩選：報表單號
			if (reportIdFilter != null) {
				sb.append(" AND rr.REPORT_ID = :reportId\n");
				paramMap.put("reportId", reportIdFilter);
			}

			// 動態篩選：活動名稱
			if (StringUtils.isNotBlank(campaignNameFilter)) {
				sb.append(" AND cd.campaign_name LIKE :campaignName\n");
				// 也可以 trim() 再加 %，避免前後空白干擾
				paramMap.put("campaignName", "%" + campaignNameFilter.trim() + "%");
			}

			// 動態篩選：發幣月份
			if (StringUtils.isNotBlank(rewardMonthFilter)) {
				sb.append(" AND TO_CHAR(rr.reward_date,'yyyy/mm') = :rewardMonth\n");
				paramMap.put("rewardMonth", rewardMonthFilter.trim().replace("-", "/"));
			}

			// 動態篩選：狀態
			if (StringUtils.isNotBlank(statusFilter)) {
				switch (statusFilter.trim()) {
				case "1":
					statusFilter = "WAIT_MOMO_EVENT_NO";
					break;
				case "2":
					statusFilter = "CREATING";
					break;
				case "3":
					statusFilter = "CREATED";
					break;
				case "4":
					statusFilter = "WAIT_REWARD";
					break;
				case "5":
					statusFilter = "DONE_REWARD";
					break;
				default:
					/* 留空或錯誤處理 */ break;
				}

				sb.append(" AND rr.STATUS = :status\n"); // 修正：不要寫 IS =
				paramMap.put("status", statusFilter.trim());
			}

			// 動態篩選：狀態日期
			if (StringUtils.isNotBlank(reportStatusDateFilter)) {
				sb.append(" AND TO_CHAR(rr.UPDATE_DATE,'yyyy/mm/dd') = :reportStatusDate\n");
				paramMap.put("reportStatusDate", reportStatusDateFilter.trim().replace("-", "/"));
			}

			
		  sb.append("  AND ( rr.STATUS = 'WAIT_MOMO_EVENT_NO' OR ( rr.STATUS IN ('CREATING','CREATED','WAIT_REWARD','DONE_REWARD') AND smcd.REPORT_ID IS NOT NULL ) OR ( rr.STATUS  = 'DONE_REWARD' AND srd.REPORT_ID IS NOT NULL ) )\n")
		  .append("    AND rr.TOTAL_REWARD_USERS > 0\n")
		  .append("  GROUP BY\n")
		  .append("    CASE  -- 第一欄的按鈕\n")
		  .append("      WHEN srd.DELAY = '發幣活動已逾期' THEN ''\n")
		  .append("      WHEN rr.STATUS = 'WAIT_MOMO_EVENT_NO' THEN '新增發幣活動資訊'\n")
		  .append("      ELSE '查看'\n")
		  .append("    END,\n")
		  .append("    rr.REPORT_ID,\n")
		  .append("    cd.campaign_name,\n")
		  .append("    cd.campaign_detail_id,\n")
		  .append("    CASE\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 0 AND cat.rt = 0 THEN 'AQ'\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 1 AND cat.rt = 0 THEN 'AQ / NP'\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 0 AND cat.rt = 1 THEN 'AQ / RT'\n")
		  .append("      WHEN cat.aq = 1 AND cat.np = 1 AND cat.rt = 1 THEN 'AQ / NP / RT'\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 0 AND cat.rt = 0 THEN ' '\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 1 AND cat.rt = 0 THEN 'NP'\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 0 AND cat.rt = 1 THEN 'RT'\n")
		  .append("      WHEN cat.aq = 0 AND cat.np = 1 AND cat.rt = 1 THEN 'NP / RT'\n")
		  .append("    END,\n")
		  .append("    NVL2(rr.reward_date, TO_CHAR(rr.reward_date, 'YYYY/MM'),'--'),\n")
		  .append("    NVL2(rr.UPDATE_DATE, TO_CHAR(rr.UPDATE_DATE, 'yyyy/mm/dd'),'--'),\n")
		  .append("    CASE\n")
		  .append("      WHEN srd.DELAY = '發幣活動已逾期' THEN '已處理'\n")
		  .append("      WHEN rr.STATUS = 'WAIT_MOMO_EVENT_NO' THEN '未建立'\n")
		  .append("      WHEN rr.STATUS = 'CREATING' THEN '建立中'\n")
		  .append("      WHEN rr.STATUS = 'CREATED' THEN '已建立'\n")
		  .append("      WHEN rr.STATUS = 'WAIT_REWARD' THEN '已匹配'\n")
		  .append("      WHEN rr.STATUS = 'DONE_REWARD' THEN '已發送'\n")
		  .append("    END,\n")
		  .append("    NVL(srd.DELAY, ''),\n")
		  .append("    cd.PROJECT_TYPE,\n")
		  .append("    smcd.EVENTNO,\n")
		  .append("    smcd.EVENTDTID\n")
		  .append("  ORDER BY reward_month DESC, Button ASC, REPORT_STATUS_DATE DESC\n")
		  .append(")-- 畫面輸入條件 專案代碼 才需要\n");
			// 動態篩選：專案代碼
			if (StringUtils.isNotBlank(projectCodeFilter)) {
				sb.append("		WHERE project_code LIKE :projectCode");
				paramMap.put("projectCode", "%" + projectCodeFilter.trim() + "%");
			}

		// 2. 建立 NativeQuery 並 unwrap
			Query query = manager
				    .createNativeQuery(sb.toString())
				    .unwrap(NativeQueryImpl.class)
				    .setResultTransformer(Transformers.aliasToBean(SerialCampaignDto.class))
				    // 下面依序對應 SELECT 的各別名
				    .addScalar("BUTTON",            StandardBasicTypes.STRING)
				    .addScalar("REPORT_ID",         StandardBasicTypes.BIG_DECIMAL)
				    .addScalar("PROJECT_CODE",      StandardBasicTypes.STRING)
				    .addScalar("REWARD_MONTH",      StandardBasicTypes.STRING)
				    .addScalar("CAMPAIGN_NAME",     StandardBasicTypes.STRING)
				    .addScalar("CAMPAIGN_DETAIL_ID",StandardBasicTypes.BIG_DECIMAL)
				    .addScalar("APPLY_TYPE",        StandardBasicTypes.STRING)
				    .addScalar("REPORT_STATUS_DATE",StandardBasicTypes.STRING)
				    .addScalar("STATUS",            StandardBasicTypes.STRING)
				    .addScalar("DELAY",             StandardBasicTypes.STRING)
				    .addScalar("PROJECT_TYPE",      StandardBasicTypes.STRING)
				    .addScalar("EVENTNO",           StandardBasicTypes.STRING)
				    .addScalar("EVENTDTID",         StandardBasicTypes.BIG_DECIMAL);

		for (Entry<String, Object> entry : paramMap.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return query.getResultList();
	}

}
