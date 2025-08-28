package com.twm.mgmt.model.account;

import java.io.Serializable;
import java.util.Date;

import com.twm.mgmt.persistence.dto.RedeemTotalApiDto;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class RedeemTotalApiResultVo implements Serializable {
	String moDeptNo;
	String apiUrl;
	Date createDate;
	
	public RedeemTotalApiResultVo(RedeemTotalApiDto dto) {
		this.moDeptNo = dto.getMoDeptNo();
		this.apiUrl = dto.getApiUrl();
		this.createDate = dto.getCreateDate();//DateUtilsEx.formatDate(dto.getCreateDate());
	}
}
