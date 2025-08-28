package com.twm.mgmt.validator.account;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.twm.mgmt.enums.CrsErrorCode;
import com.twm.mgmt.model.account.MOAccountVo;
import com.twm.mgmt.model.common.ErrorVo;
import com.twm.mgmt.utils.StringUtilsEx;
import com.twm.mgmt.validator.BaseValidator;

public class MOAccountVoValidator extends BaseValidator {

	private MOAccountVo vo;

	public MOAccountVoValidator(MOAccountVo vo) {
		this.vo = vo;
	}

	@Override
	public List<ErrorVo> validate() {
		Long departmentId = vo.getDepartmentId();

		if (departmentId == null) {
			addError("departmentId", CrsErrorCode.IS_REQUIRED);
		}

		Long contactAccountId = vo.getContactAccountId();

		if (contactAccountId == null) {
			addError("contactAccountId", CrsErrorCode.IS_REQUIRED);
		}

		String merchantId = vo.getMerchantId();

		if (StringUtilsEx.isBlank(merchantId)) {
			addError("merchantId", CrsErrorCode.IS_REQUIRED);
		} else {
			String regex = "^[a-zA-Z0-9-_]+$";

			if (!merchantId.matches(regex)) {
				addError("merchantId", CrsErrorCode.INPUT_TEXT_FORMAT_ERROR);
			}
		}

		String deptNo = vo.getDeptNo();

		if (StringUtilsEx.isBlank(deptNo)) {
			addError("deptNo", CrsErrorCode.IS_REQUIRED);
		} else {
			String regex = "^[a-zA-Z0-9]+$";

			if (!merchantId.matches(regex)) {
				addError("deptNo", CrsErrorCode.INPUT_TEXT_FORMAT_ERROR);
			}
		}

		String apiKey = vo.getApiKey();

		if (StringUtilsEx.isBlank(apiKey)) {
			addError("apiKey", CrsErrorCode.IS_REQUIRED);
		}

		String encryptedKey = vo.getEncryptedKey();

		if (StringUtilsEx.isBlank(encryptedKey)) {
			addError("encryptedKey", CrsErrorCode.IS_REQUIRED);
		}
		
		String requestId = vo.getRequestId();
		if(StringUtils.isBlank(requestId)) {
			addError("requestId", CrsErrorCode.IS_REQUIRED);
		}

		return getResult();
	}

}
