/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {

	private static Logger defaultLogger;

	public static Logger getLogger() {
		if (defaultLogger == null) {
			defaultLogger = getLogger("CSFX Function");
		}
		return defaultLogger;
	}

	public static Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
//		ConsoleHandler handler = new ConsoleHandler();
//		handler.setLevel(Level.ALL);
//		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		return logger;
	}

//	public static void info(String msg) {
//		getLogger().info(msg);
//	}
//
//	public static void error(String msg) {
//		getLogger().severe(msg);
//	}
//
//	public static void error(String msg, Throwable tx) {
//		getLogger().log(Level.SEVERE, msg, tx);
//	}
//
//	public static void debug(String msg) {
//		getLogger().fine(msg);
//	}
//
//	public static void warn(String msg) {
//		getLogger().warning(msg);
//	}

}
