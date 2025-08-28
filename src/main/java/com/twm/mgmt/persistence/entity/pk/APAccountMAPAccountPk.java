package com.twm.mgmt.persistence.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuppressWarnings("serial")
public class APAccountMAPAccountPk implements Serializable {

	private Long departmentId;

	private String sourceId;

}
