/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.region.Region;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.scf.v20180416.ScfClient;
import com.tencentcloudapi.scf.v20180416.models.Code;
import com.tencentcloudapi.scf.v20180416.models.CreateFunctionRequest;
import com.tencentcloudapi.scf.v20180416.models.Environment;
import com.tencentcloudapi.scf.v20180416.models.Function;
import com.tencentcloudapi.scf.v20180416.models.ListFunctionsRequest;
import com.tencentcloudapi.scf.v20180416.models.ListFunctionsResponse;
import com.tencentcloudapi.scf.v20180416.models.UpdateFunctionCodeRequest;
import com.tencentcloudapi.scf.v20180416.models.UpdateFunctionConfigurationRequest;
import com.tencentcloudapi.scf.v20180416.models.Variable;

@Mojo(name = "upload-function", defaultPhase = LifecyclePhase.INSTALL)
public class FunctionUploadMojo extends AbstractMojo {

	private static final String DEFAULT_REGION = "ap-guangzhou";
	private static final String RUNTIME = "Java8";

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject mavenProject;

	@Parameter(property = "function")
	private FunctionConfiguration function;

	public void execute() throws MojoExecutionException {
		if (function == null || !function.isEnabled()) {
			getLog().info(" Function Upload - Skipped. (<enabled>false</enabled>) ");
			return;
		}
		File jar = mavenProject.getArtifact().getFile();
		getLog().info(" Function Upload - JAR: " + jar.getAbsolutePath());
		long size = jar.length() / 1024 / 1024;
		if (size > 20) {
			throw new MojoExecutionException(
					"The package " + jar.getName() + " is large than 20M, please use offical website to upload it.");
		}
		getLog().info(" Function Upload - JAR: " + size + "M");

		String secretId = function.getSecretId();
		String secretKey = function.getSecretKey();
		if (secretId == null || secretKey == null) {
			throw new MojoExecutionException("'secretId' or 'secretKey' is not configured.");
		}

		String functionName = function.getName();
		if (functionName == null) {
			throw new MojoExecutionException("'function.name' is not configured.");
		}

		// 1. Upload Jar to Bucket.
		boolean uploaded = uploadJarToBucket();

		String region = function.getRegion();
		if (region == null) {
			region = DEFAULT_REGION;
			getLog().info(" Function Upload - Function: use default region GuangZhou");
		}
		getLog().info(" Function Upload - Function: Signature(ClientProfile.SIGN_SHA256 = HmacSHA256)");
		// 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey
		Credential credential = new Credential(secretId, secretKey);
		ClientProfile profile = new ClientProfile(ClientProfile.SIGN_SHA256);
		ScfClient scfClient = new ScfClient(credential, region, profile);

		String handler = function.getHandler();

		Function functionModel = null;
		ListFunctionsRequest listRequest = new ListFunctionsRequest();

		try {
			ListFunctionsResponse listResponse = scfClient.ListFunctions(listRequest);
			for (Function func : listResponse.getFunctions()) {
				if (functionName.equals(func.getFunctionName())) {
					functionModel = func;
					break;
				}
			}
		} catch (TencentCloudSDKException e) {
			throw new MojoExecutionException("GetFunction failed", e);
		}

		if (functionModel != null) {
			getLog().info(" Function Upload - Function: Updating existing function ");
			if (uploaded) {
				getLog().info(" Function Upload - Function: Updating code.");
				UpdateFunctionCodeRequest updateCodeRequest = new UpdateFunctionCodeRequest();
				updateCodeRequest.setFunctionName(functionName);
				updateCodeRequest.setHandler(handler);
				updateCodeRequest.setCosBucketName(function.getBucket().getName());
				updateCodeRequest.setCosBucketRegion(function.getBucket().getRegion());
				updateCodeRequest.setCosObjectName(functionName);

				try {
					scfClient.UpdateFunctionCode(updateCodeRequest);
				} catch (TencentCloudSDKException e) {
					throw new MojoExecutionException(" Function Upload - Function: UpdateFunctionCode", e);
				}
			}
			getLog().info(" Function Upload - Function: Updating configuration.");
			UpdateFunctionConfigurationRequest updateConfigRequest = new UpdateFunctionConfigurationRequest();
			updateConfigRequest.setFunctionName(functionName);
			updateConfigRequest.setRuntime(RUNTIME);
			updateConfigRequest.setDescription(function.getDescription());
			updateConfigRequest.setTimeout(function.getTimeout());
			updateConfigRequest.setMemorySize(function.getMemory());
			Map<String, String> map = function.getEnvironments();
			if (!map.isEmpty()) {
				Environment environment = new Environment();
				Set<Entry<String, String>> entrySet = map.entrySet();
				List<Variable> variables = new ArrayList<Variable>();
				for (Entry<String, String> entry : entrySet) {
					Variable var = new Variable();
					var.setKey(entry.getKey());
					var.setValue(entry.getValue());
					variables.add(var);
				}
				environment.setVariables(variables.toArray(new Variable[variables.size()]));
				updateConfigRequest.setEnvironment(environment);
			}
			try {
				scfClient.UpdateFunctionConfiguration(updateConfigRequest);
			} catch (TencentCloudSDKException e) {
				throw new MojoExecutionException(" Function Upload - Function: UpdateFunctionConfiguration", e);
			}
		} else {
			getLog().info(" Function Upload - Function: Creating new function ");
			CreateFunctionRequest createRequest = new CreateFunctionRequest();
			if (uploaded) {
				Code code = new Code();
				code.setCosBucketName(function.getBucket().getName());
				code.setCosBucketRegion(function.getBucket().getRegion());
				code.setCosObjectName(functionName);
				createRequest.setCode(code);
			}
			createRequest.setDescription(function.getDescription());
			createRequest.setRuntime(RUNTIME);
			createRequest.setFunctionName(functionName);
			createRequest.setHandler(handler);
			try {
				scfClient.CreateFunction(createRequest);
			} catch (TencentCloudSDKException e) {
				throw new MojoExecutionException(" Function Upload - Function: CreateFunction", e);
			}
		}

	}

	private boolean uploadJarToBucket() {
		if (function == null) {
			return false;
		}
		BucketConfiguration bucket = function.getBucket();
		if (bucket != null && bucket.getName() != null) {
			getLog().info(" Function Upload - Bucket: Upload jar to bucket: " + bucket.getName() + " at region("
					+ bucket.getRegion() + ")");
		} else {
			getLog().info(" Function Upload - Bucket: not found, Skipped");
			return false;
		}

		String bucketSimpleName = bucket.getName();
		String appid = bucket.getAppid();
		String bucketName = bucketSimpleName + "-" + appid;
		String bucketRegion = bucket.getRegion();
		if (bucketRegion == null) {
			bucketRegion = DEFAULT_REGION;
			getLog().info(" Function Upload - Bucket: use default region GuangZhou");
		}

		COSCredentials cred = new BasicCOSCredentials(function.getSecretId(), function.getSecretKey());
		ClientConfig clientConfig = new ClientConfig(new Region(bucketRegion));
		COSClient cosClient = new COSClient(cred, clientConfig);
		if (!cosClient.doesBucketExist(bucketName)) {
			CreateBucketRequest request = new CreateBucketRequest(bucketSimpleName);
			cosClient.createBucket(request);
			cosClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
			getLog().info(" Function Upload - Bucket: create bucket: " + bucketName);
		} else {
			cosClient.putObject(bucketName, function.getName(), mavenProject.getArtifact().getFile());
			getLog().info(" Function Upload - Bucket: upload jar to bucket " + bucketName + " with name "
					+ function.getName());
		}
		cosClient.shutdown();
		return true;
	}
}
