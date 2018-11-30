/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package org.ecsoya.cloud.function.scf.api.signature;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Signature with API Gateways.
 * 
 * @author Ecsoya
 */
public class Signature {
	private static final String CONTENT_CHARSET = "UTF-8";
	private static final String HMAC_ALGORITHM = "HmacSHA1";

	private static String sign(String secret, String timeStr, String source)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		// get signStr
		String signStr = "date: " + timeStr + "\n" + "source: " + source;
		// get sig
		String sig = null;
		Mac mac1 = Mac.getInstance(HMAC_ALGORITHM);
		byte[] hash;
		SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), mac1.getAlgorithm());
		mac1.init(secretKey);
		hash = mac1.doFinal(signStr.getBytes(CONTENT_CHARSET));
		sig = new String(Base64.encode(hash));
		System.out.println("signValue--->" + sig);
		return sig;
	}

	public static Map<String, String> buildSignatureHeaders(String secretId, String secretKey, String source)
			throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		if (secretId == null || secretKey == null) {
			return Collections.emptyMap();
		}
		Map<String, String> headers = new HashMap<>();
		// get current GMT time
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timeStr = sdf.format(cd.getTime());

		headers.put("Date", timeStr);
		headers.put("Source", source);
		headers.put("Authorization", getSignature(secretId, secretKey, timeStr, source));
		return headers;
	}

	private static String getSignature(String secretId, String secretKey, String timeStr, String source)
			throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		String sig = sign(secretKey, timeStr, source);

		return "hmac id=\"" + secretId + "\", algorithm=\"hmac-sha1\", headers=\"date source token\", signature=\""
				+ sig + "\"";
	}

}