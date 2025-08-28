package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twm.mgmt.persistence.dao.AccountDao;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long>, AccountDao {

	/**
	 * 依EMAIL取得帳號
	 * 
	 * @param email
	 * @return
	 */
	@Query("SELECT a FROM AccountEntity a WHERE LOWER(a.email) = LOWER(:email)")
	List<AccountEntity> findByEmail(@Param("email") String email);

	/**
	 * 依使用者ID取得已啟用的帳號
	 * 
	 * @param userId
	 * @return
	 */
	@Query("SELECT a FROM AccountEntity a WHERE LOWER(a.userId) = LOWER(:userId) AND a.enabled = 'Y'")
	List<AccountEntity> findByUserId(@Param("userId") String userId);

	/**
	 * 取得已啟用的帳號
	 * 
	 * @return
	 */
	@Query("SELECT a FROM AccountEntity a WHERE a.enabled = 'Y' ORDER BY a.userName")
	List<AccountEntity> findEnabledAccount();

	/**
	 * 更新帳號狀態
	 * 
	 * @param accountId
	 * @param enabled
	 * @param updateAccount
	 * @return
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE AccountEntity a SET a.enabled = :enabled, a.updateDate = SYSDATE, a.updateAccount = :updateAccount WHERE a.accountId = :accountId")
	int updateEnabledByAccountId(@Param("accountId") Long accountId, @Param("enabled") String enabled,
			@Param("updateAccount") Long updateAccount);

	/**
	 * 取得已啟用且有簽核權限的部門同仁不包含指定的人
	 * 
	 * @param accountIds
	 * @param departmentId
	 * @return
	 */
	// @Query("SELECT a FROM AccountEntity a WHERE a.accountId NOT IN (?1) AND
	// a.departmentId = ?2 AND a.approvable = 'Y' AND a.enabled = 'Y' ORDER BY
	// a.userName")//<v20210621.M1.M_Replaced> 不懂
	@Query("SELECT a FROM AccountEntity a Where a.approvable = 'Y' AND a.enabled = 'Y' ORDER BY a.userName ") // <v20210621.M1.M_Replacer>
	List<AccountEntity> findByDepartmentId(@Param("accountIds") List<Long> accountIds,
			@Param("departmentId") Long departmentId);

	/**
	 * 取得已啟用的部門同仁
	 * 
	 * @param departmentId
	 * @return
	 */
	@Query("SELECT a FROM AccountEntity a WHERE a.departmentId = :departmentId AND a.enabled = 'Y' ORDER BY a.userName")
	List<AccountEntity> findByDepartmentId(@Param("departmentId") Long departmentId);

	/**
	 * 依EMAIL取得帳號
	 * 
	 * @param email
	 * @return
	 */
	@Query("SELECT a FROM AccountEntity a WHERE a.accountId=:accountid")
	List<AccountEntity> findByAccountID(@Param("accountid") Long accountid);

	@Query("SELECT a FROM AccountEntity a WHERE a.accountId=:accountid")
	AccountEntity findByAccountID1(@Param("accountid") Long accountid);

	@Query("SELECT ac FROM AccountEntity ac, RoleEntity ro, DepartmentEntity dp " + "WHERE ac.roleId = ro.roleId "
			+ "and ac.departmentId = dp.departmentId and ac.enabled = 'Y' "
			+ "and ro.roleId = 25 and dp.enabled = 'Y' and dp.buTag = :tag")
	/*
	 * @Query("SELECT ac  FROM AccountEntity ac WHERE " +
	 * "EXISTS (SELECT 1 FROM RoleEntity ro WHERE ac.roleId = ro.roleId AND ro.roleId = 25) "
	 * +
	 * "AND EXISTS (SELECT 1 FROM DepartmentEntity dp WHERE ac.departmentId = dp.departmentId and dp.enabled = 'Y') "
	 * + "AND ac.enabled = 'Y' AND dp.buTag = ?1")
	 */
	List<AccountEntity> findByBuTag(@Param("tag") String tag);

	/**
	 * 取得跨部門負責人
	 * 
	 * @return
	 */
	@Query("SELECT act FROM DepartmentEntity dp,AccountEntity act WHERE act.departmentId = dp.departmentId and dp.buTag =:buTag and act.accountId !=:accountid")
	List<AccountEntity> findByBuTagAndAccountId(@Param("buTag") String buTag, @Param("accountid") Long accountid);

	/**
	 * 取得已啟用且有簽核權限的部門同仁不包含指定的人
	 * 
	 * @param accountIds
	 * @param departmentId
	 * @return
	 */
	@Query("SELECT a FROM AccountEntity a WHERE a.accountId NOT IN (:accountIds) AND a.departmentId = :departmentId AND a.approvable = 'Y' AND a.enabled = 'Y' ORDER BY a.userName")
	List<AccountEntity> findByDepartmentIdAndApprovable(@Param("accountIds") List<Long> accountIds,
			@Param("departmentId") Long departmentId);

	/**
	 * 取得被設定者
	 * 
	 * @return
	 */
	List<AccountEntity> findAllByOrderByUserIdAsc();

	/**
	 * 取得設定者
	 * 
	 * @param roleId
	 * @return
	 */
	List<AccountEntity> findByRoleIdOrderByUserIdAsc(Long roleId);

	@Query(value = "SELECT a.USER_NAME, a.ACCOUNT_ID FROM MOMOAPI.ACCOUNT a LEFT JOIN "
			+ "(SELECT sab.CREATE_ACCOUNT AS ca FROM momoapi.SERIAL_APPROVAL_BATCH sab "
			+ "WHERE (sab.STATUS = 'WAIT_APPROVAL' AND sab.L1_ACCOUNT = :accountId) "
			+ "OR (sab.L1_STATUS = 'WAIT_APPROVAL' AND sab.L1_ACCOUNT = :accountId) "
			+ "OR (sab.L2_STATUS = 'WAIT_APPROVAL' AND sab.L2_ACCOUNT = :accountId) "
			+ "OR (sab.L1_STATUS = 'WAIT_APPROVAL' AND sab.CREATE_ACCOUNT = :accountId) "
			+ "OR (sab.L2_STATUS = 'WAIT_APPROVAL' AND sab.CREATE_ACCOUNT = :accountId) "
			+ "OR (sab.L2_STATUS = 'WAIT_REWARD' AND sab.CREATE_ACCOUNT = :accountId) "
			+ "GROUP BY sab.CREATE_ACCOUNT) id ON a.ACCOUNT_ID = id.ca "
			+ "WHERE a.ACCOUNT_ID = id.ca", nativeQuery = true)
	List<Object[]> findAccountsByAccountId(@Param("accountId") Long accountId);

}
