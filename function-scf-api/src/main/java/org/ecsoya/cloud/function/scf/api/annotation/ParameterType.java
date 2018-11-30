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
public enum ParameterType {
	t_string("string"), t_int("int"), t_float("float"), t_long("long"), t_double("double"), t_boolean("boolean"),
	t_json("json");

	private String literal;

	/**
	 * 
	 */
	private ParameterType(String literal) {
		this.literal = literal;
	}

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}
}
