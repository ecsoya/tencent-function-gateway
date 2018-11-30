/*******************************************************************************
 * Copyright (c) 2018 Ecsoya and others.
 *
 * Contributors:
 *      Ecsoya (jin.liu@soyatec.com)
 *    
 *******************************************************************************/
package function;

import java.util.function.Function;

import org.ecsoya.cloud.function.scf.api.LambdaGatewayFunction;
import org.ecsoya.cloud.function.scf.api.annotation.GatewayFunction;
import org.ecsoya.cloud.function.scf.api.model.Failure;
import org.ecsoya.cloud.function.scf.api.model.Gateway;
import org.ecsoya.cloud.function.scf.api.model.HttpMethod;
import org.ecsoya.cloud.function.scf.api.model.Response;

public class DemoGatewayFunction extends LambdaGatewayFunction {
	
	@GatewayFunction(path="/api/upper", httpMethod=HttpMethod.GET)
	public Function<Gateway, Response> toUppercase() {
		return gateway -> {
			
			String body = gateway.getBody();
			if (body == null) {
				return new Failure(403, "");
			}
			return new Response(200, body.toUpperCase());
		};
	}
}
