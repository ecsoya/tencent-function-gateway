/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.model;

import java.util.Map;

/**
 * 在 API 网关设置为集成响应时，需要返回类似如下内容的数据结构
 * 
 * 当“是否启用响应集成”启用时，应返回这个格式。
 * 
 * @author Ecsoya
 *
 */
public class IntegratedResponse extends Response {

	private boolean isBase64 = false;

	private Map<String, String> headers;

	private String body;

	public boolean isBase64() {
		return isBase64;
	}

	public void setBase64(boolean isBase64) {
		this.isBase64 = isBase64;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
