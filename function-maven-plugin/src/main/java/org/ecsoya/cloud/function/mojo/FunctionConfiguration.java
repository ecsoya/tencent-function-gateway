/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.mojo;

import java.util.HashMap;
import java.util.Map;

public class FunctionConfiguration {

	private boolean enabled = true;

	private String name;

	private String handler;

	private String secretId;

	private String secretKey;

	private String region = "ap-guangzhou";

	private BucketConfiguration bucket;

	private String description;

	private int timeout = 60; // s

	private int memory = 128;// m

	private Map<String, String> environments = new HashMap<String, String>();

	public FunctionConfiguration() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public BucketConfiguration getBucket() {
		return bucket;
	}

	public void setBucket(BucketConfiguration bucket) {
		this.bucket = bucket;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public Map<String, String> getEnvironments() {
		return environments;
	}

	public void setEnvironments(Map<String, String> environments) {
		this.environments = environments;
	}

}
