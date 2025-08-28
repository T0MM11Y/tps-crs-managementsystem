package com.twm.mgmt.service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.enums.RoleType;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.repository.APKeyIvRepository;
import com.twm.mgmt.persistence.repository.AccountActionHistoryRepository;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MOAccountRepository;
import com.twm.mgmt.persistence.repository.RoleRepository;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.StringUtilsEx;

public abstract class BaseService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${spring.profiles.active}")
	public String active;

	@Value("${rc.recieve.secrect.key}")
	public String rcScrectKey;

	@Value("${rc.recieve.secrect.iv}")
	public String rcScrectIv;

	@Value("${crs.mail.from}")
	public String fromEmail;

	@Autowired
	protected AccountRepository accountRepo;

	@Autowired
	protected RoleRepository roleRepo;

	@Autowired
	protected DepartmentRepository departmentRepo;

	@Autowired
	protected MOAccountRepository mOAccountRepo;

	@Autowired
	protected AccountActionHistoryRepository accountActionHistoryRepo;

	@Autowired
	protected APKeyIvRepository apKeyIvRepo;

	public AccountEntity getAccountEntity(Long accountId) {
		if (accountId != null) {
			Optional<AccountEntity> optional = accountRepo.findById(accountId);

			if (optional.isPresent()) {

				return optional.get();
			}
		}

		return null;
	}

	public List<AccountEntity> getApprovalUser() {
		List<Long> longList = new ArrayList<Long>();
		longList.add(getAccountId());
		// 解決checkmarx高風險,對數據編碼在解碼
		return JsonUtil.jsonToList(
				ESAPI.encoder()
						.decodeForHTML(ESAPI.encoder()
								.encodeForHTML(JsonUtil.objectToJson(
										accountRepo.findByDepartmentIdAndApprovable(longList, getDepartmentId())))),
				AccountEntity.class);

	}

	/**
	 * 送簽主檔歷程
	 * 
	 * @param approval
	 * @param recordReason
	 */
	protected void saveApprovalMainHistory(String recordReason) {
		//ApprovalMainHistoryEntity entity = new ApprovalMainHistoryEntity();

		//entity.setRowId(new BigDecimal(approvalmainhistoryrepository.currentPERIOD_ID_num() + 1));
		//entity.setApprovalMainId(approval.getApprovalMainId());
		// System.out.println("baseservice1");
		//entity.setApprovalType(approval.getApprovalType());
		// System.out.println("baseservice2");
		//entity.setCampaignDetailId(approval.getCampaignDetailId());
		// System.out.println("baseservice3");
		//entity.setReportId(approval.getReportId());
		// System.out.println("baseservice4");
		//entity.setStatus(approval.getStatus());
		// System.out.println("baseservice5");
		//entity.setL1Account(approval.getL1Account());
		// System.out.println("baseservice6");
		//entity.setL1Status(approval.getL1Status());
		// System.out.println("baseservice7");
		//entity.setL1ApprovalDetailId(approval.getL1ApprovalDetailId());
		// System.out.println("baseservice8");
		//entity.setL2Account(approval.getL2Account());
		// System.out.println("baseservice9");
		//entity.setL2Status(approval.getL2Status());
		// System.out.println("baseservice10");
		//entity.setL2ApprovalDetailId(approval.getL2ApprovalDetailId());
		// System.out.println("baseservice11");
		//entity.setSourceAccount(approval.getSourceAccount());
		// System.out.println("baseservice12");
		//entity.setRecordReason(recordReason);
		// System.out.println("baseservice13");
		//entity.setCreateDate(new Date());
		// System.out.println("baseservice14");
		//entity.setCreateAccount(getAccountId());
		// System.out.println("baseservice15");
		//approvalMainHistoryRepo.save(entity);
	}

	/**
	 * 申裝類型
	 * 
	 * @param aq
	 * @param np
	 * @param rt
	 * @param separator
	 * @return
	 */
	protected String getApplyType(Integer aq, Integer np, Integer rt, String separator) {
		List<String> applyType = new ArrayList<>();

		if (aq != null && aq == 1) {
			applyType.add("AQ");
		}

		if (np != null && np == 1) {
			applyType.add("NP");
		}

		if (rt != null && rt == 1) {
			applyType.add("RT");
		}

		return StringUtilsEx.join(applyType, separator);
	}

	/**
	 * 使用者ID
	 * 
	 * @return
	 */
	public Long getAccountId() {

		return getUserInfo().getAccountId();
	}

	/**
	 * 使用者角色ID
	 * 
	 * @return
	 */
	public Long getRoleId() {

		return getUserInfo().getRoleId();
	}

	/**
	 * 使用者角色名稱
	 * 
	 * @return
	 */
	protected String getRoleName() {

		return getUserInfo().getRoleName();
	}

	/**
	 * 使用者角色
	 * 
	 * @return
	 */
	protected RoleType getRoleType() {

		return RoleType.find(getRoleName());
	}

	/**
	 * 使用者部門別
	 * 
	 * @return
	 */
	public Long getDepartmentId() {

		return getUserInfo().getDepartmentId();
	}

	/**
	 * 使用者BuTag
	 * 
	 * @return
	 */
	public String getBuTag() {

		return getUserInfo().getBuTag();
	}

	/**
	 * 取得使用者資訊
	 * 
	 * @return
	 */
	private UserInfoVo getUserInfo() {
		HttpSession session = (HttpSession) RequestContextHolder.getRequestAttributes()
				.resolveReference(RequestAttributes.REFERENCE_SESSION);

		return (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);
	}

	protected String fullUrl(String path, HttpServletRequest request) {
		URI uri = URI.create(request.getRequestURL().toString());

		String scheme = uri.getScheme();

		String host = uri.getHost();

		int port = uri.getPort();

		if (port > 0) {

			return String.format("%s://%s:%s%s", scheme, host, port, path);
		}

		return String.format("%s://%s%s", scheme, host, path);
	}

}
