package com.twm.mgmt.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.serialCampaign.MoMoSeqEventResponse;
import com.twm.mgmt.model.serialCampaign.ReportDetail;
import com.twm.mgmt.model.serialCampaign.SerialCampaignRequest;
import com.twm.mgmt.model.serialCampaign.SerialCampaignSettingVo;
import com.twm.mgmt.persistence.entity.SerialMomoCreateBatchEntity;
import com.twm.mgmt.persistence.entity.SerialMomoCreateBatchHistoryEntity;
import com.twm.mgmt.persistence.entity.SerialMomoCreateDetailEntity;
import com.twm.mgmt.persistence.repository.SerialCampaignDetailRepository;
import com.twm.mgmt.persistence.repository.SerialMomoCreateBatchHistoryRepository;
import com.twm.mgmt.persistence.repository.SerialMomoCreateBatchRepository;
import com.twm.mgmt.persistence.repository.SerialMomoCreateDetailRepository;
import com.twm.mgmt.persistence.repository.SerialRewardReportRepository;
import com.twm.mgmt.utils.DateUtilsEx;

import lombok.Data;

@Data
@Service
public class SerialCampaignService extends BaseService {

	@Autowired
	private SerialCampaignDetailRepository serialCampaignDetailRepo;

	@Autowired
	private SerialRewardReportRepository serialRewardReportRepository;

	@Autowired
	private SerialMomoCreateBatchRepository batchRepo;

	@Autowired
	private SerialMomoCreateBatchHistoryRepository historyRepo;

	@Autowired
	private SerialMomoCreateDetailRepository detailRepo;

	@Autowired
	private SerialRewardReportRepository rewardReportRepo;

	@Transactional
	public QueryResultVo findReward(SerialCampaignSettingVo serialCampaignSettingVo)
			throws NumberFormatException, Exception {
		log.info("Starting findReward with SerialCampaignSettingVo: {}", serialCampaignSettingVo);
		serialCampaignSettingVo.setAccountId(getAccountId());

		QueryResultVo resultVo = new QueryResultVo(serialCampaignSettingVo);
		log.info("Initialized QueryResultVo");
		List rewards = serialCampaignDetailRepo.findReward(serialCampaignSettingVo, getAccountId());
		log.info("Retrieved rewards from repository");
		resultVo.setResult(rewards);

		log.info("Completed findReward");
		return resultVo;
	}

	@Transactional
	public List<Object[]> findRewardSummaryByAccountId() {
		return serialRewardReportRepository.findRewardSummaryByAccountId(getAccountId());
	}

	/**
	 * 一次性建立 batch、history、detail，並更新 reward report
	 * 
	 * @param seqResp
	 */
	@Transactional
	public void createSerialCampaign(SerialCampaignRequest request, MoMoSeqEventResponse seqResp) {
		// 組 reportIds，過濾掉重複的 reportId
		String reportIds = request.getReportIdDetail().stream()
		        .map(ReportDetail::getReportId)
		        .distinct() // 過濾掉重複的 reportId
		        .map(String::valueOf)
		        .collect(Collectors.joining(","));

		// 組 amountCountMap
		String amountCountMap = request.getConditions().stream()
				.map(c -> c.getSeqDenoAmt().toPlainString() + ":" + c.getSeqDenoCount())
				.collect(Collectors.joining(","));

		// 在方法裡，先定義 formatter
		DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		// 解析日期
		LocalDateTime start = LocalDateTime.parse(request.getAmountValidityStartDate() + "000000", ymd);
		LocalDateTime end = LocalDateTime.parse(request.getAmountValidityEndDate() + "235959", ymd);

		// 3. 取第一筆的 eventDtId 給 batch、history
		Long firstEventDtId = seqResp.getData().get(0).getEventDtId().longValue();

		// 建 batch
		SerialMomoCreateBatchEntity batch = SerialMomoCreateBatchEntity.builder().eventNo(request.getEventNo())
				.eventDtId(firstEventDtId) // 若有明細 ID，請改為正確來源
				.reportIds(reportIds).status("CREATING").payAccount(request.getPayAccount())
				.orderNumber(request.getOrderNumber()).eventNoAmt(request.getEventNoAmount().longValue())
				.remainingAmt(request.getRemainingAmt().longValue())
				.requestAmount(request.getRequestAmount().longValue())
				.requestCount(request.getRequestCount().longValue()).amountCountMap(amountCountMap)
				.amountValidityStartDate(start).amountValidityEndDate(end).createDate(LocalDateTime.now())
				.createAccount(getAccountId()).type("序號發幣").build();
		SerialMomoCreateBatchEntity savedBatch = batchRepo.save(batch);

		// 建 history
		SerialMomoCreateBatchHistoryEntity history = SerialMomoCreateBatchHistoryEntity.builder()
				.momoCreateId(savedBatch.getMomoCreateId()).eventNo(savedBatch.getEventNo())
				.eventDtId(savedBatch.getEventDtId()).reportIds(savedBatch.getReportIds())
				.status(savedBatch.getStatus()).createDate(LocalDateTime.now()).type(savedBatch.getType()).build();
		historyRepo.save(history);

		// 建 detail
		LocalDateTime now = LocalDateTime.now();
		Long accountId = getAccountId();
		for (ReportDetail rd : request.getReportIdDetail()) {
			SerialMomoCreateDetailEntity detail = SerialMomoCreateDetailEntity.builder()
					.eventNo(savedBatch.getEventNo()).eventDtId(firstEventDtId).reportId(Long.valueOf(rd.getReportId()))
					.projectType(rd.getProjectType()).campaignName(rd.getCampaignName())
					.totalAmount(rd.getTotalAmount().longValue()).amount(rd.getAmount().longValue())
					.pendingUser(rd.getPendingUser().longValue()).createDate(now).createAccount(accountId).type("序號發幣")
					.build();
			detailRepo.save(detail);
		}

		// 更新 reward report
		Date updNow = new Date();
		Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
		for (ReportDetail rd : request.getReportIdDetail()) {
			rewardReportRepo.findById(new BigDecimal(rd.getReportId())).ifPresent(entity -> {
				entity.setStatus("CREATING");
				entity.setUpdateDate(updNow);
				entity.setMomoEventNo(request.getEventNo());
				entity.setOrderNumber(request.getOrderNumber());
				entity.setPayAccount(request.getPayAccount());
				entity.setAmountValidityStartDate(startDate);
				entity.setAmountValidityEndDate(endDate);
				entity.setRewardDate(Date.from(request.getRewardDate().atTime(LocalTime.of(00, 00, 00)) // 23:59:59
						.atZone(ZoneId.systemDefault()) // 本地時區
						.toInstant()));
				rewardReportRepo.save(entity);
			});
		}

	}

	public List<Map<String, Object>> getReportData(BigDecimal reportId, Date rewardDate)
			throws NumberFormatException, Exception {

		List<Map<String, Object>> results = serialRewardReportRepository.findReportsNative(getAccountId(), reportId,
				rewardDate);

		return results;
	}

	@Transactional
	public void updateAmountValidityDates(List<BigDecimal> reportIds, Date startDate, Date endDate) {
		Date adjustedStartDate = DateUtilsEx.getStartOfDay(startDate);
		Date adjustedEndDate = DateUtilsEx.getEndOfDay(endDate);
		serialRewardReportRepository.updateAmountValidityDates(adjustedStartDate, adjustedEndDate, reportIds);
	}
}
