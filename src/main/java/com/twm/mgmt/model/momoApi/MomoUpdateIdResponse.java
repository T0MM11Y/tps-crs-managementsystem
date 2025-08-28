package com.twm.mgmt.model.momoApi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twm.mgmt.model.common.PaginationVo;
import com.twm.mgmt.model.common.UserInfoVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class MomoUpdateIdResponse implements Serializable  {
	
	@JsonProperty(value="WARNING_ID")
	private BigDecimal WARNING_ID;
	@JsonProperty(value="SMS_DATE")
	private String SMS_DATE;
	@JsonProperty(value="STATUS")
	private Integer STATUS;
	@JsonProperty(value="MOMOID_CHANGE_MAIN_ID")
	private BigDecimal MOMOID_CHANGE_MAIN_ID;
	
	


}
