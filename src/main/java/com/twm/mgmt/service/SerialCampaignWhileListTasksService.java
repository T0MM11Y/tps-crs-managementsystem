package com.twm.mgmt.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twm.mgmt.persistence.entity.SerialApprovalBatchEntity;
import com.twm.mgmt.persistence.entity.SerialApprovalBatchHistoryEntity;
import com.twm.mgmt.persistence.entity.SerialApprovalDetailEntity;
import com.twm.mgmt.persistence.entity.SerialCampaignDetailEntity;
import com.twm.mgmt.persistence.entity.SerialRewardReportEntity;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.persistence.repository.SerialApprovalBatchHistoryRepository;
import com.twm.mgmt.persistence.repository.SerialApprovalBatchRepository;
import com.twm.mgmt.persistence.repository.SerialApprovalDetailRepository;
import com.twm.mgmt.persistence.repository.SerialCampaignDetailRepository;
import com.twm.mgmt.persistence.repository.SerialRewardReportRepository;

@Service
public  class SerialCampaignWhileListTasksService extends BaseService  {

	



	@Autowired
	private SerialCampaignDetailRepository serialCampaignDetailRepo;

	@Autowired
	private SerialRewardReportRepository serialRewardReportRepo;

	@Autowired
	private SerialApprovalBatchRepository serialApprovalBatchRepo;

	@Autowired
	private SerialApprovalBatchHistoryRepository serialApprovalBatchHistoryRepo;

	@Autowired
	private SerialApprovalDetailRepository serialApprovalDetailRepo;
	
	@Transactional(rollbackFor = { Exception.class })
    public void processApprovalBatches() {
        List<Object[]> approvalDetails = serialApprovalDetailRepo.findApprovalDetailsForWhitelistReport();
        for (Object[] detail : approvalDetails) {
            BigDecimal approvalBatchId = (BigDecimal) detail[0];
            BigDecimal reportId = (BigDecimal) detail[1];
            String l1Status = (String) detail[2];
            String l2Status = (String) detail[3];
            BigDecimal campaignDetailId = (BigDecimal) detail[4];
            
    	    // Update SERIAL_REWARD_REPORT
    	    Optional<SerialRewardReportEntity> reportOptional = serialRewardReportRepo.findById(reportId);
    	    Date now = new Date();
    	    if(reportOptional.isPresent()) {
    	    	SerialRewardReportEntity report = reportOptional.get();
    	    	report.setStatus("WAIT_SIGN_FOR");
    	    	report.setUpdateDate(now);
    	        serialRewardReportRepo.save(report);	
    	    }


    	    // Update SERIAL_CAMPAIGN_DETAIL
    	    Optional<SerialCampaignDetailEntity> campaignDetailOptional = serialCampaignDetailRepo.findById(campaignDetailId);
    	    if(campaignDetailOptional.isPresent()) {
    	    	SerialCampaignDetailEntity detail2 = campaignDetailOptional.get();
    	        detail2.setStatus("INCOMPLETE");
    	        detail2.setUpdateDate(now);
    	        serialCampaignDetailRepo.save(detail2);
    	    }

            if ("WAIT_APPROVAL".equals(l1Status)) {
                updateForStageOne(approvalBatchId,reportId,campaignDetailId);
            } else if ("WAIT_APPROVAL".equals(l2Status)) {
                updateForStageTwo(approvalBatchId,reportId,campaignDetailId);
            }
        }
    }
	
	
	private void updateForStageOne(BigDecimal approvalBatchId, BigDecimal reportId, BigDecimal campaignDetailId) {

		 Date now = new Date();

	    // Update SERIAL_APPROVAL_BATCH
	    SerialApprovalBatchEntity approvalBatch = serialApprovalBatchRepo.findById(approvalBatchId)
	                                               .orElseThrow(() -> new EntityNotFoundException("Approval Batch not found"));
	    approvalBatch.setStatus("EXPIRED");
	    approvalBatch.setL1Status("EXPIRED");
	    approvalBatch.setL1CheckDate(now);
	    approvalBatch.setL2Status("-");
	    approvalBatch.setL2CheckDate(now);
	    approvalBatch.setUpdateDate(now);
	    approvalBatch.setUpdateAccount(BigDecimal.ZERO.longValue());
	    serialApprovalBatchRepo.save(approvalBatch);
	    
	    updateSerialApprovalDetail(approvalBatch.getApprovalBatchId(), approvalBatch.getStatus(), approvalBatch.getUpdateDate(), approvalBatch.getUpdateAccount());
	    createSerialApprovalBatchHistory(approvalBatch);
	}
	
	
	private void updateForStageTwo(BigDecimal approvalBatchId, BigDecimal reportId, BigDecimal campaignDetailId) {
		 Date now = new Date();

	    // Update SERIAL_APPROVAL_BATCH
	    SerialApprovalBatchEntity approvalBatch = serialApprovalBatchRepo.findById(approvalBatchId)
	                                               .orElseThrow(() -> new EntityNotFoundException("Approval Batch not found"));
	    approvalBatch.setStatus("EXPIRED");
	    approvalBatch.setL2Status("EXPIRED");
	    approvalBatch.setL2CheckDate(now);
	    approvalBatch.setUpdateDate(now);
	    approvalBatch.setUpdateAccount(BigDecimal.ZERO.longValue());
	    serialApprovalBatchRepo.save(approvalBatch);
	    
	    updateSerialApprovalDetail(approvalBatch.getApprovalBatchId(), approvalBatch.getStatus(), approvalBatch.getUpdateDate(), approvalBatch.getUpdateAccount());
	    createSerialApprovalBatchHistory(approvalBatch);
	}
	
	
	private void updateSerialApprovalDetail(BigDecimal approvalBatchId, String status, Date updateDate, Long updateAccount) {
	    List<SerialApprovalDetailEntity> approvalDetails = serialApprovalDetailRepo.findByApprovalBatchId(approvalBatchId);
	    for (SerialApprovalDetailEntity detail : approvalDetails) {
	        detail.setStatus(status);
	        detail.setUpdateDate(updateDate);
	        detail.setUpdateAccount(updateAccount);
	        serialApprovalDetailRepo.save(detail);
	    }
	}
	
	
	private void createSerialApprovalBatchHistory(SerialApprovalBatchEntity approvalBatch) {
	    SerialApprovalBatchHistoryEntity history = new SerialApprovalBatchHistoryEntity();
	    history.setApprovalBatchId(approvalBatch.getApprovalBatchId());
	    history.setApprovalType("WHITELIST_REPORT");
	    history.setStatus(approvalBatch.getStatus());
	    history.setGroupId(approvalBatch.getGroupId());
	    history.setL1Account(approvalBatch.getL1Account());
	    history.setL1Status(approvalBatch.getL1Status());
	    history.setL1CommentInfo(approvalBatch.getL1CommentInfo());
	    history.setL1CheckDate(approvalBatch.getL1CheckDate());
	    history.setL2Account(approvalBatch.getL2Account());
	    history.setL2Status(approvalBatch.getL2Status());
	    history.setL2CheckDate(approvalBatch.getL2CheckDate());
	    history.setCreateDate(approvalBatch.getUpdateDate());
	    history.setCreateAccount(approvalBatch.getUpdateAccount());
	    serialApprovalBatchHistoryRepo.save(history);
	}
	




}
