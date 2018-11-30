/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.ecsoya.cloud.function.scf.api.util.ReflectUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Gateway extends JsonModel {

	private RequestContext requestContext;

	private String body;

	private String path;

	private String query;

	private Map<String, String> queryString;

	private HttpMethod httpMethod;

	private Map<String, String> headers;
	private Map<String, String> pathParameters;
	private Map<String, String> queryStringParameters;
	private Map<String, String> headerParameters;
	private Map<String, String> stageVariables;

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public JSONObject parseBody() {
		if (body == null) {
			return null;
		}
		JSONParser parser = getJSONParser();
		try {
			Object value = parser.parse(body);
			if (value instanceof JSONObject) {
				return (JSONObject) value;
			}
		} catch (ParseException e) {
		}
		return null;

	}

	public <T> T parseBody(Class<T> type) {
		if (body == null || type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
			return null;
		}
		JSONObject json = parseBody();
		if (json == null) {
			return null;
		}
		try {
			JSONObject target = new JSONObject();
			List<Field> fields = ReflectUtil.getAllFields(type);
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				if (!json.containsKey(name)) {
					continue;
				}
				Object value = json.get(name);
				target.appendField(name, value);
			}
			JSONParser parser = getJSONParser();
			return parser.parse(target.toJSONString(), type);
		} catch (Exception e) {
			return null;
		}
	}

	private JSONParser getJSONParser() {
		return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Map<String, String> getPathParameters() {
		return pathParameters;
	}

	public String getPath(String key) {
		if (key == null || pathParameters == null) {
			return null;
		}
		return pathParameters.get(key);
	}

	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}

	public Map<String, String> getQueryStringParameters() {
		return queryStringParameters;
	}

	public void setQueryStringParameters(Map<String, String> queryStringParameters) {
		this.queryStringParameters = queryStringParameters;
	}

	public String getQuery(String key) {
		if (key == null) {
			return null;
		}
		if (queryStringParameters != null && queryStringParameters.containsKey(key)) {
			return queryStringParameters.get(key);
		}
		if (queryString != null && queryString.containsKey(key)) {
			return queryString.get(key);
		}
		return null;
	}

	public Map<String, String> getHeaderParameters() {
		return headerParameters;
	}

	public void setHeaderParameters(Map<String, String> headerParameters) {
		this.headerParameters = headerParameters;
	}

	public String getHeader(String key) {
		if (key == null) {
			return null;
		}
		if (headers != null && headers.containsKey(key)) {
			return headers.get(key);
		}
		if (headerParameters != null && headerParameters.containsKey(key)) {
			return headerParameters.get(key);
		}
		return null;
	}

	public Map<String, String> getStageVariables() {
		return stageVariables;
	}

	public void setStageVariables(Map<String, String> stageVariables) {
		this.stageVariables = stageVariables;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getQueryString() {
		return queryString;
	}

	public void setQueryString(Map<String, String> queryString) {
		this.queryString = queryString;
	}

	public String getHost() {
		return getHeader("origin");
	}

}
