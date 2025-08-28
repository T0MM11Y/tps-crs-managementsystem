package com.twm.mgmt.aspect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.model.account.APAccountVo;
import com.twm.mgmt.model.account.APKeyIvVo;
import com.twm.mgmt.model.account.AccountVo;
import com.twm.mgmt.model.account.MOAccountVo;
import com.twm.mgmt.persistence.entity.APKeyIvEntity;
import com.twm.mgmt.persistence.entity.AccountActionHistoryEntity;
import com.twm.mgmt.persistence.entity.AccountActionHistoryEntity.AccountActionHistoryEntityBuilder;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;
import com.twm.mgmt.persistence.repository.AccountActionHistoryRepository;
import com.twm.mgmt.persistence.repository.AccountRepository;
import com.twm.mgmt.service.AccountService;
import com.twm.mgmt.service.BaseService;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.RandomUtil;



@Aspect
@Component
public class AccountActionHistoryAspect extends BaseService {
	
	@Autowired
	protected AccountRepository accountRepo;
	
	@Value("${ap.account.secrect.key}")
	private String screctKey;

	@Value("${ap.account.secrect.iv}")
	private String screctIv;
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.saveOrUpdateAccount(..))")
	public void afterSaveOrUpdateAccount(JoinPoint joinpoint) {
		AccountVo vo = (AccountVo)joinpoint.getArgs()[0];
		ActionType action = vo.getAction();
		
		
		Date date = new Date();
		AccountActionHistoryEntityBuilder accountActionHistoryEntityBuilder = AccountActionHistoryEntity.builder().requestId(vo.getRequestId()).executeAccountId(getAccountId()).executeDate(date);
		
		if (action.isAdd()) {
			String email = vo.getEmail();
			AccountEntity accountEntity = accountRepo.findByEmail(email).get(0);
			accountActionHistoryEntityBuilder.accountId(accountEntity.getAccountId());						
			accountActionHistoryEntityBuilder.executeContent("[新增帳號]簽核核決權限:"+vo.getApprovable()+";角色權限:"+roleRepo.findById(vo.getRoleId()).orElseGet(() -> new RoleEntity()).getRoleName()+";部門別:"+departmentRepo.findById(vo.getDepartmentId()).orElseGet(() -> new DepartmentEntity()).getDepartmentName());
		} else {
			accountActionHistoryEntityBuilder.accountId(vo.getAccountId());
			accountActionHistoryEntityBuilder.executeContent("[編輯帳號]簽核核決權限:"+vo.getApprovable()+";角色權限:"+roleRepo.findById(vo.getRoleId()).orElseGet(() -> new RoleEntity()).getRoleName()+";部門別:"+departmentRepo.findById(vo.getDepartmentId()).orElseGet(() -> new DepartmentEntity()).getDepartmentName());
		}
		
		AccountActionHistoryEntity accountActionHistoryEntity = accountActionHistoryEntityBuilder.build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.saveOrUpdateMOAccount(..))")
	public void aftersaveOrUpdateMOAccount(JoinPoint joinpoint) {
		MOAccountVo vo = (MOAccountVo)joinpoint.getArgs()[0];
		ActionType action = vo.getAction();		
		Date date = new Date();
		AccountActionHistoryEntityBuilder accountActionHistoryEntityBuilder = AccountActionHistoryEntity.builder().requestId(vo.getRequestId()).accountId(vo.getContactAccountId()).executeAccountId(getAccountId()).executeDate(date);		
		
		if (action.isAdd()) {
			accountActionHistoryEntityBuilder.executeContent("[新增MO幣系統帳號]部門別:"+departmentRepo.findById(vo.getDepartmentId()).orElseGet(() -> new DepartmentEntity()).getDepartmentName()+";MO幣系統窗口:"+accountRepo.findById(vo.getContactAccountId()).orElseGet(() -> new AccountEntity()).getUserName()+";MOMO廠商代碼:"+vo.getMerchantId()+";MOMO公司單位:"+vo.getDeptNo()+";MOMO API KEY:"+vo.getApiKey()+";MOMO 資料加密 KEY:"+vo.getEncryptedKey());
		} else {
			accountActionHistoryEntityBuilder.executeContent("[編輯MO幣系統帳號]部門別:"+departmentRepo.findById(vo.getDepartmentId()).orElseGet(() -> new DepartmentEntity()).getDepartmentName()+";MO幣系統窗口:"+accountRepo.findById(vo.getContactAccountId()).orElseGet(() -> new AccountEntity()).getUserName()+";MOMO廠商代碼:"+vo.getMerchantId()+";MOMO公司單位:"+vo.getDeptNo()+";MOMO API KEY:"+vo.getApiKey()+";MOMO 資料加密 KEY:"+vo.getEncryptedKey());
		}
		
		AccountActionHistoryEntity accountActionHistoryEntity = accountActionHistoryEntityBuilder.build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.insertMoAccountMapAccount(..))")
	public void afterInsertMoAccountMapAccount(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		Date date = new Date();		
		AccountActionHistoryEntity accountActionHistoryEntity = AccountActionHistoryEntity.builder().requestId((String)args[4]).accountId(Long.valueOf((String)args[3])).executeAccountId(getAccountId()).executeDate(date).executeContent("[MOMO 單位對應-新增]功能:"+(String)args[5]+";MOMO 公司單位:"+(String)args[2]+";CRS 用戶帳號:"+accountRepo.findById(Long.valueOf((String)args[3])).orElseGet(() -> new AccountEntity()).getUserName()).build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.deleteMoAccountMapAccount(..))")
	public void afterDeleteMoAccountMapAccount(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		Date date = new Date();
		MOAccountEntity result = mOAccountRepo.findBydepartmentId1((String)args[2]);
		DepartmentEntity entity1 = departmentRepo.findBydepartmentId(result.getDepartmentId());			
		AccountActionHistoryEntity accountActionHistoryEntity = AccountActionHistoryEntity.builder().requestId((String)args[4]).accountId(Long.valueOf((String)args[3])).executeAccountId(getAccountId()).executeDate(date).executeContent("[MOMO 單位對應-刪除]功能:"+(String)args[5]+";MOMO 公司單位:"+(String)args[2]+"_"+entity1.getDepartmentName()+";CRS 用戶帳號:"+accountRepo.findById(Long.valueOf((String)args[3])).orElseGet(() -> new AccountEntity()).getUserName()).build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.saveOrUpdateAPAccount(..))")
	public void afterSaveOrUpdateAPAccount(JoinPoint joinpoint) throws Exception {
		APAccountVo vo = (APAccountVo)joinpoint.getArgs()[0];
		ActionType action = vo.getAction();		
		Date date = new Date();
		AccountActionHistoryEntityBuilder accountActionHistoryEntityBuilder = AccountActionHistoryEntity.builder().requestId(vo.getRequestId()).accountId(0L).executeAccountId(getAccountId()).executeDate(date);
		
		
		List<APKeyIvEntity> entities = apKeyIvRepo.find2RowBySourceId(vo.getSourceId());		
		String keyIvs = "";		
		if (CollectionUtils.isNotEmpty(entities)) {			
			for (APKeyIvEntity keyIvEntity : entities) {												
				String expiredDate = DateUtilsEx.formatDate(keyIvEntity.getExpiredDate(), "yyyy-MM-dd");				
				String key = keyIvEntity.getKey();				
				String iv = keyIvEntity.getIv();
				keyIvs = keyIvs + ";到期日:" + expiredDate + ";KEY:" + AESUtil.decryptStr(key, screctKey, screctIv) +";IV:" + AESUtil.decryptStr(iv, screctKey, screctIv);
			}			
		}
		
		if (action.isAdd()) {
			
			accountActionHistoryEntityBuilder.executeContent("[新增 AP 帳號]部門別:"+departmentRepo.findById(vo.getDepartmentId()).orElseGet(() -> new DepartmentEntity()).getDepartmentName()+";AP 帳號:"+vo.getSourceId()+";AP 帳號狀態:"+vo.getEnabled()+keyIvs+";帳號保管人姓名:"+vo.getContactAccountId());
		} else {
			accountActionHistoryEntityBuilder.executeContent("[編輯 AP 帳號]部門別:"+departmentRepo.findById(vo.getDepartmentId()).orElseGet(() -> new DepartmentEntity()).getDepartmentName()+";AP 帳號:"+vo.getSourceId()+";AP 帳號狀態:"+vo.getEnabled()+keyIvs+";帳號保管人姓名:"+vo.getContactAccountId());
		}
		
		AccountActionHistoryEntity accountActionHistoryEntity = accountActionHistoryEntityBuilder.build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.updateAccountStatus(..))")
	public void afterUpdateAccountStatus(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		Date date = new Date();
		
		AccountActionHistoryEntity accountActionHistoryEntity = AccountActionHistoryEntity.builder().requestId((String)args[2]).accountId((Long)args[0]).executeAccountId(getAccountId()).executeDate(date).executeContent("[編輯帳號]使用者:"+accountRepo.findById((Long)args[0]).orElseGet(() -> new AccountEntity()).getUserName()+";停用/啟用:"+(String)args[1]).build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AlertFor90dayLoginService.disableAccount(..))")
	public void afterDisableAccount(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		Date date = new Date();		
		AccountActionHistoryEntity accountActionHistoryEntity = AccountActionHistoryEntity.builder().requestId("此為排程執行90天未登入停用").accountId(((BigDecimal)args[0]).longValue()).executeAccountId(0L).executeDate(date).executeContent("[編輯帳號]使用者:"+accountRepo.findById(((BigDecimal)args[0]).longValue()).orElseGet(() -> new AccountEntity()).getUserName()+";停用/啟用:"+"N").build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.insertRedeemTotalApi(..))")
	public void afterInsertRedeemTotalApi(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		Date date = new Date();		
		AccountActionHistoryEntity accountActionHistoryEntity = AccountActionHistoryEntity.builder().requestId((String)args[3]).accountId(getAccountId()).executeAccountId(getAccountId()).executeDate(date).executeContent("[每月兌幣總額API開關-新增]MOMO 公司單位:"+(String)args[1]+";呼叫的API:"+(String)args[2]).build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}
	
	@AfterReturning("execution(* com.twm.mgmt.service.AccountService.deleteRedeemTotalApi(..))")
	public void afterDeleteRedeemTotalApi(JoinPoint joinpoint) {
		Object[] args = joinpoint.getArgs();
		Date date = new Date();		
		AccountActionHistoryEntity accountActionHistoryEntity = AccountActionHistoryEntity.builder().requestId((String)args[3]).accountId(getAccountId()).executeAccountId(getAccountId()).executeDate(date).executeContent("[每月兌幣總額API開關-刪除]MOMO 公司單位:"+(String)args[1]+";呼叫的API:"+(String)args[2]).build();
		accountActionHistoryRepo.save(accountActionHistoryEntity);
	}

}
