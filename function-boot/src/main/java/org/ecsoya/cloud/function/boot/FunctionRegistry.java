/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.boot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.ecsoya.cloud.function.scf.api.LambdaGatewayFunction;
import org.ecsoya.cloud.function.scf.api.model.Gateway;
import org.ecsoya.cloud.function.scf.api.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.qcloud.scf.runtime.Context;

import function.DemoGatewayFunction;;

@Component
public class FunctionRegistry {

	private final PathMatcher pathMatcher;

	private Logger logger = LoggerFactory.getLogger(FunctionRegistry.class);

	private Map<String, Function<Gateway, Response>> functions;
	private Map<String, Method> functionMethods;
	private DemoGatewayFunction delegate = new DemoGatewayFunction();

	@SuppressWarnings("unchecked")
	public FunctionRegistry() {
		functions = new HashMap<>();
		pathMatcher = new AntPathMatcher();

		try {
			Method method = LambdaGatewayFunction.class.getDeclaredMethod("initialize", Context.class);
			method.setAccessible(true);
			method.invoke(delegate, createContext());
		} catch (Exception e) {
			logger.error("Could not to initialize CsfxFunction.");
		}
		try {
			Field field = LambdaGatewayFunction.class.getDeclaredField("functions");
			field.setAccessible(true);
			functions = (Map<String, Function<Gateway, Response>>) field.get(delegate);
		} catch (Exception e) {
			logger.error("Could not to get functions from CsfxFunction.");
			functions = new HashMap<>();
		}

		try {
			Field field = LambdaGatewayFunction.class.getDeclaredField("functionMethods");
			field.setAccessible(true);
			functionMethods = (Map<String, Method>) field.get(delegate);
		} catch (Exception e) {
			logger.error("Could not to get functions from CsfxFunction.");
			functionMethods = new HashMap<>();
		}
	}

	public String validateFunction(String name, Gateway gateway) {
		try {
			Method method = LambdaGatewayFunction.class.getDeclaredMethod("validateFunction", String.class,
					Gateway.class);
			method.setAccessible(true);
			return (String) method.invoke(delegate, name, gateway);
		} catch (Exception e) {
			logger.error("Could not to initialize CsfxFunction.");
		}
		return null;
	}

	private static Context createContext() {
		return new Context() {

			@Override
			public int getTimeLimitInMs() {
				return 0;
			}

			@Override
			public String getRequestId() {
				return null;
			}

			@Override
			public int getMemoryLimitInMb() {
				return 0;
			}
		};
	}

	public Set<String> getFunctionNames() {
		return functions.keySet();
	}

	public Function<Gateway, Response> lookup(String name) {
		if (name == null) {
			return null;
		}
		return functions.get(name);
	}

	public Entry<String, Function<Gateway, Response>> match(String path) {
		if (path == null) {
			return null;
		}
		Set<Entry<String, Function<Gateway, Response>>> entrySet = functions.entrySet();
		for (Entry<String, Function<Gateway, Response>> entry : entrySet) {
			String pattern = entry.getKey();
			if (pathMatcher.match(pattern, path)) {
				return entry;
			}
		}
		return null;
	}

	public Map<String, String> buildPathParameters(String contextPath, String path) {
		if (contextPath == null || path == null) {
			return Collections.emptyMap();
		}
		return pathMatcher.extractUriTemplateVariables(contextPath, path);
	}
}
