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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.twm.mgmt.model.account.PermissionVo;
import com.twm.mgmt.model.account.RoleVo;
import com.twm.mgmt.model.common.MenuVo;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.SubMenuVo;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.model.report.MomoeventVo;
import com.twm.mgmt.model.report.PayaccountVo;
import com.twm.mgmt.model.report.CheckbillVo;
import com.twm.mgmt.model.usermanage.*;
import com.twm.mgmt.persistence.dto.APAccountDto;
import com.twm.mgmt.persistence.dto.AccountDto;
import com.twm.mgmt.persistence.dto.ExchangecurrencyDto;
import com.twm.mgmt.persistence.dto.MOAccountDto;
import com.twm.mgmt.persistence.entity.APAccountEntity;
import com.twm.mgmt.persistence.entity.APKeyIvEntity;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.AccountPermissionProgramEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.entity.MenuEntity;
import com.twm.mgmt.persistence.entity.ProgramEntity;
import com.twm.mgmt.persistence.entity.RewardReportEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;
import com.twm.mgmt.persistence.entity.RolePermissionProgramEntity;
import com.twm.mgmt.persistence.entity.pk.APAccountPk;
import com.twm.mgmt.persistence.repository.APAccountRepository;
import com.twm.mgmt.persistence.repository.APKeyIvRepository;
import com.twm.mgmt.persistence.repository.AccountPermissionProgramRepository;
import com.twm.mgmt.persistence.repository.CampaignBlockListRepository;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MOAccountRepository;
import com.twm.mgmt.persistence.repository.MenuRepository;
import com.twm.mgmt.persistence.repository.RolePermissionProgramRepository;
import com.twm.mgmt.persistence.repository.RoleRepository;
import com.twm.mgmt.persistence.repository.TransacionofferhistoryRepository;
import com.twm.mgmt.persistence.repository.RewardReportRepository;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.RandomUtil;
import com.twm.mgmt.utils.StringUtilsEx;

@Service
public class UserManageService extends BaseService {

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
	private CampaignBlockListRepository campaignblocklistRepo;

	@Autowired
	private DepartmentRepository departmentRepo;

	@Autowired
	private APAccountRepository apAccountRepo;

	@Autowired
	private APKeyIvRepository apKeyIvRepo;

	@Autowired
	private MOAccountRepository moAccountRepo;
	
	@Autowired
	private RewardReportRepository rewardreportRepo;

	@Autowired
	private TransacionofferhistoryRepository xxrepo;
	
	public QueryResultVo findexchangecurrencyList(ExchangecurrencyVo condition, UserInfoVo xx) {
		//System.out.println("findexchangecurrencyList0");
		QueryResultVo resultVo = new QueryResultVo(condition);

		
		List<ExchangecurrencyDto> result = xxrepo.findByCondition(condition);
		
		//SSOLogin
		AccountEntity accountEntity = accountRepo.findByAccountID1(xx.getAccountId());
//		AccountEntity accountEntity = accountRepo.findByAccountID1((long)1);
		
		if(accountEntity.getRoleId().toString().equals("25")) { //看角色
			for(int i =0; i<= result.size()-1 ; i++) {
				
				//SSOLogin
				if(!result.get(i).getDepartmentId().equals(xx.getDepartmentName())) {
					result.remove(i--);
				}
				
//				if(!result.get(i).getDepartmentId().equals("語音服務開發部")) {
//					result.remove(i--);
//				}
			}
		}

		for(ExchangecurrencyDto x: result) {

			if(condition.getStatus().equals("checkboxA")) {
				if(x.getRemardstatus().equals("WAIT_BEST_CHECK")) {
					x.setRemardstatus("待擇優");
				}
				if(x.getRemardstatus().equals("WAIT_OFFER")) {
					x.setRemardstatus("待發幣");
				}
				if(x.getRemardstatus().equals("OFFERING")) {
					x.setRemardstatus("發幣中");
				}
				if(x.getRemardstatus().equals("SUCCESS_OFFER")) {
					x.setRemardstatus("發幣成功");
				}
				if(x.getRemardstatus().equals("FAIL_OFFER")) {
					x.setRemardstatus("發幣失敗");
				}
				if(x.getRemardstatus().equals("FAIL_CHECK")) {
					x.setRemardstatus("檢核失敗");
				}
				
				if(campaignblocklistRepo.ExchangeCurrencynum(BigDecimal.valueOf(Integer.parseInt(x.getCampaignDetailID())), x.getTwmuid()) > 0) {
					x.setIsblock("是");
				}else {
					x.setIsblock("否");
				}
			}
			if(condition.getStatus().equals("checkboxB")) {
			
				if(x.getRemardstatus().equals("SUCCESS_REDEEM")) {
					x.setRemardstatus("兌幣成功");
				}
				if(x.getRemardstatus().equals("FAIL_REDEEM")) {
					x.setRemardstatus("兌幣失敗");
				}
				if(x.getRemardstatus().equals("CANCEL_REDEEM")) {
					x.setRemardstatus("兌幣取消");
				}
				if(x.getRemardstatus().equals("FAIL_CHECK")) {
					x.setRemardstatus("名單檢核失敗");
				}
				if(x.getRemardstatus().equals("IS_BEST")) {
					x.setRemardstatus("名單被擇優");
				}
				
				
				if(campaignblocklistRepo.ExchangeCurrencynum(BigDecimal.valueOf(Integer.parseInt(x.getCampaignDetailID())), x.getTwmuid1())>0) {
					x.setIsblock("是");
				}else {
					x.setIsblock("否");
				}
			}	

		}

		resultVo.setTotal(result.size());
		resultVo.setResult(result);
		return resultVo;

	}

	
	
	
	public QueryResultVo findexchangecurrencyListB(ExchangecurrencyVo condition, UserInfoVo xx) {
		//System.out.println("findexchangecurrencyList0");
		QueryResultVo resultVo = new QueryResultVo(condition);

		List<ExchangecurrencyDto> result = xxrepo.findByCondition(condition);
		
		//SSOLogin
		AccountEntity accountEntity = accountRepo.findByAccountID1(xx.getAccountId());
//		AccountEntity accountEntity = accountRepo.findByAccountID1((long)1);
		
		if(accountEntity.getRoleId().toString().equals("25")) { //看角色
			for(int i =0; i<= result.size()-1 ; i++) {
				
				//SSOLogin
				if(!result.get(i).getDepartmentId().equals(xx.getDepartmentName())) {
					result.remove(i--);
				}
//				
//				if(!result.get(i).getDepartmentId().equals("語音服務開發部")) {
//					result.remove(i--);
//				}
			}
		}

		resultVo.setTotal(result.size());
		resultVo.setResult(result);
		return resultVo;

	}
	
	
	
	public List<PayaccountVo> findPayaccountList() {
		List<RewardReportEntity> entities = rewardreportRepo.findEMpayaccount();
		//return entities.stream().map(entity -> new PayaccountVo(entity)).collect(Collectors.toList());
		//System.out.println("PayaccountVosizee11:"+entities.stream().distinct().collect(Collectors.toList()).size());
		return entities.stream().map(entity -> new PayaccountVo(entity)).collect(Collectors.toList());
		
	}
	
	public List<MomoeventVo> findMomoeventList() {
		List<RewardReportEntity> entities = rewardreportRepo.findEMmomoevent();
		//return entities.stream().map(entity -> new PayaccountVo(entity)).collect(Collectors.toList());
		//System.out.println("PayaccountVosizee12:"+entities.stream().distinct().collect(Collectors.toList()).size());
		return entities.stream().map(entity -> new MomoeventVo(entity)).collect(Collectors.toList());
		
	}
	
	








}
