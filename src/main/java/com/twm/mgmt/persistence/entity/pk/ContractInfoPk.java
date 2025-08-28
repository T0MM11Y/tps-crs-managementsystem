package com.twm.mgmt.persistence.entity.pk;

import java.io.Serializable;
import java.math.BigDecimal;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class ContractInfoPk implements Serializable {



	private String subid;
	

	private String projectCode;
	

	private String projectSeqNbr;
	

	private BigDecimal contractId;

}
