package com.twm.mgmt.persistence.dao;


import java.util.List;


import com.twm.mgmt.model.momoidChange.MomoidChangeVo;
import com.twm.mgmt.persistence.dto.MomoidChangeMainDto;


public interface MomoidChangeMainDao {

	List<MomoidChangeMainDto> findByMomoidChange(MomoidChangeVo momoidChangeVo);
	List<MomoidChangeMainDto> findMomoidChangeApproval(MomoidChangeVo momoidChangeVo);
	MomoidChangeMainDto signOffFlowChart(String approvalId);


}
