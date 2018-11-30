/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.boot;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunctionController {

	@Autowired
	private FunctionRegistry functionRegistry;

	@RequestMapping(path = { "/", "" })
	public String home() {
		Set<String> functionNames = functionRegistry.getFunctionNames();
		StringBuffer buffer = new StringBuffer();
		for (String string : functionNames) {
			buffer.append("<li>");
			buffer.append(string);
			buffer.append("</li>");
		}
		return "<div><h3>Functions Debug...</h3>" + "<ol>" + new String(buffer) + "</ol>" + "</div>";
	}

	@RequestMapping(path = { "/debug" })
	public String debug() {
		return "debug";
	}

}
