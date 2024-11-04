import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import java.util.HashMap;
import java.util.Map;
@LambdaHandler(
		lambdaName = "hello_world",
		roleName = "hello_world-role",
		isPublishVersion = true,
		aliasName = "learn",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE, // This means no authentication is required to access the Function URL
		invokeMode = InvokeMode.BUFFERED // This can be set to BUFFERED or STREAMING based on your requirements
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent requestEvent, Context context) {
		String path = requestEvent.getRequestContext().getHttp().getPath();
		String method = requestEvent.getRequestContext().getHttp().getMethod();
		// Check if the request is for the /hello endpoint
		if ("GET".equals(method) && "/hello".equals(path)) {
			return buildResponse(200, "Hello from Lambda");
		} else {
			// Handle all other paths with a 400 error
			return buildResponse(400, String.format("Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", path, method));
		}
	}
	private APIGatewayV2HTTPResponse buildResponse(int statusCode, String message) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", statusCode);
		resultMap.put("message", message);
		String responseBody;
		try {
			responseBody = objectMapper.writeValueAsString(resultMap);
		} catch (JsonProcessingException e) {
			// Handle JSON processing error if it occurs
			responseBody = "{\"statusCode\":500,\"message\":\"Internal Server Error\"}";
		}
		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(statusCode)
				.withHeaders(Map.of("Content-Type", "application/json"))
				.withBody(responseBody)
				.build();
	}
}