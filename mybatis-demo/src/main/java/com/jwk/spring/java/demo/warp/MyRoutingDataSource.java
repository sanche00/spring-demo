package com.jwk.spring.java.demo.warp;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class MyRoutingDataSource extends AbstractRoutingDataSourceCustom {
	@Override
	protected Object determineCurrentLookupKey() {
		Object dbKey = RequestContextHolder.getRequestAttributes().getAttribute("db_key",
				RequestAttributes.SCOPE_SESSION);

		return "current:" + dbKey;
	}
}
