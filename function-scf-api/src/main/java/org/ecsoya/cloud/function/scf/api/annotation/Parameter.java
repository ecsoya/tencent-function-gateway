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
package org.ecsoya.cloud.function.scf.api.annotation;

/**
 * @author Ecsoya
 *
 */
public @interface Parameter {

	String name();

	ParameterType type = ParameterType.t_string;

	String defaultValue() default "";

	boolean required() default true;

	String comment() default "";
}
