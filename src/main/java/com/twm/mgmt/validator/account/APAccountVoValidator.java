package com.twm.mgmt.validator.account;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.enums.CrsErrorCode;
import com.twm.mgmt.model.account.APAccountVo;
import com.twm.mgmt.model.account.APKeyIvVo;
import com.twm.mgmt.model.common.ErrorVo;
import com.twm.mgmt.persistence.repository.APAccountRepository;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.SpringUtils;
import com.twm.mgmt.utils.StringUtilsEx;
import com.twm.mgmt.validator.BaseValidator;

public class APAccountVoValidator extends BaseValidator {

	private APAccountVo vo;

	public APAccountVoValidator(APAccountVo vo) {
		this.vo = vo;
	}

	@Override
	public List<ErrorVo> validate() {
		Long departmentId = vo.getDepartmentId();

		if (departmentId == null) {
			addError("departmentId", CrsErrorCode.IS_REQUIRED);
		}
		
		String contactAccountId = vo.getContactAccountId();

		if (StringUtils.isBlank(contactAccountId)) {
			addError("contactAccountId", CrsErrorCode.IS_REQUIRED);
		}
		
		String requestId = vo.getRequestId();
		if(StringUtils.isBlank(requestId)) {
			addError("requestId", CrsErrorCode.IS_REQUIRED);
		}

		String sourceId = vo.getSourceId();

		if (StringUtilsEx.isBlank(sourceId)) {
			addError("sourceId", CrsErrorCode.IS_REQUIRED);
		} else {
			ActionType action = vo.getAction();

			if (action.isAdd()) {
				APAccountRepository repo = SpringUtils.getBean(APAccountRepository.class);

				boolean isExist = repo.countBySourceId(sourceId.toUpperCase()) > 0;

				if (isExist) {
					addError("sourceId", CrsErrorCode.SOURCE_ID_IS_EXIST);
				}
			}

			String regex = "^[a-zA-Z0-9-_]+$";

			if (!sourceId.matches(regex)) {
				addError("sourceId", CrsErrorCode.INPUT_TEXT_FORMAT_ERROR);
			}
		}

		List<APKeyIvVo> keyIvs = vo.getKeyIvs();

		if (CollectionUtils.isNotEmpty(keyIvs)) {
			int size = keyIvs.size();

			if (size > 2) {
				addError(String.format("expiredDate%s", size - 1), CrsErrorCode.VALIDATE_ERROR);
			} else {
				for (int i = 0; i < size; i++) {
					String expiredDate = keyIvs.get(i).getExpiredDate();

					if (StringUtilsEx.isBlank(expiredDate)) {
						addError(String.format("expiredDate%s", i), CrsErrorCode.IS_REQUIRED);
					} else if (!isDate(expiredDate)) {
						addError(String.format("expiredDate%s", i), CrsErrorCode.INPUT_TEXT_FORMAT_ERROR);
					}
				}

				if (size == 2) {
					String expiredDate1 = keyIvs.get(0).getExpiredDate();
					String expiredDate2 = keyIvs.get(1).getExpiredDate();

					if (StringUtilsEx.isNotBlank(expiredDate1) && StringUtilsEx.isNotBlank(expiredDate2)) {
						if (DateUtilsEx.parseDate(expiredDate1).compareTo(DateUtilsEx.parseDate(expiredDate2)) == 0) {
							addError("expiredDate1", CrsErrorCode.EXPIRE_DATE_IS_DUPLICATE);
						}
					}
				}
			}
		}

		return getResult();
	}

	private boolean isDate(String date) {
		try {
			DateUtilsEx.parseDate(date);

			return true;
		} catch (Exception e) {

			return false;
		}
	}

}
