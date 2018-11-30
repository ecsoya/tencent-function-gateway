/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ecsoya.cloud.function.scf.api.annotation.GatewayFunction;
import org.ecsoya.cloud.function.scf.api.model.Failure;
import org.ecsoya.cloud.function.scf.api.model.Gateway;
import org.ecsoya.cloud.function.scf.api.model.RequestContext;
import org.ecsoya.cloud.function.scf.api.model.Response;
import org.ecsoya.cloud.function.scf.api.util.LoggerFactory;

import com.qcloud.scf.runtime.Context;

public class LambdaGatewayFunction implements IGatewayFunction {

	public static final int ERROR = 40300;

	private AtomicBoolean initialized = new AtomicBoolean();

	private final Map<String, Function<Gateway, Response>> functions;
	private final Map<String, Method> functionMethods;

	protected final Logger logger;

	public LambdaGatewayFunction() {
		this.functions = new HashMap<>();
		this.functionMethods = new HashMap<>();
		this.logger = LoggerFactory.getLogger();
	}

	protected boolean initialize(Context context) {
		info("Initialize...");
		if (!this.initialized.compareAndSet(false, true)) {
			return false;
		}
		info("Register Functions:");
		registerFunction(this);
		return true;
	}

	@SuppressWarnings("unchecked")
	protected void registerFunction(Object functionHolder) {
		if (functionHolder == null) {
			return;
		}
		Map<String, Method> gatewayFunctions = getGatewayFunctions(functionHolder.getClass());
		Set<Entry<String, Method>> entrySet = gatewayFunctions.entrySet();
		for (Entry<String, Method> entry : entrySet) {
			String key = entry.getKey();
			Method method = entry.getValue();
			try {
				method.setAccessible(true);
				Object value = method.invoke(functionHolder);
				if (!(value instanceof Function<?, ?>)) {
					error("Function registered failed for '" + key
							+ "', please make sure the return type is Function<Gateway, Response>.");
					continue;
				}
				registerFunction(key, (Function<Gateway, Response>) value, method);
			} catch (Exception e) {
				error("Function registered failed for '" + key
						+ "', please make sure the return type is Function<Gateway, Response>.");
				continue;
			}
		}
	}

	protected void registerFunction(String key, Function<Gateway, Response> function, Method method) {
		if (key == null || function == null) {
			return;
		}
		functions.put(key, function);
		functionMethods.put(key, method);
		info("Register Function: " + key);
	}

	protected Function<Gateway, Response> lookup(String name) {
		if (name == null) {
			return null;
		}
		return functions.get(name);
	}

	protected Method lookupFunctionImpl(String name) {
		if (name == null) {
			return null;
		}
		return functionMethods.get(name);
	}

	private Map<String, Method> getGatewayFunctions(Class<?> type) {
		if (type == null) {
			return Collections.emptyMap();
		}
		final Map<String, Method> methods = new HashMap<String, Method>();
		Class<?> clazz = type;
		while (clazz != Object.class) {
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for (final Method method : declaredMethods) {
				if (method.isAnnotationPresent(GatewayFunction.class)) {
					GatewayFunction func = method.getAnnotation(GatewayFunction.class);
					methods.put(func.path(), method);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return methods;
	}

	protected void info(String msg) {
		logger.info(msg);
	}

	protected void error(String msg) {
		logger.severe(msg);
	}

	protected void debug(String msg) {
		logger.fine(msg);
	}

	protected void error(String msg, Throwable thrown) {
		logger.log(Level.SEVERE, msg, thrown);
	}

	protected void warn(String msg) {
		logger.warning(msg);
	}

	@Override
	public Response handleRequest(Gateway gateway, Context context) {
		if (gateway == null) {
			return fail(ERROR, "event is not come from api gateway (gateway)");
		}
		RequestContext requestContext = gateway.getRequestContext();
		if (requestContext == null) {
			return fail(ERROR, "event is not come from api gateway (requestContext)");
		}
		String name = requestContext.getPath();
		if (name == null) {
			return fail(ERROR, "event is not come from api gateway (path)");
		}
		info("Begin to handle gateway path=" + name);

		initialize(context);

		String result = validateFunction(name, gateway);
		if (result != null) {
			error("Function validate failed");
			return fail(401, result);
		}

		Function<Gateway, Response> function = lookup(name);

		if (function == null) {
			error("Function could not be found for " + name);
			return fail(ERROR, "Function could not be found for " + name);
		}
		try {
			return function.apply(gateway);
		} catch (Throwable ex) {
			error(getClass().getName() + " failed: " + ex.getLocalizedMessage());
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}
			if (ex instanceof Error) {
				throw (Error) ex;
			}
			throw new UndeclaredThrowableException(ex);
		} finally {
			info("Finished to handle gateway path=" + name);
		}
	}

	protected String validateFunction(String name, Gateway gateway) {
		return null;
	}

	public Failure fail(int errorCode, String errorMsg) {
		return new Failure(errorCode, errorMsg);
	}

	public Response success(Object data) {
		return new Response(200, data);
	}

	public Logger getLogger() {
		return logger;
	}
}
