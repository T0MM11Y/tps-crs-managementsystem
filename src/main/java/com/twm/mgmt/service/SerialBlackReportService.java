package com.twm.mgmt.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twm.mgmt.persistence.entity.SerialCampaignBlockDetail;
import com.twm.mgmt.persistence.entity.SerialCampaignBlockMain;
import com.twm.mgmt.persistence.repository.CampaignMainRepository;
import com.twm.mgmt.persistence.repository.SerialCampaignBlockDetailRepository;
import com.twm.mgmt.persistence.repository.SerialCampaignBlockMainRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SerialBlackReportService extends BaseService {
	
	public static final int reportSerialCampaignKind = 9;
	
	@Autowired
	SerialCampaignBlockMainRepository serialCampaignBlockMainRepo;
	
	@Autowired
	SerialCampaignBlockDetailRepository serialCampaignBlockDetailRepo;
	
	@Autowired
	private CampaignMainRepository campaignmainRepo;
	
	public int countSerialBlockDetail(Integer blockId) {
		try {
			return serialCampaignBlockDetailRepo.countSerialBlockDetail(blockId);
		} catch(Exception ex) {
			log.error("countSerialBlockDetail SQL Exception Parameter = {} ", blockId);
			log.error("Exception = {}", ex.toString());
			throw ex;
		}	
	}
	
	public List<SerialCampaignBlockDetail> getSerialBlockDetail(int blockId, int offset, int maxSize) {
		try {
			return serialCampaignBlockDetailRepo.getSerialBlockDetail(blockId, offset, maxSize);
		} catch(Exception ex) {
			log.error("getSerialBlockDetail SQL Exception Parameter = {} ", blockId);
			log.error("Exception = {}", ex.toString());
			throw ex;
		}	
	}
	
	public Integer getSerialAvailableBlockIdByKindId(int reportSerialCampaignKind) {
		try {
			return serialCampaignBlockMainRepo.getActiveSerialBlockIdByKindId(reportSerialCampaignKind, getAccountId());
		} catch(Exception ex) {
			log.error("getSerialAvailableBlockIdByKindId SQL Exception Parameter = {} ", reportSerialCampaignKind);
			log.error("Exception = {}", ex.toString());
			throw ex;
		}
	}
	
	public void insertSerialCampaignBlockDetail(ArrayList<SerialCampaignBlockDetail> serialCampaignBlockDetail) {
		log.info("建立任何黑名單用戶");
		if(null != serialCampaignBlockDetail && serialCampaignBlockDetail.size() > 0)
			serialCampaignBlockDetailRepo.saveAll(serialCampaignBlockDetail);
		log.info("完成任何黑名單用戶寫入DB");
	}

	public int insertSerialCampaignBlockMain(int reportSerialCampaignKind) {
		log.info("設定任何黑名單");
		SerialCampaignBlockMain serialCampaignBlockMain = new SerialCampaignBlockMain();
		serialCampaignBlockMain.setCampaignMainId(campaignmainRepo.getCampaignMainId(reportSerialCampaignKind));
		serialCampaignBlockMain.setCreateDate(new Date());
		serialCampaignBlockMain.setCreateAccount(Math.toIntExact(getAccountId()));
		serialCampaignBlockMain.setActive(true);
		
		serialCampaignBlockMainRepo.save(serialCampaignBlockMain);
		log.info("完成設定任何黑名單寫入DB");
		
		return serialCampaignBlockMain.getBlockId();
	}

	public List<SerialCampaignBlockDetail> getSerialBlockDetailBySubId(Integer blockId, String subId) {
		try {
			return serialCampaignBlockDetailRepo.getSerialBlockDetailBySubId(blockId, subId);
		} catch(Exception ex) {
			log.error("getSerialBlockDetailBySubId SQL Exception Parameter = {} {} ", blockId, subId);
			log.error("Exception = {}", ex.toString());
			throw ex;
		}	
	}
	
	public SerialCampaignBlockDetail getSerialBlockDetailByRowId(String rowId) {
		try {
			return serialCampaignBlockDetailRepo.getSerialBlockDetailByRowId(Integer.parseInt(rowId));
		} catch(Exception ex) {
			log.error("getSerialBlockDetailByRowId SQL Exception Parameter = {} ", rowId);
			log.error("Exception = {}", ex.toString());
			throw ex;
		}	
	}

	public void delete(List<String> deleteIds) {
		for (String rowId : deleteIds) {
			serialCampaignBlockDetailRepo.deleteByPrimaryKey(Integer.parseInt(rowId));
		}
	}

}