package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.SqsEvents;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "sqs_handler",
		roleName = "sqs_handler-role",
		isPublishVersion = false,
		aliasName = "learn",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(
		targetQueue="async_queue",
		batchSize = 1
)
@DependsOn(
		name = "async_queue",
		resourceType = ResourceType.SQS_QUEUE
)

public class SqsHandler implements RequestHandler<Object,Map<String,Object>> {

	@Override
	public Map<String, Object> handleRequest(Object input, Context context) {
		LambdaLogger lambdaLogger= context.getLogger();
		lambdaLogger.log(input.toString());
		System.out.println("Hello from lambda");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", 200);
		result.put("body", "Hello from Lambda");
		return result;
	}
}
