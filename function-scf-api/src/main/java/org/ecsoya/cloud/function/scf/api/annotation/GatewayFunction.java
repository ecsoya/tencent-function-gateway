/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ecsoya.cloud.function.scf.api.model.HttpMethod;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface GatewayFunction {

	String path();

	/**
	 * Header parameters
	 */
	Parameter[] headers() default {};

	/**
	 * Path Parameters
	 */
	Parameter[] params() default {};

	/**
	 * Query parameters
	 */
	Parameter[] queries() default {};

	/**
	 * Body parameters
	 */
	Parameter[] body() default {};

	HttpMethod httpMethod() default HttpMethod.POST;

	boolean authorize() default true;

	String comment() default "";
}
