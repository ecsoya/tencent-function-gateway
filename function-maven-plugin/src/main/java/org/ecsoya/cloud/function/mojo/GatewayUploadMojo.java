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
package org.ecsoya.cloud.function.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Ecsoya
 *
 */
@Mojo(name = "upload-gateway", defaultPhase = LifecyclePhase.INSTALL)
public class GatewayUploadMojo extends AbstractMojo {

	@Parameter(property = "gateway")
	private GatewayConfiguration gateway;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Gateway Upload..." + gateway);
		getLog().info("Gateway Upload... NOT IMPLEMENTED YET ... waiting for SDK.");
	}

}
