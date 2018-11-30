/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api;

import com.qcloud.scf.runtime.Context;

public interface IFunction<T, R> {

	R handleRequest(T input, Context context);
}
