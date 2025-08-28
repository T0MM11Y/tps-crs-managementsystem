package com.twm.mgmt.persistence.entity.pk;

import java.io.Serializable;

import lombok.Data;

@Data
public class SerialCampaignBlockDetailId implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int rowId;
	
	private int blockId;
	
	private String projectCode;
	
	private String subId;
}
