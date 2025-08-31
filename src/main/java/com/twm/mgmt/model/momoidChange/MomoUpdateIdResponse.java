package com.twm.mgmt.model.momoidChange;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class MomoUpdateIdResponse implements Serializable {

	@JsonProperty(value = "WARNING_ID")
	private String WARNING_ID;
	@JsonProperty(value = "SMS_DATE")
	private String SMS_DATE;
	@JsonProperty(value = "STATUS")
	private Integer STATUS;

}
