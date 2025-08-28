package com.twm.mgmt.validator.serialCampaignWhileList;

import java.util.List;
import java.util.Map;

import com.twm.mgmt.enums.CrsErrorCode;

import com.twm.mgmt.model.common.ErrorVo;

import com.twm.mgmt.utils.StringUtilsEx;
import com.twm.mgmt.validator.BaseValidator;

public class SerialCampaignWhileListConditionVoValidator extends BaseValidator {

	private Map<String, String> requestData;

	public SerialCampaignWhileListConditionVoValidator(Map<String, String> requestData) {
		this.requestData = requestData;
	}

	@Override
	public List<ErrorVo> validate() {

		String submitSendStartDate = requestData.get("submitSendStartDate");

		String submitSendEndDate = requestData.get("submitSendEndDate");

		if ((StringUtilsEx.isBlank(submitSendStartDate) && StringUtilsEx.isNotBlank(submitSendEndDate)) || (StringUtilsEx.isNotBlank(submitSendStartDate) && StringUtilsEx.isBlank(submitSendEndDate))) {
			addError("submitSendStartDate", CrsErrorCode.QUERY_DATE_ARE_REQUIRED);
		} else if (StringUtilsEx.isNotBlank(submitSendStartDate) && StringUtilsEx.isNotBlank(submitSendEndDate)) {
			validateDateRange("submitSendStartDate", "submitSendEndDate", submitSendStartDate, submitSendEndDate, 90);
		}

		return getResult();
	}

}
