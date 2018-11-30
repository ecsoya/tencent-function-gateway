# tencent-function-gateway
腾讯云无服务器函数（SCF）API + API 网关 + SpringBoot调试

### 工程介绍

#### 1. function-scf-api
  
  A. 封装了腾讯云无服务器函数和API网关的API。
  
  B. 创建了函数的接口和API网关函数的接口。
```java
public interface IFunction<T, R> {
	R handleRequest(T input, Context context);
}

public interface IGatewayFunction extends IFunction<Gateway, Response> {

}

```
   C. 添加了基于Java8 Lambda的函数表达式实现基类
```java
  public class LambdaGatewayFunction implements IGatewayFunction {
    ...
  }
```
  D. 和API网关配套使用的Java注解
  * GatewayFunction
  * Parameter

#### 2. function-demo

基于`LambdaGatewayFunction`的一个demo

```java
 
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

```

#### 3. function-boot
  基于SpringBoot调试API网关+无服务器函数。
  
  * 跨域访问（CORS）
  * API网关模拟


#### 4. function-maven-plugin

  将腾讯云无服务器函数自动打包上传的maven插件。
