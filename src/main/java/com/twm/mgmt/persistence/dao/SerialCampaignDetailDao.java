package com.twm.mgmt.persistence.dao;

import java.util.List;

import com.twm.mgmt.model.serialCampaign.SerialCampaignSettingVo;

public interface SerialCampaignDetailDao {
	List findReward(SerialCampaignSettingVo serialCampaignSettingVo, Long accountId);

}
