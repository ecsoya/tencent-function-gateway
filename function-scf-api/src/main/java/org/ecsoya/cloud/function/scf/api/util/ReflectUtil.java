/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectUtil {

	public static List<Field> getAllFields(Class<?> type) {
		if (type == null || Object.class.equals(type)) {
			return Collections.emptyList();
		}
		List<Field> fields = new ArrayList<Field>();
		Field[] declaredFields = type.getDeclaredFields();
		fields.addAll(Arrays.asList(declaredFields));

		Class<?> superclass = type.getSuperclass();
		if (!Object.class.equals(superclass)) {
			fields.addAll(getAllFields(superclass));
		}
		return fields;
	}
}
