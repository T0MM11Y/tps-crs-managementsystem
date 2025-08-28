package com.twm.mgmt.persistence.entity.pk;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class MomoidChangeListPk implements Serializable {


	private BigDecimal momoidChangeMainId;
	

	private String projectCode;
	

	private String projectSeqNbr;
	

	private String subid;

	private BigDecimal momoidChangeListId;
	

}
