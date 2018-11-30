/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.model;

import net.minidev.json.JSONObject;

public class Failure extends Response {

	private String errorMsg;

	public Failure() {
		super();
	}

	public Failure(int errorCode, String errorMsg) {
		super(errorCode, null);
		this.errorMsg = errorMsg;
	}

	public int getErrorCode() {
		return getStatusCode();
	}

	public void setErrorCode(int errorCode) {
		setStatusCode(errorCode);
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toJSONString() {
		JSONObject object = new JSONObject();
		object.put("errorCode", getErrorCode());
		object.put("errorMsg", getErrorMsg());
		return object.toJSONString();
	}

}
