package com.twm.mgmt.persistence.dao;

import java.util.List;
import java.util.Map;

import com.twm.mgmt.persistence.dto.SerialCampaignWhileListDto;

public interface SerialCampaignWhileListDao {

	List<SerialCampaignWhileListDto> findWhileList(Map<String, String> requestData,Long accountId);


}
