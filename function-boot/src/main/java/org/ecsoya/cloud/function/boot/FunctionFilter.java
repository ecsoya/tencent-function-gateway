/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.boot;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ecsoya.cloud.function.scf.api.model.Gateway;
import org.ecsoya.cloud.function.scf.api.model.HttpMethod;
import org.ecsoya.cloud.function.scf.api.model.RequestContext;
import org.ecsoya.cloud.function.scf.api.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@WebFilter(urlPatterns = "/*", filterName = "functionFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FunctionFilter extends HttpFilter implements Filter {

	private static final long serialVersionUID = 5671031772244827787L;

	@Autowired
	private FunctionRegistry functionRegistry;

	private String body;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// Get body from very beginning.
		if (request instanceof HttpServletRequest) {
			if ("POST".equalsIgnoreCase(((HttpServletRequest) request).getMethod())) {
				try {
					body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		super.doFilter(request, response, chain);
	}

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", header);
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"X-Api-ID,X-Service-RateLimit,X-UsagePlan-RateLimit,X-UsagePlan-Quota,Cache-Control,Connection,Content-Disposition,Date,Keep-Alive,Pragma,Via,Accept,Accept-Charset,Accept-Encoding,Accept-Language,Authorization,Cookie,Expect,From,Host,If-Match,If-Modified-Since,If-None-Match,If-Range,If-Unmodified-Since,Range,Origin,Referer,User-Agent,X-Forwarded-For,X-Forwarded-Host,X-Forwarded-Proto,Accept-Range,Age,Content-Range,Content-Security-Policy,ETag,Expires,Last-Modified,Location,Server,Set-Cookie,Trailer,Transfer-Encoding,Vary,Allow,Content-Encoding,Content-Language,Content-Length,Content-Location,Content-Type,X-Date,Source,X-Acpt");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setCharacterEncoding("UTF-8");
		if (request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		String path = null;
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			path = request.getServletPath();
		} else {
			path = request.getServletPath() + pathInfo;
		}
		Entry<String, Function<Gateway, Response>> functionEntry = functionRegistry.match(path);
		if (functionEntry != null) {
			Gateway gateway = createGateway(functionEntry.getKey(), request);
			gateway.setBody(body);

			String errMsg = functionRegistry.validateFunction(functionEntry.getKey(), gateway);
			if (errMsg != null) {
				response.sendError(401, errMsg);
			} else {
				Response apply = functionEntry.getValue().apply(gateway);
				if (apply == null) {
					response.sendError(401, "Unable to execute function.");
				} else {
					response.getWriter().append(apply.toJSONString());
				}
			}
		} else {
			super.doFilter(request, response, chain);
		}
	}

	private Gateway createGateway(String contextPath, HttpServletRequest request) {
		Gateway gateway = new Gateway();

		// 1. Headers
		Map<String, String> headers = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			headers.put(name, request.getHeader(name));
		}
		gateway.setHeaders(headers);

		// 2. Query Parameters
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String> queryMap = new HashMap<>();
		Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String[] value = entry.getValue();
			if (value.length > 0) {
				queryMap.put(entry.getKey(), value[0]);
			}
		}
		gateway.setQueryString(queryMap);

		// 3. Path Parameters.
		String path;
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			path = request.getServletPath();
		} else {
			path = request.getServletPath() + pathInfo;
		}
		gateway.setPath(path);
		Map<String, String> pathMap = functionRegistry.buildPathParameters(contextPath, path);
		gateway.setPathParameters(pathMap);

		RequestContext requestContext = new RequestContext();
		requestContext.setPath(contextPath);
		HttpMethod httpMethod = null;
		try {
			String method = request.getMethod();
			httpMethod = HttpMethod.valueOf(method);
		} catch (Exception e) {
			httpMethod = HttpMethod.POST;
		}
		requestContext.setHttpMethod(httpMethod);
		gateway.setRequestContext(requestContext);

		return gateway;
	}

}
