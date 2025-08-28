package com.twm.mgmt.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twm.mgmt.Enum.LogFormat;
import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.model.account.APAccountConditionVo;
import com.twm.mgmt.model.account.APAccountResultVo;
import com.twm.mgmt.model.account.APAccountVo;
import com.twm.mgmt.model.account.APKeyIvVo;
import com.twm.mgmt.model.account.AccountConditionVo;
import com.twm.mgmt.model.account.AccountResultVo;
import com.twm.mgmt.model.account.AccountVo;
import com.twm.mgmt.model.account.DepartmentConditionVo;
import com.twm.mgmt.model.account.DepartmentResultVo;
import com.twm.mgmt.model.account.DepartmentVo;
import com.twm.mgmt.model.account.MOAccountConditionVo;
import com.twm.mgmt.model.account.MOAccountResultVo;
import com.twm.mgmt.model.account.MOAccountVo;
import com.twm.mgmt.model.account.MoAccountMapAccountConditionVo;
import com.twm.mgmt.model.account.MoAccountMapAccountResultVo;
import com.twm.mgmt.model.account.PermissionVo;
import com.twm.mgmt.model.account.RedeemTotalApiResultVo;
import com.twm.mgmt.model.account.RedeemTotalApiVo;
import com.twm.mgmt.model.account.RoleVo;
import com.twm.mgmt.model.common.MenuVo;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.SubMenuVo;
import com.twm.mgmt.persistence.dto.APAccountDto;
import com.twm.mgmt.persistence.dto.AccountActionHistoryDto;
import com.twm.mgmt.persistence.dto.AccountDto;
import com.twm.mgmt.persistence.dto.MOAccountDto;
import com.twm.mgmt.persistence.dto.MoAccountMapAccountDto;
import com.twm.mgmt.persistence.dto.RedeemTotalApiDto;
import com.twm.mgmt.persistence.entity.APAccountEntity;
import com.twm.mgmt.persistence.entity.APAccountMAPAccountEntity;
import com.twm.mgmt.persistence.entity.APKeyIvEntity;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.AccountPermissionProgramEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.entity.MenuEntity;
import com.twm.mgmt.persistence.entity.ProgramEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;
import com.twm.mgmt.persistence.entity.RolePermissionProgramEntity;
import com.twm.mgmt.persistence.entity.pk.APAccountMAPAccountPk;
import com.twm.mgmt.persistence.entity.pk.APAccountPk;
import com.twm.mgmt.persistence.repository.APAccountMAPAccountRepository;
import com.twm.mgmt.persistence.repository.APAccountRepository;
import com.twm.mgmt.persistence.repository.APKeyIvRepository;
import com.twm.mgmt.persistence.repository.AccountActionHistoryRepository;
import com.twm.mgmt.persistence.repository.AccountPermissionProgramRepository;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MOAccountRepository;
import com.twm.mgmt.persistence.repository.MenuRepository;
import com.twm.mgmt.persistence.repository.MoAccountMapAccountRepository;
import com.twm.mgmt.persistence.repository.RedeemTotalApiRepository;
import com.twm.mgmt.persistence.repository.RolePermissionProgramRepository;
import com.twm.mgmt.persistence.repository.RoleRepository;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.RandomUtil;
import com.twm.mgmt.utils.StringUtilsEx;

@Service
public class AccountService extends BaseService {

	@Value("${ap.account.secrect.key}")
	private String screctKey;

	@Value("${ap.account.secrect.iv}")
	private String screctIv;
	
	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private MenuRepository menuRepo;

	@Autowired
	private RolePermissionProgramRepository rolePermissionProgramRepo;

	@Autowired
	private AccountPermissionProgramRepository accountPermissionProgramRepo;

	@Autowired
	private DepartmentRepository departmentRepo;

	@Autowired
	private APAccountRepository apAccountRepo;
	
	@Autowired
	private APAccountMAPAccountRepository aPAccountMAPAccountRepo;

	@Autowired
	private APKeyIvRepository apKeyIvRepo;

	@Autowired
	private MOAccountRepository moAccountRepo;
	
	@Autowired
	private MoAccountMapAccountRepository moAccountMapAccountRepo;
	
	@Autowired
	private RedeemTotalApiRepository redeemTotalApiRepo;
	
	@Autowired
	private AccountActionHistoryRepository accountActionHistoryRepo;
	
	private String commonFormat = LogFormat.CommonLog.getName();
	private String errorFormat = LogFormat.SqlError.getName();
	public List<RoleVo> findRoleList() {
		List<RoleEntity> entities = roleRepo.findAll();

		return entities.stream().map(entity -> new RoleVo(entity)).collect(Collectors.toList());
	}

	public List<AccountVo> findAccountList() {
		List<AccountEntity> entities = accountRepo.findEnabledAccount();
		//checkmarx弱掃
		entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), AccountEntity.class);

		return entities.stream().map(entity -> new AccountVo(entity)).collect(Collectors.toList());
	}

	@Transactional
	public List<MenuVo> findRoleMenu(Long roleId) {
		List<MenuVo> vos = composeMenu();

		if (roleId != null) {
			List<RolePermissionProgramEntity> programs = rolePermissionProgramRepo.findByRoleId(roleId);

			vos.stream().forEach(vo -> {
				vo.getSubVos().stream().forEach(subVo -> {
					Long programId = subVo.getProgramId();

					subVo.setPermission(programs.stream().filter(program -> program.getProgramId().equals(programId))
							.findFirst().isPresent() ? true : null);
				});
			});
		}

		return vos;
	}

	@Transactional
	public List<MenuVo> findAccountMenu(Long accountId, Long roleId) {
		List<MenuVo> vos = composeMenu();

		List<RolePermissionProgramEntity> rolePrograms = rolePermissionProgramRepo.findByRoleId(roleId);

		vos.stream().forEach(vo -> {
			vo.getSubVos().stream().forEach(subVo -> {
				Long programId = subVo.getProgramId();

				subVo.setStatusName(
						rolePrograms.stream().anyMatch(program -> program.getProgramId() == programId) ? "已啟用" : "已停用");
			});
		});

		List<AccountPermissionProgramEntity> programs = accountPermissionProgramRepo.findByAccountId(accountId);

		if (CollectionUtils.isNotEmpty(programs)) {

			vos.stream().forEach(vo -> {
				vo.getSubVos().stream().forEach(subVo -> {
					Long programId = subVo.getProgramId();

					Optional<AccountPermissionProgramEntity> optional = programs.stream()
							.filter(program -> program.getProgramId() == programId).findFirst();

					subVo.setPermission(
							optional.isPresent() ? StringUtilsEx.equals("Y", optional.get().getEnabled()) : null);
				});
			});
		}

		return vos;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void saveOrUpdatePermission(PermissionVo vo) throws Exception {
		Long accountId = vo.getAccountId();

		if (accountId == null) {
			// role permission
			ActionType action = ActionType.find(vo.getAction());

			if (action.isAdd()) {
				RoleEntity entity = new RoleEntity();

				entity.setRoleName(String.format("ROLE_%s", vo.getRoleName()));

				roleRepo.save(entity);

				vo.setRoleId(entity.getRoleId());

			} else if (action.isEdit()) {
				rolePermissionProgramRepo.deleteByRoleId(vo.getRoleId());
			}

			Long roleId = vo.getRoleId();

			vo.getOnPrograms().stream().filter(lists -> !lists.isEmpty()).forEach(lists -> {
				Supplier<Stream<String>> supplier = () -> lists.stream().filter(str -> StringUtilsEx.isNotBlank(str));

				supplier.get().forEach(str -> {
					Long programId = Long.valueOf(str);

					saveRolePermissionProgramEntity(roleId, programId);
				});
			});
		} else {
			// account permission
			accountPermissionProgramRepo.deleteByAccountId(accountId);

			// on
			processAccountPrograms(vo.getOnPrograms(), accountId, Boolean.TRUE);

			// off
			processAccountPrograms(vo.getOffPrograms(), accountId, Boolean.FALSE);
		}

	}

	public List<DepartmentVo> findDepartmentList() {
		List<DepartmentEntity> entities = departmentRepo.findEnabledDepartment();
		//checkmarx弱掃
		entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), DepartmentEntity.class);
		return entities.stream().map(entity -> new DepartmentVo(entity)).collect(Collectors.toList());
	}

	public void copyQueryCondition(AccountConditionVo oriCondition, AccountConditionVo newCondition) {
		newCondition.setRoleId(oriCondition.getRoleId());

		newCondition.setDepartmentId(oriCondition.getDepartmentId());
	}

	@Transactional
	public QueryResultVo findAccountList(AccountConditionVo condition) {
		QueryResultVo resultVo = new QueryResultVo(condition);

		List<AccountDto> dtos = accountRepo.findByCondition(condition);

		Integer total = accountRepo.countByCondition(condition);

		List<AccountResultVo> result = dtos.stream().map(dto -> new AccountResultVo(dto)).collect(Collectors.toList());

		resultVo.setTotal(total);

		resultVo.setResult(result);

		return resultVo;
	}

	public AccountVo getAccount(Long accountId) {
		AccountVo vo = new AccountVo();

		if (accountId == null) {
			vo.setAction(ActionType.ADD);

			vo.setApprovable("N");
		} else {
			AccountEntity entity = getAccountEntity(accountId);

			if (entity != null) {
				vo.setAccountId(entity.getAccountId());

				vo.setUserName(entity.getUserName());

				vo.setEmail(entity.getEmail());

				vo.setMobile(entity.getMobile());

				vo.setRoleId(entity.getRoleId());

				vo.setDepartmentId(entity.getDepartmentId());

				vo.setApprovable(entity.getApprovable());
			}

			vo.setAction(ActionType.EDIT);
		}

		return vo;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void saveOrUpdateAccount(AccountVo vo) throws Exception {
		ActionType action = vo.getAction();

		Date sysdate = new Date();

		Long accountId = getAccountId();

		String email = vo.getEmail();

		AccountEntity entity;

		if (action.isAdd()) {
			entity = new AccountEntity();

			entity.setCreateDate(sysdate);

			entity.setCreateAccount(accountId);
		} else {
			entity = accountRepo.findById(vo.getAccountId()).get();

			entity.setUpdateDate(sysdate);

			entity.setUpdateAccount(accountId);
		}

		// account

		// 新增加變更簽核核決權限的參數2022/01/18-RichardTsai
		if (vo.getApprovable() != null)
			entity.setApprovable(vo.getApprovable());
		else
			entity.setApprovable("N");

		entity.setUserId(email.substring(0, email.indexOf("@")));

		entity.setUserName(vo.getUserName());

		entity.setEmail(email);

		entity.setMobile(vo.getMobile());

		entity.setRoleId(vo.getRoleId());

		entity.setDepartmentId(vo.getDepartmentId());

		accountRepo.save(entity);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void updateAccountStatus(Long accountId, String status,String requestId) {
		accountRepo.updateEnabledByAccountId(accountId, status, getAccountId());
	}

	public void copyQueryCondition(DepartmentConditionVo oriCondition, DepartmentConditionVo newCondition) {
		newCondition.setDepartmentName(oriCondition.getDepartmentName());
	}

	@Transactional
	public QueryResultVo findDepartmentList(DepartmentConditionVo condition) {
		QueryResultVo resultVo = new QueryResultVo(condition);

		List<DepartmentEntity> entities = departmentRepo.findByCondition(condition);

		Integer total = departmentRepo.countByCondition(condition);

		List<DepartmentResultVo> result = entities.stream().map(entity -> new DepartmentResultVo(entity))
				.collect(Collectors.toList());

		resultVo.setTotal(total);

		resultVo.setResult(result);

		return resultVo;
	}

	public DepartmentVo getDepartment(Long departmentId) {
		DepartmentVo vo = new DepartmentVo();

		if (departmentId == null) {
			vo.setAction(ActionType.ADD);

		} else {
			Optional<DepartmentEntity> optional = departmentRepo.findById(departmentId);

			if (optional.isPresent()) {
				DepartmentEntity entity = optional.get();

				vo.setDepartmentId(entity.getDepartmentId());

				vo.setDepartmentName(entity.getDepartmentName());
			}

			vo.setAction(ActionType.EDIT);
		}

		return vo;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void saveOrUpdateDepartment(DepartmentVo vo) throws Exception {
		ActionType action = vo.getAction();

		Date sysdate = new Date();

		DepartmentEntity entity;

		if (action.isAdd()) {
			entity = new DepartmentEntity();

			entity.setCreateDate(sysdate);
		} else {
			entity = departmentRepo.findById(vo.getDepartmentId()).get();

			entity.setUpdateDate(sysdate);
		}

		entity.setDepartmentId(vo.getDepartmentId());

		entity.setDepartmentName(vo.getDepartmentName());

		departmentRepo.save(entity);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void updateDepartmentStatus(Long departmentId, String status) {
		departmentRepo.updateEnabledByDepartmentId(departmentId, status);
	}

	public void copyQueryCondition(APAccountConditionVo oriCondition, APAccountConditionVo newCondition) {
		newCondition.setDepartmentId(oriCondition.getDepartmentId());
	}

	@Transactional
	public QueryResultVo findAPAccountList(APAccountConditionVo condition) {
		QueryResultVo resultVo = new QueryResultVo(condition);

		List<APAccountDto> dtos = apAccountRepo.findByCondition(condition);

		Integer total = apAccountRepo.countByCondition(condition);

		List<APAccountResultVo> result = dtos.stream().map(entity -> new APAccountResultVo(entity))
				.collect(Collectors.toList());

		resultVo.setTotal(total);

		resultVo.setResult(result);

		return resultVo;
	}

	public APAccountVo getAPAccount(Long departmentId, String sourceId) throws Exception {
		APAccountVo vo = new APAccountVo();

		if (departmentId != null && StringUtilsEx.isNotBlank(sourceId)) {
			Optional<APAccountEntity> optional = apAccountRepo.findById(new APAccountPk(departmentId, sourceId));

			if (optional.isPresent()) {
				APAccountEntity entity = optional.get();

				vo.setDepartmentId(entity.getDepartmentId());
				
				vo.setContactAccountId(aPAccountMAPAccountRepo.findById(new APAccountMAPAccountPk(departmentId, sourceId)).orElseGet(() -> new APAccountMAPAccountEntity()).getAccountname());

				vo.setSourceId(entity.getSourceId());

				vo.setEnabled(entity.getEnabled());

				List<APKeyIvEntity> entities = apKeyIvRepo.find2RowBySourceId(entity.getSourceId());
				//checkmarx弱掃
				entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), APKeyIvEntity.class);

				if (CollectionUtils.isNotEmpty(entities)) {
					List<APKeyIvVo> keyIvs = new ArrayList<>();

					for (APKeyIvEntity keyIvEntity : entities) {
						APKeyIvVo keyIvVo = new APKeyIvVo();

						keyIvVo.setKeyIvId(keyIvEntity.getKeyIvId());

						keyIvVo.setExpiredDate(DateUtilsEx.formatDate(keyIvEntity.getExpiredDate(), "yyyy-MM-dd"));

						keyIvVo.setKey(AESUtil.decryptStr(keyIvEntity.getKey(), screctKey, screctIv));

						keyIvVo.setIv(AESUtil.decryptStr(keyIvEntity.getIv(), screctKey, screctIv));

						keyIvs.add(keyIvVo);
					}

					vo.setKeyIvs(keyIvs);
				}
			}

			vo.setAction(ActionType.EDIT);
		} else {
			vo.setEnabled("Y");

			vo.setAction(ActionType.ADD);
		}

		return vo;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void saveOrUpdateAPAccount(APAccountVo vo) throws Exception {
		ActionType action = vo.getAction();

		Date sysdate = new Date();

		APAccountEntity apAccountEntity;
		APAccountMAPAccountEntity aPAccountMAPAccountEntity;

		if (action.isAdd()) {
			apAccountEntity = new APAccountEntity();
			aPAccountMAPAccountEntity = new APAccountMAPAccountEntity();

			apAccountEntity.setDepartmentId(vo.getDepartmentId());
			aPAccountMAPAccountEntity.setDepartmentId(vo.getDepartmentId());

			apAccountEntity.setSourceId(vo.getSourceId());
			aPAccountMAPAccountEntity.setSourceId(vo.getSourceId());
			
			aPAccountMAPAccountEntity.setAccountname(vo.getContactAccountId());

			apAccountEntity.setCreateDate(sysdate);
			aPAccountMAPAccountEntity.setCreateDate(sysdate);
			
		} else {
			apAccountEntity = apAccountRepo.findById(new APAccountPk(vo.getDepartmentId(), vo.getSourceId())).get();
			aPAccountMAPAccountEntity = aPAccountMAPAccountRepo.findById(new APAccountMAPAccountPk(vo.getDepartmentId(), vo.getSourceId())).get();
			aPAccountMAPAccountEntity.setAccountname(vo.getContactAccountId());
			apAccountEntity.setUpdateDate(sysdate);
			aPAccountMAPAccountEntity.setUpdateDate(sysdate);
		}

		apAccountEntity.setEnabled(vo.getEnabled());

		apAccountRepo.save(apAccountEntity);
		aPAccountMAPAccountRepo.save(aPAccountMAPAccountEntity);

		if (CollectionUtils.isNotEmpty(vo.getKeyIvs())) {
			for (APKeyIvVo keyIv : vo.getKeyIvs()) {
				Long id = keyIv.getKeyIvId();

				Date expiredDate = DateUtilsEx.endDate(keyIv.getExpiredDate());

				APKeyIvEntity keyIvEntity;

				if (id == null) {
					keyIvEntity = new APKeyIvEntity();

					String key = RandomUtil.genRandom(CrsConstants.AES_KEY_LEN);

					String iv = RandomUtil.genRandom(CrsConstants.AES_IV_LEN);

					keyIvEntity.setSourceId(vo.getSourceId());					
					keyIvEntity.setKey(AESUtil.encryptStr(key, screctKey, screctIv));
					keyIvEntity.setIv(AESUtil.encryptStr(iv, screctKey, screctIv));
					keyIvEntity.setCreateDate(sysdate);
				} else {
					keyIvEntity = apKeyIvRepo.findById(id).get();
				}

				keyIvEntity.setExpiredDate(expiredDate);

				apKeyIvRepo.save(keyIvEntity);
			}
		}
	}

	public void copyQueryCondition(MOAccountConditionVo oriCondition, MOAccountConditionVo newCondition) {

	}

	@Transactional
	public QueryResultVo findMOAccountList(MOAccountConditionVo condition) {
		QueryResultVo resultVo = new QueryResultVo(condition);

		List<MOAccountDto> dtos = moAccountRepo.findByCondition(condition);

		Integer total = moAccountRepo.countByCondition(condition);

		List<MOAccountResultVo> result = dtos.stream().map(dto -> new MOAccountResultVo(dto))
				.collect(Collectors.toList());

		resultVo.setTotal(total);

		resultVo.setResult(result);

		return resultVo;
	}

	public MOAccountVo getMOAccount(String merchantId, String deptNo) throws Exception {
		MOAccountVo vo = new MOAccountVo();

		if (StringUtilsEx.isNotBlank(merchantId)) {
			// Optional<MOAccountEntity> optional = moAccountRepo.findById(merchantId);
			Example<MOAccountEntity> example = Example.of(new MOAccountEntity(merchantId, deptNo));
			Optional<MOAccountEntity> optional = moAccountRepo.findOne(example);

			if (optional.isPresent()) {
				MOAccountEntity entity = optional.get();

				vo.setMerchantId(entity.getMerchantId());

				vo.setDepartmentId(entity.getDepartmentId());

				vo.setContactAccountId(entity.getContactAccountId());

				vo.setDeptNo(entity.getDeptNo());

				vo.setApiKey(entity.getApiKey());

				vo.setEncryptedKey(entity.getEncryptedKey());
			}

			vo.setAction(ActionType.EDIT);
		} else {
			vo.setAction(ActionType.ADD);
		}

		return vo;
	}

	public List<AccountVo> findAccountList(Long departmentId) {
		List<AccountEntity> entities = accountRepo.findByDepartmentId(departmentId);

		return entities.stream().map(entity -> new AccountVo(entity)).collect(Collectors.toList());
	}

	@Transactional(rollbackFor = { Exception.class })
	public void saveOrUpdateMOAccount(MOAccountVo vo) throws Exception {
		ActionType action = vo.getAction();

		Date sysdate = new Date();

		MOAccountEntity entity;
		entity = new MOAccountEntity();

		if (action.isAdd()) {
			
			entity.setMerchantId(vo.getMerchantId());

			entity.setDepartmentId(vo.getDepartmentId());

			entity.setDeptNo(vo.getDeptNo());

			entity.setCreateDate(sysdate);
		} else {
			// entity = moAccountRepo.findById(vo.getMerchantId()).get();
			// BeanUtils.copyProperties(moAccountRepo.findOne(mOAccountEntityExample).get(),
			// entity);
			MOAccountEntity entity2 = new MOAccountEntity();
			Example<MOAccountEntity> mOAccountEntityExample = Example
					.of(new MOAccountEntity(vo.getMerchantId(), vo.getDeptNo()));
			entity2 = moAccountRepo.findOne(mOAccountEntityExample).get();
			
			 BeanUtils.copyProperties(entity2,
			 entity);
			 moAccountRepo.delete(entity2);
			 moAccountRepo.flush();
			 

		}

		entity.setContactAccountId(vo.getContactAccountId());

		entity.setApiKey(vo.getApiKey());

		entity.setEncryptedKey(vo.getEncryptedKey());

		entity.setUpdateDate(sysdate);

		moAccountRepo.save(entity);
		

	}

	private List<MenuVo> composeMenu() {
		List<MenuEntity> menus = menuRepo.findAll().stream().sorted(Comparator.comparing(MenuEntity::getOrderNo))
				.collect(Collectors.toList());

		return menus.stream().map(menu -> {
			MenuVo vo = new MenuVo();

			List<ProgramEntity> programs = menu.getPrograms().stream()
					.sorted(Comparator.comparing(ProgramEntity::getOrderNo)).collect(Collectors.toList());

			vo.setMenuId(menu.getMenuId());

			vo.setName(menu.getMenuName());

			vo.setSubVos(
					programs.stream().map(program -> new SubMenuVo(program.getProgramId(), program.getProgramName()))
							.collect(Collectors.toList()));

			return vo;
		}).collect(Collectors.toList());
	}

	private void saveRolePermissionProgramEntity(Long roleId, Long programId) {
		RolePermissionProgramEntity entity = new RolePermissionProgramEntity();

		entity.setRoleId(roleId);

		entity.setProgramId(programId);

		entity.setCreateAccount(getAccountId());

		entity.setCreateDate(new Date());

		rolePermissionProgramRepo.save(entity);
	}

	private void processAccountPrograms(List<List<String>> programs, Long accountId, boolean isEnabled) {
		if (CollectionUtils.isNotEmpty(programs)) {
			programs.stream().filter(lists -> !lists.isEmpty()).forEach(lists -> {
				Supplier<Stream<String>> supplier = () -> lists.stream().filter(str -> StringUtilsEx.isNotBlank(str));

				supplier.get().forEach(str -> {
					Long programId = Long.valueOf(str);

					saveAccountPermissionProgramEntity(accountId, programId, isEnabled);
				});
			});
		}
	}

	private void saveAccountPermissionProgramEntity(Long accountId, Long programId, boolean isEnabled) {
		AccountPermissionProgramEntity entity = new AccountPermissionProgramEntity();

		entity.setAccountId(accountId);

		entity.setProgramId(programId);

		entity.setCreateAccount(getAccountId());

		entity.setCreateDate(new Date());

		entity.setEnabled(isEnabled ? "Y" : "N");

		accountPermissionProgramRepo.save(entity);
	}
	
	@Transactional
	public QueryResultVo getMoAccountMapAccountList(MoAccountMapAccountConditionVo condition) {
		QueryResultVo resultVo = new QueryResultVo(condition);

		List<MoAccountMapAccountDto> dtos = moAccountMapAccountRepo.findByCondition(condition);

		Integer total = moAccountMapAccountRepo.countByCondition(condition);

		List<MoAccountMapAccountResultVo> result = dtos.stream().map(dto -> new MoAccountMapAccountResultVo(dto))
				.collect(Collectors.toList());

		resultVo.setTotal(total);
		resultVo.setResult(result);
		return resultVo;
	}
	
	@Transactional
	public void deleteMoAccountMapAccount(String schedule_id,String functionId,String moDeptNo,String crsAccountId,String requestId,String functionTitle ) {
		moAccountMapAccountRepo.deleteMoAccountMapAccount(functionId,moDeptNo,crsAccountId);
	}
	
	@Transactional
	public void insertMoAccountMapAccount(String schedule_id,String functionId,String departmentId,String crsAccountId,String requestId,String functiontitle) {
		moAccountMapAccountRepo.insertMoAccountMapAccount(functionId,departmentId,crsAccountId,functiontitle);
	}
	
	@Transactional
	public QueryResultVo getRedeemTotalApiList(RedeemTotalApiVo condition) {
		QueryResultVo resultVo = new QueryResultVo(condition);

		List<RedeemTotalApiDto> dtos = redeemTotalApiRepo.findByCondition(condition);

		Integer total = redeemTotalApiRepo.countByCondition(condition);

		List<RedeemTotalApiResultVo> result = dtos.stream().map(dto -> new RedeemTotalApiResultVo(dto))
				.collect(Collectors.toList());

		resultVo.setTotal(total);
		resultVo.setResult(result);
		return resultVo;
	}
	
	@Transactional
	public void deleteRedeemTotalApi(String schedule_id,String moDeptNo,String apiUrl, String requestId) {
		redeemTotalApiRepo.deleteRedeemTotalApi(moDeptNo,apiUrl);
	}
	
	@Transactional
	public void insertRedeemTotalApi(String schedule_id,String departmentId,String apiUrl, String requestId) {
		redeemTotalApiRepo.insertRedeemTotalApi(departmentId,apiUrl);
	}
	
	@Transactional
    public List<AccountActionHistoryDto> getAccountActionHistoriesByCriteria(Long executeAccountId, Long accountId, String requestId) {
        List<Object[]> results = accountActionHistoryRepo.findAccountActionHistoriesByCriteria(executeAccountId, accountId, requestId);
        List<AccountActionHistoryDto> dtos = new ArrayList<>();
        for (Object[] result : results) {
            AccountActionHistoryDto dto = new AccountActionHistoryDto(
                (String) result[0],
                (String) result[1],
                (BigDecimal) result[2],
                (String) result[3],
                (BigDecimal) result[4],
                (String) result[5],
                (String) result[6]
            );
            dtos.add(dto);
        }
        return dtos;
    }

	@Transactional
	public List<AccountEntity> getExecuteAccountList(Long roleId ) {
		return accountRepo.findByRoleIdOrderByUserIdAsc(roleId);
		
	}

	@Transactional
	public List<AccountEntity> getAccountList() {		
		return accountRepo.findAllByOrderByUserIdAsc();
	}
	

}
