package com.twm.mgmt.model.momoidChange;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.twm.mgmt.model.common.PaginationVo;
import com.twm.mgmt.model.common.UserInfoVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class MomoidChangeVo  extends PaginationVo  implements Serializable  {
	
	private Long approvalId;
	
	private UserInfoVo userInfoVo;
	
	private List<Map<String,Object>> listMap ;
	private String[] momoidChangeFileNames;
	private byte[][] momoidChangeFileBytes;
	
	private String departmentId;
	private String userName;
	private String status;
	private String momoidChangeMainId;
	private String createDateStart;
	private String createDateEnd;
	private String smsDateStart;
	private String smsDateEnd;
	
	private Long accountId;
	
	private String opinion;
	private String commentInfo;
	private String momoidChangeApprovalId;
	private String index;
	
	private String phoneNumber;
	private String projectCode;
	private String projectSeqNbr;
	private String subid;
	private String sendReason;
	private String momoMemberId;
	
	private Integer aBtnsIndex;
	


}
