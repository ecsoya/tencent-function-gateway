/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.model;

import net.minidev.json.JSONValue;

public abstract class JsonModel {

	@Override
	public String toString() {
		return toJSONString();
	}

	public String toJSONString() {
		return JSONValue.toJSONString(this);
	}

}
