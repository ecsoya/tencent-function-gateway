/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
/**
 * 
 */
package org.ecsoya.cloud.function.mojo;

/**
 * @author Ecsoya
 *
 */
public class GatewayConfiguration {

	private String service;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
	public String toString() {
		return "service: " + service;
	}

}
