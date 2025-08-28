package com.twm.mgmt.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.twm.mgmt.model.common.ErrorVo;
import com.twm.mgmt.validator.BaseValidator;

public class CommonUtils {

	@SuppressWarnings("unchecked")
	public static <O extends Object> O xmlStr2Obj(String str, Class<?> clazz) throws JAXBException {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			return (O) unmarshaller.unmarshal(new StringReader(str));
		} catch (JAXBException e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param validators
	 * @return
	 */
	public static Map<String, List<String>> validate(List<BaseValidator> validators) {
		Map<String, List<String>> result = new HashMap<>();

		if (!validators.isEmpty()) {
			for (BaseValidator validator : validators) {
				for (ErrorVo vo : validator.validate()) {
					String fieldId = vo.getId();

					List<String> messages = result.get(fieldId);

					if (messages == null || messages.isEmpty()) {
						messages = new ArrayList<>();
					}

					messages.add(vo.getMessage());

					result.put(vo.getId(), messages);
				}
			}
		}

		return result;
	}

}
