package com.minejava.kafkastreams.compositeservice.servicescom;

import com.minejava.kafkastreams.compositeservice.service.CompositeService;
import com.minejava.kafkastreams.utilservice.dto.SubjectUtilAg;
import com.minejava.kafkastreams.utilservice.dto.UserserviceAg;
import com.minejava.kafkastreams.utilservice.exceptions.NotFoundException;
import com.minejava.kafkastreams.utilservice.http.ServerAddress;
import com.minejava.kafkastreams.utilservice.payload.UserPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.integration.handler.advice.RequestHandlerCircuitBreakerAdvice.CircuitBreakerOpenException;
import org.springframework.retry.RetryException;
import reactor.core.publisher.Mono;

import java.util.List;


@Log4j2
public class UserServicesCompI {
	private final ServerAddress serviceUtil;
	private final CompositeService compositeService;

	public UserServicesCompI(ServerAddress serviceUtil, CompositeService compositeService) {
		this.serviceUtil = serviceUtil;
		this.compositeService = compositeService;
	}


	public Mono<UserserviceAg> getUserById(String userId) {
		return Mono.zip(
			  values -> createUserAg(
				 (UserPayload) values[0],
				 (List<SubjectUtilAg>) values[1],
				 serviceUtil.getHostAddress()),
			  compositeService
				 .getUserById(userId)
				 // Legacy code uses the following
				 // .onErrorMap(RetryExceptionWrapper.class, Throwable::getCause)
				 // <<<<< Decided to change to RetryException class >>>>>>>>
				 .onErrorMap(RetryException.class, Throwable::getCause)
				 .onErrorReturn(CircuitBreakerOpenException.class, getUserFallbackValue(userId))
		   .doOnError(ex -> log.warn("getUser failed: {}", ex.toString()))
		   .log());
	}

	private <R> R createUserAg(UserPayload value, List<SubjectUtilAg> value1, String hostAddress) {
		return null;
	}

	private UserPayload getUserFallbackValue(String userId) {
		log.warn("No user found with id {}", userId);
		if (userId.equals("someUser")) {
			String errMsg = "User Id: " + userId + " not found in fallback cache!";
			log.warn(errMsg);
			throw new NotFoundException(errMsg);
		}

		return new UserPayload(userId, "Fallback user" + userId, null, null, serviceUtil.getHostAddress());
	}
}
