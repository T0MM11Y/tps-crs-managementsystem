package com.twm.mgmt.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.twm.mgmt.persistence.dao.impl.SerialCampaignWhileListDaoImpl;
import com.twm.mgmt.persistence.dto.SerialCampaignWhileListDto;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;
import com.twm.mgmt.persistence.entity.SerialApprovalBatchEntity;
import com.twm.mgmt.persistence.entity.SerialApprovalBatchHistoryEntity;
import com.twm.mgmt.persistence.entity.SerialApprovalDetailEntity;
import com.twm.mgmt.persistence.entity.SerialCampaignDetailEntity;
import com.twm.mgmt.persistence.entity.SerialCampaignFileEntity;
import com.twm.mgmt.persistence.entity.SerialDataEntity;
import com.twm.mgmt.persistence.entity.SerialRewardReportDetailEntity;
import com.twm.mgmt.persistence.entity.SerialRewardReportEntity;
import com.twm.mgmt.persistence.entity.SerialTransactionOfferHistoryEntity;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.persistence.repository.CampaignMainRepository;
import com.twm.mgmt.persistence.repository.SerialApprovalBatchHistoryRepository;
import com.twm.mgmt.persistence.repository.SerialApprovalBatchRepository;
import com.twm.mgmt.persistence.repository.SerialApprovalDetailRepository;
import com.twm.mgmt.persistence.repository.SerialCampaignDetailRepository;
import com.twm.mgmt.persistence.repository.SerialCampaignFileRepository;
import com.twm.mgmt.persistence.repository.SerialDataRepository;
import com.twm.mgmt.persistence.repository.SerialRewardReportDetailRepository;
import com.twm.mgmt.persistence.repository.SerialRewardReportRepository;
import com.twm.mgmt.persistence.repository.SerialTransactionOfferHistoryRepository;
import com.twm.mgmt.utils.DateUtilsEx;

import lombok.Data;

@Data
@Service
public class SerialCampaignWhileListService extends BaseService {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@Autowired
	private CampaignMainRepository CampaignMainRepo;

	@Autowired
	private SerialCampaignDetailRepository serialCampaignDetailRepo;

	@Autowired
	private SerialCampaignFileRepository serialCampaignFileRepo;

	@Autowired
	private SerialRewardReportRepository serialRewardReportRepo;

	@Autowired
	private SerialRewardReportDetailRepository serialRewardReportDetailRepo;

	@Autowired
	private SerialTransactionOfferHistoryRepository serialTransactionOfferHistoryRepo;

	@Autowired
	private SerialDataRepository serialDataRepo;

	@Autowired
	private SerialApprovalBatchRepository serialApprovalBatchRepo;

	@Autowired
	private SerialApprovalBatchHistoryRepository serialApprovalBatchHistoryRepo;

	@Autowired
	private SerialApprovalDetailRepository serialApprovalDetailRepo;

	@Autowired
	private AccountRepository accountRepo;

	@Transactional(rollbackFor = { Exception.class })
	public void completeSendApproval(Map<String, String> requestData, MultipartFile file)
			throws IOException, ParseException {
		BigDecimal campaignDetailId = serialCampaignDetailRepo.getSerialCampaignDetailId();
		log.info("SerialCampaignDetailEntity產生新的CAMPAIGN_DETAIL_ID:" + campaignDetailId);
		log.info("/serialCampaignWhileList/setting/completeSendApproval的請求參數={}", requestData);

		SerialCampaignDetailEntity serialCampaignDetailEntity;
		if (requestData.get("campaignDetailId").matches("\\d+")) {
			Optional<SerialCampaignDetailEntity> serialCampaignDetailEntityOptional = serialCampaignDetailRepo
					.findById(new BigDecimal(requestData.get("campaignDetailId")));
			serialCampaignDetailEntity = serialCampaignDetailEntityOptional.get();
			serialCampaignDetailEntity.setUpdateDate(new Date());
			serialCampaignDetailEntity.setUpdateAccount(getAccountId());

		} else {
			serialCampaignDetailEntity = SerialCampaignDetailEntity.builder().campaignDetailId(campaignDetailId)
					.campaignMainId(CampaignMainRepo.findByCampaignKind(BigDecimal.TEN).getCampaignMainId())
					.projectFilePath("filePath").signOffFilePath("filePath").rewardMonthRange(BigDecimal.ZERO)
					.rewardDayOfMonth(BigDecimal.ZERO).reportDayOfMonth(BigDecimal.ZERO).isChooseBest(0)
					.isCheckContract(0).isCheckMobileStatus(0).isBcAcctRewardSetting(0).isAutoMonthInfo(0)
					.isCheckConstruct(0).createDate(new Date()).createAccount(getAccountId())
					.projectType(requestData.get("projectType")).build();

		}
		serialCampaignDetailEntity.setStatus("RECEIVED");
		serialCampaignDetailEntity.setCampaignName(requestData.get("campaignName"));
		serialCampaignDetailEntity.setCampaignInfo(requestData.get("campaignInfo"));
		serialCampaignDetailRepo.save(serialCampaignDetailEntity);

		SerialCampaignFileEntity serialCampaignFileEntity;
		if (requestData.get("campaignDetailId").matches("\\d+")) {
			Optional<SerialCampaignFileEntity> serialCampaignFileEntityOptional = serialCampaignFileRepo
					.findById(new BigDecimal(requestData.get("campaignDetailId")));
			serialCampaignFileEntity = serialCampaignFileEntityOptional.get();
			serialCampaignFileEntity.setUpdateDate(new Date());
		} else {
			serialCampaignFileEntity = SerialCampaignFileEntity.builder()
					.campaignDetailId(serialCampaignDetailEntity.getCampaignDetailId())
					.fileName(file.getOriginalFilename()).createDate(new Date()).updateDate(new Date()).build();
		}
		serialCampaignFileEntity.setFileContent(new String(file.getBytes(), StandardCharsets.UTF_8).getBytes());
		serialCampaignFileRepo.save(serialCampaignFileEntity);

		Pair<Date, Date> validityDates = extractValidityDates(file);

		SerialRewardReportEntity serialRewardReportEntity;
		if (requestData.get("reportId").matches("\\d+")) {
			Optional<SerialRewardReportEntity> serialRewardReportEntityOptional = serialRewardReportRepo
					.findById(new BigDecimal(requestData.get("reportId")));
			serialRewardReportEntity = serialRewardReportEntityOptional.get();
			serialRewardReportEntity.setUpdateDate(new Date());
		} else {
			serialRewardReportEntity = SerialRewardReportEntity.builder()
					.campaignDetailId(serialCampaignDetailEntity.getCampaignDetailId())
					// current
					// date
					.momoEventNo(requestData.get("momoEventNo")).momoFilename(file.getOriginalFilename())
					.orderNumber(requestData.get("orderNumber")).payAccount(requestData.get("payAccount"))
					.requisitioner(requestData.get("requisitioner")).requisitionUnit(requestData.get("requisitionUnit"))
					.createDate(new Date()) // current date
					// ... other fields ...
					.build();

		}
		serialRewardReportEntity.setCampaignInfo(requestData.get("campaignInfo"));
		serialRewardReportEntity.setStatus("WAIT_APPROVAL");
		serialRewardReportEntity.setTotalRewardUsers(new BigDecimal(requestData.get("totalRewardUsers")));
		serialRewardReportEntity.setTotalRewardAmount(new BigDecimal(requestData.get("totalRewardAmount")));
		serialRewardReportEntity.setRewardDate(DateUtilsEx.startDate(requestData.get("rewardDate").replace("/", "-")));
		serialRewardReportEntity.setSendApprovalDate(setStartOfDay(new Date()));
		serialRewardReportEntity.setAmountValidityStartDate(validityDates.getLeft());
		serialRewardReportEntity.setAmountValidityEndDate(validityDates.getRight());
		serialRewardReportRepo.save(serialRewardReportEntity);

		if (StringUtils.isNotBlank(requestData.get("reportId")) && requestData.get("reportId").matches("\\d+")
				&& StringUtils.isNotBlank(requestData.get("campaignDetailId"))
				&& requestData.get("campaignDetailId").matches("\\d+")) {
			deleteData(new BigDecimal(requestData.get("reportId")),
					new BigDecimal(requestData.get("campaignDetailId")));
		}

		InputStreamReader in = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
		try {

			BufferedReader reader = new BufferedReader(in);
			String line;
			reader.readLine(); // Skip header line
			int serialIdCounter = 1;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length > 3) {
					SerialRewardReportDetailEntity serialRewardReportDetailEntity = buildReportDetail(
							serialRewardReportEntity.getReportId(), serialCampaignDetailEntity.getCampaignDetailId(),
							values, serialIdCounter);
					serialRewardReportDetailRepo.save(serialRewardReportDetailEntity);
					SerialTransactionOfferHistoryEntity serialTransactionOfferHistoryEntity = createTransactionOfferHistory(
							serialRewardReportDetailEntity, values[2], requestData.get("campaignInfo"));
					createSerialData(serialRewardReportEntity.getReportId(), serialRewardReportDetailEntity,
							serialTransactionOfferHistoryEntity, values);
					serialIdCounter++;
				}
			}
		} finally {
			in.close();
		}

		SerialApprovalBatchEntity serialApprovalBatchEntity;
		if (requestData.get("approvalBatchId").matches("\\d+")) {
			Optional<SerialApprovalBatchEntity> serialApprovalBatchEntityOptional = serialApprovalBatchRepo
					.findById(new BigDecimal(requestData.get("approvalBatchId")));
			serialApprovalBatchEntity = serialApprovalBatchEntityOptional.get();
		} else {

			serialApprovalBatchEntity = SerialApprovalBatchEntity.builder()

					.build();
		}

		serialApprovalBatchEntity.setUpdateAccount(getAccountId());
		serialApprovalBatchEntity.setUpdateDate(new Date());
		serialApprovalBatchEntity.setCreateAccount(getAccountId());
		serialApprovalBatchEntity.setCreateDate(new Date());
		serialApprovalBatchEntity.setL2Account(Long.valueOf(requestData.get("signUser2")));
		serialApprovalBatchEntity.setL1Status("WAIT_APPROVAL");
		serialApprovalBatchEntity.setL1Account(Long.valueOf(requestData.get("signUser1")));
		serialApprovalBatchEntity.setGroupId(serialRewardReportEntity.getReportId().toString()); // Assuming groupId is																									// a String
		serialApprovalBatchEntity.setApprovalType("WHITELIST_REPORT");
		serialApprovalBatchEntity.setStatus("WAIT_APPROVAL");
		serialApprovalBatchEntity.setL1CommentInfo(null);
		serialApprovalBatchEntity.setL1CheckDate(null);
		serialApprovalBatchEntity.setL2CommentInfo(null);
		serialApprovalBatchEntity.setL2CheckDate(null);
		serialApprovalBatchEntity.setL2Status(null);
		serialApprovalBatchRepo.save(serialApprovalBatchEntity);

		SerialApprovalBatchHistoryEntity serialApprovalBatchHistoryEntity = SerialApprovalBatchHistoryEntity.builder()
				.approvalBatchId(serialApprovalBatchEntity.getApprovalBatchId())
				.approvalType(serialApprovalBatchEntity.getApprovalType()).status(serialApprovalBatchEntity.getStatus())
				.groupId(serialApprovalBatchEntity.getGroupId()).l1Account(serialApprovalBatchEntity.getL1Account())
				.l1Status(serialApprovalBatchEntity.getL1Status()).l2Account(serialApprovalBatchEntity.getL2Account())
				.createDate(new Date())
				.createAccount(getAccountId()).build();

		serialApprovalBatchHistoryRepo.save(serialApprovalBatchHistoryEntity);

		List<SerialApprovalDetailEntity> serialApprovalDetailEntityList = serialApprovalDetailRepo
				.findByApprovalBatchId(serialApprovalBatchEntity.getApprovalBatchId());
		SerialApprovalDetailEntity serialApprovalDetailEntity;
		List<SerialApprovalDetailEntity> serialApprovalDetailEntityListNew = new ArrayList<SerialApprovalDetailEntity>();
		if (serialApprovalDetailEntityList.isEmpty()) {
			serialApprovalDetailEntity = SerialApprovalDetailEntity.builder().build();
			serialApprovalDetailEntityListNew.add(serialApprovalDetailEntity);

		} else {
			for (SerialApprovalDetailEntity serialApprovalDetailEntity2 : serialApprovalDetailEntityList) {

				serialApprovalDetailEntityListNew.add(serialApprovalDetailEntity2);
			}
		}

		for (SerialApprovalDetailEntity serialApprovalDetailEntity3 : serialApprovalDetailEntityListNew) {
			serialApprovalDetailEntity3.setApprovalType(serialApprovalBatchEntity.getApprovalType());
			serialApprovalDetailEntity3.setApprovalBatchId(serialApprovalBatchEntity.getApprovalBatchId());
			serialApprovalDetailEntity3.setCampaignDetailId(serialCampaignDetailEntity.getCampaignDetailId());
			serialApprovalDetailEntity3.setReportId(serialRewardReportEntity.getReportId());
			serialApprovalDetailEntity3.setStatus(serialApprovalBatchEntity.getStatus());
			serialApprovalDetailEntity3.setCreateDate(new Date());
			serialApprovalDetailEntity3.setCreateAccount(getAccountId());
			serialApprovalDetailEntity3.setUpdateDate(new Date());
			serialApprovalDetailEntity3.setUpdateAccount(getAccountId());
		}

		serialApprovalDetailRepo.saveAll(serialApprovalDetailEntityListNew);
	}

	private Pair<Date, Date> extractValidityDates(MultipartFile file) throws IOException, ParseException {
		// Read the file and parse the dates
		InputStreamReader in = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
		try {

			BufferedReader reader = new BufferedReader(in);
			String line;
			// Skip the header line
			reader.readLine();

			// Read the first data line
			line = reader.readLine();
			if (line != null) {
				String[] values = line.split(",");
				if (values.length > 3) {
					String[] dateRange = values[3].split("~");
					if (dateRange.length == 2) {
						// SimpleDateFormat should be used according to your date format
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date startDate = dateFormat.parse(dateRange[0].trim());
						Date endDate = dateFormat.parse(dateRange[1].trim());

						// Set time to 00:00:00 for startDate and 23:59:59 for endDate
						startDate = setStartOfDay(startDate);
						endDate = setEndOfDay(endDate);

						return Pair.of(startDate, endDate);
					}
				}
			}
			return null;
		} finally {
			in.close();
		}
	}

	private Date setStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private Date setEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	private SerialRewardReportDetailEntity buildReportDetail(BigDecimal reportId, BigDecimal campaignDetailId,
			String[] values, int serialIdCounter) {
		return SerialRewardReportDetailEntity.builder().reportId(reportId).campaignDetailId(campaignDetailId)
				.subId(values[0]) // Assuming Sub ID is the first column
				.serialid(String.valueOf(serialIdCounter)).basicAmount(new BigDecimal(values[2])) // Assuming Amount is
																									// the third column
				.status("WAIT_REWARD").phoneNumber("-").contractId(BigDecimal.ZERO).transactionId(BigDecimal.ZERO)
				.acctTypeAmount(BigDecimal.ZERO).bdIdAmount(BigDecimal.ZERO).createDate(new Date()).build();
	}

	private SerialTransactionOfferHistoryEntity createTransactionOfferHistory(
			SerialRewardReportDetailEntity serialRewardReportDetailEntity, String amount, String campaignInfo) {
		SerialTransactionOfferHistoryEntity serialTransactionOfferHistoryEntity = SerialTransactionOfferHistoryEntity
				.builder().reportDetailId(serialRewardReportDetailEntity.getReportDetailId()).status("WAIT_OFFER")
				.subId(serialRewardReportDetailEntity.getSubId()).orderNote(campaignInfo).amount(new BigDecimal(amount))
				.isSendSms(BigDecimal.ONE).campaignKind(new BigDecimal(10)).createDate(new Date()).build();

		return serialTransactionOfferHistoryRepo.save(serialTransactionOfferHistoryEntity);
	}

	private void createSerialData(BigDecimal reportId, SerialRewardReportDetailEntity serialRewardReportDetailEntity,
			SerialTransactionOfferHistoryEntity serialTransactionOfferHistoryEntity, String[] values)
			throws ParseException {
		Date deadlineDate = parseDeadlineDate2(values[4]);
		SerialDataEntity serialDataEntity = SerialDataEntity.builder().serialRewardReportId(reportId)
				.serialRewardReportDetailId(serialRewardReportDetailEntity.getReportDetailId())
				.serialTransactionOfferId(serialTransactionOfferHistoryEntity.getTransactionOfferId()).subId(values[0]) // Assuming
																														// Sub
																														// ID
																														// is
																														// the
																														// first
																														// column
				.serialId(serialRewardReportDetailEntity.getSerialid()).serialNo(values[1]) // Assuming Serial No is the
																							// second column
				.plusAmount(new BigDecimal(values[2])) // Assuming Amount is the third column
				.shortUrl(values[6]) // Assuming Short URL is the fifth column
				.deadlineDate(deadlineDate).createDate(new Date()).createAccount(getAccountId().toString())
				.updateDate(new Date()).updateAccount(getAccountId().toString()).build();

		serialDataRepo.save(serialDataEntity);
	}

	private Date parseDeadlineDate(String dateString) throws ParseException {
		String[] dateRange = dateString.split("~");
		if (dateRange.length == 2) {
			String endDateString = dateRange[1].trim() + " 23:59:59";
			return dateFormat.parse(endDateString);
		}
		throw new ParseException("Invalid date format in CSV", 0);
	}
	
	private Date parseDeadlineDate2(String dateString) throws ParseException {		
		String endDateString = dateString.trim() + " 23:59:59";
		return dateFormat.parse(endDateString);
	}

	@Transactional
	public List<Object[]> findAccountsByAccountId() {
		log.info("序號簡訊白名單查詢的申請人的下拉式選單,用登入者accountId查詢:" + getAccountId());
		return accountRepo.findAccountsByAccountId(getAccountId());
	}

	@Transactional
	public List<SerialCampaignWhileListDto> findWhileList(Map<String, String> requestData) {
		log.info("/serialCampaignWhileList/findWhileList的請求參數=:{}" + "和登入者的accountId=" + getAccountId(),
				requestData);
		return serialCampaignDetailRepo.findWhileList(requestData, getAccountId());
	}

	@Transactional
	public byte[] downloadCampaignFile(BigDecimal campaignDetailId) throws IOException {
		log.info("/serialCampaignWhileList/downloadCampaignFile的campaignDetailId="+campaignDetailId);
		SerialCampaignFileEntity entity = serialCampaignFileRepo.findById(campaignDetailId).orElseThrow();

		ByteArrayInputStream inputStream = new ByteArrayInputStream(entity.getFileContent());
		CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new InputStreamReader(inputStream));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// Define the headers for the output CSV
		
		String headers = "Sub ID,\u91D1\u984D,\u6548\u671F\n";
		outputStream.writeBytes("\uFEFF".getBytes());
		outputStream.writeBytes(headers.getBytes());
		// outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
		CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT);
		//printer.printRecord("Sub ID", "金額", "效期");

		for (CSVRecord record : parser) {
			// Print each record based on the headers

			printer.printRecord(record.get(0), record.get(2), record.get(3));
		}

		printer.flush();
		log.info(outputStream.toString());
		return outputStream.toByteArray();
	}

	@Transactional(rollbackFor = { Exception.class })
	public void rollback(BigDecimal campaignDetailId, BigDecimal reportId, BigDecimal approvalBatchId) {
		log.info("/serialCampaignWhileList/rollback的campaignDetailId:={},reportId:={},approvalBatchId:={}",campaignDetailId,reportId,approvalBatchId);
		SerialCampaignDetailEntity serialCampaignDetailEntity = serialCampaignDetailRepo.findById(campaignDetailId)
				.orElseThrow(() -> new RuntimeException("Campaign Detail not found"));

		serialCampaignDetailEntity.setStatus("INCOMPLETE");
		serialCampaignDetailEntity.setUpdateDate(new Date());

		serialCampaignDetailRepo.save(serialCampaignDetailEntity);

		SerialRewardReportEntity reportEntity = serialRewardReportRepo.findById(reportId)
				.orElseThrow(() -> new RuntimeException("Report not found"));
		reportEntity.setStatus("WAIT_SIGN_FOR");
		reportEntity.setUpdateDate(new Date());
		serialRewardReportRepo.save(reportEntity);

		SerialApprovalBatchEntity batchEntity = serialApprovalBatchRepo.findById(approvalBatchId)
				.orElseThrow(() -> new RuntimeException("Approval batch not found"));
		batchEntity.setStatus("ROLLBACK");
		batchEntity.setL1Status("ROLLBACK");
		batchEntity.setL1CheckDate(new Date());
		batchEntity.setL2Status("-");
		batchEntity.setL2CheckDate(new Date());
		batchEntity.setUpdateDate(new Date());
		batchEntity.setUpdateAccount(getAccountId());
		serialApprovalBatchRepo.save(batchEntity);

		List<SerialApprovalDetailEntity> approvalDetails = serialApprovalDetailRepo
				.findByApprovalBatchId(approvalBatchId);
		for (SerialApprovalDetailEntity detail : approvalDetails) {
			detail.setStatus(batchEntity.getStatus());
			detail.setUpdateDate(new Date());
			detail.setUpdateAccount(getAccountId());
			serialApprovalDetailRepo.save(detail);
		}

		SerialApprovalBatchHistoryEntity historyEntity = new SerialApprovalBatchHistoryEntity();

		historyEntity.setApprovalBatchId(batchEntity.getApprovalBatchId());
		historyEntity.setApprovalType("WHITELIST_REPORT");
		historyEntity.setStatus(batchEntity.getStatus());
		historyEntity.setGroupId(batchEntity.getGroupId());
		historyEntity.setL1Account(batchEntity.getL1Account());
		historyEntity.setL1Status(batchEntity.getL1Status());
		historyEntity.setL1CheckDate(batchEntity.getL1CheckDate());
		historyEntity.setL2Account(batchEntity.getL2Account());
		historyEntity.setL2Status(batchEntity.getL2Status());
		historyEntity.setL2CheckDate(batchEntity.getL2CheckDate());
		historyEntity.setCreateDate(new Date());
		historyEntity.setCreateAccount(getAccountId());

		serialApprovalBatchHistoryRepo.save(historyEntity);

	}

	@Transactional
	public Map<String, Object> view(BigDecimal campaignDetailId, BigDecimal reportId) {
		log.info("/serialCampaignWhileList/view的campaignDetailId:={},reportId:={}",campaignDetailId,reportId);
		SerialCampaignDetailEntity campaignDetail = serialCampaignDetailRepo.findById(campaignDetailId).get();
		SerialRewardReportEntity rewardReport = serialRewardReportRepo.findById(reportId).get();

		Map<String, Object> result = new HashMap<>();
		result.put("serialCampaignDetail", campaignDetail);
		result.put("serialRewardReport", rewardReport);
		return result;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void sendApproval(BigDecimal campaignDetailId, BigDecimal reportId, String lEVEL_NO, BigDecimal bATCH_ID,
			String opinion, String commentInfo) {
		log.info("/serialCampaignWhileList/sendApproval的campaignDetailId:={},reportId:={},bATCH_ID:={},lEVEL_NO:={},opinion:={}");
		if ("1".equals(lEVEL_NO)) {
			if ("1".equals(opinion)) {

				SerialRewardReportEntity report = serialRewardReportRepo.findById(reportId)
						.orElseThrow(() -> new EntityNotFoundException("SerialRewardReport not found"));
				report.setStatus("WAIT_APPROVAL");
				report.setUpdateDate(new Date());
				serialRewardReportRepo.save(report);

				SerialApprovalBatchEntity batch = serialApprovalBatchRepo.findById(bATCH_ID)
						.orElseThrow(() -> new EntityNotFoundException("SerialApprovalBatch not found"));
				batch.setL1Status("AGREE");
				batch.setL1CommentInfo(commentInfo);
				batch.setL1CheckDate(new Date());
				batch.setL2Status("WAIT_APPROVAL");
				batch.setUpdateDate(new Date());
				batch.setUpdateAccount(getAccountId());
				serialApprovalBatchRepo.save(batch);

				SerialApprovalBatchHistoryEntity history = new SerialApprovalBatchHistoryEntity();
				history.setApprovalBatchId(batch.getApprovalBatchId());
				history.setApprovalType("WHITELIST_REPORT");
				history.setStatus(batch.getStatus());
				history.setGroupId(batch.getGroupId());
				history.setL1Account(getAccountId());
				history.setL1Status(batch.getL1Status());
				history.setL1CommentInfo(batch.getL1CommentInfo());
				history.setL1CheckDate(batch.getL1CheckDate());
				history.setL2Account(batch.getL2Account());
				history.setL2Status(batch.getL2Status());
				history.setCreateDate(new Date());
				history.setCreateAccount(getAccountId());
				serialApprovalBatchHistoryRepo.save(history);
			} else if ("2".equals(opinion)) {

				// Update SerialRewardReport
				SerialRewardReportEntity rewardReport = serialRewardReportRepo.findById(reportId)
						.orElseThrow(() -> new EntityNotFoundException("SerialRewardReport not found"));
				rewardReport.setStatus("WAIT_SIGN_FOR");
				rewardReport.setUpdateDate(new Date());
				serialRewardReportRepo.save(rewardReport);

				// Update SerialCampaignDetail
				SerialCampaignDetailEntity campaignDetail = serialCampaignDetailRepo.findById(campaignDetailId)
						.orElseThrow(() -> new EntityNotFoundException("SerialCampaignDetail not found"));
				campaignDetail.setStatus("INCOMPLETE");
				campaignDetail.setUpdateDate(new Date());
				serialCampaignDetailRepo.save(campaignDetail);

				// Update SerialApprovalBatch
				SerialApprovalBatchEntity batch = serialApprovalBatchRepo.findById(bATCH_ID)
						.orElseThrow(() -> new EntityNotFoundException("SerialApprovalBatch not found"));
				batch.setStatus("REJECT");
				batch.setL1Status("REJECT");
				batch.setL1CommentInfo(commentInfo);
				batch.setL1CheckDate(new Date());
				batch.setL2Status("-");
				batch.setL2CheckDate(new Date());
				batch.setUpdateDate(new Date());
				batch.setUpdateAccount(getAccountId());
				serialApprovalBatchRepo.save(batch);

				// Update all associated SerialApprovalDetail entries
				List<SerialApprovalDetailEntity> details = serialApprovalDetailRepo.findByApprovalBatchId(bATCH_ID);
				for (SerialApprovalDetailEntity detail : details) {
					detail.setStatus(batch.getStatus());
					detail.setUpdateDate(new Date());
					detail.setUpdateAccount(getAccountId());
					serialApprovalDetailRepo.save(detail);
				}

				SerialApprovalBatchHistoryEntity history = SerialApprovalBatchHistoryEntity.builder()
						.approvalBatchId(batch.getApprovalBatchId()).approvalType("WHITELIST_REPORT")
						.status(batch.getStatus()).groupId(batch.getGroupId()).l1Account(getAccountId())
						.l1Status(batch.getL1Status()).l1CommentInfo(batch.getL1CommentInfo())
						.l1CheckDate(new Date()).l2Account(batch.getL2Account())
						.l2Status(batch.getL2Status()).l2CheckDate(new Date())
						.createDate(new Date()).createAccount(getAccountId()).build();

				serialApprovalBatchHistoryRepo.save(history);
			}
		} else if ("2".equals(lEVEL_NO)) {
			if ("1".equals(opinion)) {

				// Update SerialApprovalBatch
				SerialApprovalBatchEntity batch = serialApprovalBatchRepo.findById(bATCH_ID)
						.orElseThrow(() -> new EntityNotFoundException("SerialApprovalBatch not found"));
				batch.setL2Status("AGREE");
				batch.setL2CommentInfo(commentInfo);
				batch.setL2CheckDate(new Date());
				batch.setStatus("AGREE");
				batch.setUpdateDate(new Date());
				batch.setUpdateAccount(getAccountId());
				serialApprovalBatchRepo.save(batch);

				// Update all associated SerialApprovalDetail entries
				List<SerialApprovalDetailEntity> details = serialApprovalDetailRepo.findByApprovalBatchId(bATCH_ID);
				for (SerialApprovalDetailEntity detail : details) {
					detail.setStatus(batch.getStatus());
					detail.setUpdateDate(new Date());
					detail.setUpdateAccount(getAccountId());
					serialApprovalDetailRepo.save(detail);
				}

				SerialApprovalBatchHistoryEntity history = SerialApprovalBatchHistoryEntity.builder()
						.approvalBatchId(batch.getApprovalBatchId()).approvalType("WHITELIST_REPORT")
						.status(batch.getStatus()).groupId(batch.getGroupId()).l1Account(batch.getL1Account())
						.l1Status(batch.getL1Status()).l1CommentInfo(batch.getL1CommentInfo())
						.l1CheckDate(batch.getL1CheckDate()).l2Account(batch.getL2Account())
						.l2Status(batch.getL2Status()).l2CommentInfo(batch.getL2CommentInfo())
						.l2CheckDate(batch.getL2CheckDate()).createDate(new Date())
						.createAccount(getAccountId()).build();

				serialApprovalBatchHistoryRepo.save(history);

				// Update SerialRewardReport
				Optional<SerialRewardReportEntity> rewardReportOpt = serialRewardReportRepo.findById(reportId);
				if (rewardReportOpt.isPresent()) {
					SerialRewardReportEntity rewardReport = rewardReportOpt.get();
					rewardReport.setStatus("WAIT_REWARD");
					rewardReport.setUpdateDate(new Date());
					serialRewardReportRepo.save(rewardReport);
				}

				// Update SerialCampaignDetail
				Optional<SerialCampaignDetailEntity> campaignDetailOpt = serialCampaignDetailRepo
						.findById(campaignDetailId);
				if (campaignDetailOpt.isPresent()) {
					SerialCampaignDetailEntity campaignDetail = campaignDetailOpt.get();
					campaignDetail.setStatus("CAMPAIGN_ACTIVE_E");
					campaignDetail.setUpdateDate(new Date());
					serialCampaignDetailRepo.save(campaignDetail);
				}
			} else if ("2".equals(opinion)) {
				///
				// Update SerialRewardReport
				SerialRewardReportEntity rewardReport = serialRewardReportRepo.findById(reportId)
						.orElseThrow(() -> new EntityNotFoundException("SerialRewardReport not found"));
				rewardReport.setStatus("WAIT_SIGN_FOR");
				rewardReport.setUpdateDate(new Date());
				serialRewardReportRepo.save(rewardReport);

				// Update SerialCampaignDetail
				SerialCampaignDetailEntity campaignDetail = serialCampaignDetailRepo.findById(campaignDetailId)
						.orElseThrow(() -> new EntityNotFoundException("SerialCampaignDetail not found"));
				campaignDetail.setStatus("INCOMPLETE"); // Assuming 'CHANGE_REMARK' field exists
				campaignDetail.setUpdateDate(new Date());
				serialCampaignDetailRepo.save(campaignDetail);

				// Update SerialApprovalBatch
				SerialApprovalBatchEntity batch = serialApprovalBatchRepo.findById(bATCH_ID)
						.orElseThrow(() -> new EntityNotFoundException("SerialApprovalBatch not found"));
				batch.setStatus("REJECT");
				batch.setL2Status("REJECT");
				batch.setL2CommentInfo(commentInfo);
				batch.setL2CheckDate(new Date());
				batch.setUpdateDate(new Date());
				batch.setUpdateAccount(getAccountId());
				serialApprovalBatchRepo.save(batch);

				// Update all associated SerialApprovalDetail entries
				List<SerialApprovalDetailEntity> details = serialApprovalDetailRepo.findByApprovalBatchId(bATCH_ID);
				for (SerialApprovalDetailEntity detail : details) {
					detail.setStatus(batch.getStatus());
					detail.setUpdateDate(new Date());
					detail.setUpdateAccount(getAccountId());
					serialApprovalDetailRepo.save(detail);
				}

				SerialApprovalBatchHistoryEntity history = SerialApprovalBatchHistoryEntity.builder()
						.approvalBatchId(batch.getApprovalBatchId()).approvalType("WHITELIST_REPORT")
						.status(batch.getStatus()).groupId(batch.getGroupId()).l1Account(batch.getL1Account())
						.l1Status(batch.getL1Status()).l1CommentInfo(batch.getL1CommentInfo())
						.l1CheckDate(batch.getL1CheckDate()).l2Account(batch.getL2Account())
						.l2Status(batch.getL2Status()).l2CommentInfo(batch.getL2CommentInfo())
						.l2CheckDate(new Date()).createDate(new Date())
						.createAccount(getAccountId()).build();

				serialApprovalBatchHistoryRepo.save(history);
			}
		}

	}

	@Transactional
	public Map<String, Object> editView(BigDecimal campaignDetailId, BigDecimal reportId, BigDecimal batchId) {
		log.info("/serialCampaignWhileList/setting的campaignDetailId:={},reportId:={},batchId:={}",campaignDetailId,reportId,batchId);
		Map<String, Object> result = new HashMap<>();
		result.put("SerialCampaignDetailEntity", serialCampaignDetailRepo.findById(campaignDetailId).get());
		result.put("SerialRewardReportEntity", serialRewardReportRepo.findById(reportId).get());
		result.put("SerialApprovalBatchEntity", serialApprovalBatchRepo.findById(batchId).get());
		return result;

	}

	private void deleteData(BigDecimal reportId, BigDecimal campaignDetailId) {
		// Delete SerialData
		deleteSerialData(reportId, campaignDetailId);

		// Delete SerialTransactionOfferHistory and SerialRewardReportDetail
		List<SerialRewardReportDetailEntity> reportDetails = serialRewardReportDetailRepo
				.findByReportIdAndCampaignDetailId(reportId, campaignDetailId);
		for (SerialRewardReportDetailEntity reportDetail : reportDetails) {
			deleteSerialTransactionOfferHistory(reportDetail.getReportDetailId());
		}

		// Delete SerialRewardReportDetail
		deleteSerialRewardReportDetail(reportId, campaignDetailId);
	}

	private void deleteSerialData(BigDecimal reportId, BigDecimal campaignDetailId) {
		List<SerialRewardReportDetailEntity> reportDetails = serialRewardReportDetailRepo
				.findByReportIdAndCampaignDetailId(reportId, campaignDetailId);
		for (SerialRewardReportDetailEntity reportDetail : reportDetails) {
			List<SerialTransactionOfferHistoryEntity> transactionOffers = serialTransactionOfferHistoryRepo
					.findByReportDetailId(reportDetail.getReportDetailId());
			for (SerialTransactionOfferHistoryEntity transactionOffer : transactionOffers) {
				serialDataRepo.deleteBySerialRewardReportIdAndSerialRewardReportDetailIdAndSerialTransactionOfferId(
						reportId, reportDetail.getReportDetailId(), transactionOffer.getTransactionOfferId());
			}
		}
	}

	private void deleteSerialTransactionOfferHistory(BigDecimal reportDetailId) {
		serialTransactionOfferHistoryRepo.deleteByReportDetailId(reportDetailId);
	}

	private void deleteSerialRewardReportDetail(BigDecimal reportId, BigDecimal campaignDetailId) {
		serialRewardReportDetailRepo.deleteByReportIdAndCampaignDetailId(reportId, campaignDetailId);
	}


	public void setApprovalIng(BigDecimal reportId) {
		SerialRewardReportEntity reportEntity = serialRewardReportRepo.findById(reportId)
				.orElseThrow(() -> new RuntimeException("Report not found"));
		if("WAIT_APPROVAL".equals(reportEntity.getStatus())) {
			reportEntity.setStatus("APPROVAL_ING");
		} else if ("APPROVAL_ING".equals(reportEntity.getStatus())) {
			reportEntity.setStatus("WAIT_APPROVAL");
		}
		
		serialRewardReportRepo.save(reportEntity);
		
	}

}
