/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api;

import org.ecsoya.cloud.function.scf.api.model.Failure;
import org.ecsoya.cloud.function.scf.api.model.Gateway;
import org.ecsoya.cloud.function.scf.api.model.RequestContext;
import org.ecsoya.cloud.function.scf.api.model.Response;
import org.ecsoya.cloud.function.scf.api.util.LoggerFactory;

import com.qcloud.scf.runtime.Context;

public abstract class DefaultGatewayFunction implements IGatewayFunction {

	public Response handleRequest(Gateway gateway, Context context) {
		if (gateway == null) {
			return fail(401, "event is not come from api gateway");
		}
		RequestContext requestContext = gateway.getRequestContext();
		if (requestContext == null) {
			return fail(402, "event is not come from api gateway");
		}
		String path = requestContext.getPath();
		if (path != null) {
			return handleRequest(path, gateway, context);
		}
		return fail(409, "request is not correctly execute");
	}

	protected abstract Response handleRequest(String path, Gateway gateway, Context context);

	protected Failure fail(int errorCode, String errorMsg) {
		return new Failure(errorCode, errorMsg);
	}

	protected Response success(Object data) {
		return new Response(200, data);
	}

	protected void info(String msg) {
		LoggerFactory.getLogger().info(msg);
	}

	protected void error(String msg) {
		LoggerFactory.getLogger().severe(msg);
	}

	protected void debug(String msg) {
		LoggerFactory.getLogger().fine(msg);
	}
}
