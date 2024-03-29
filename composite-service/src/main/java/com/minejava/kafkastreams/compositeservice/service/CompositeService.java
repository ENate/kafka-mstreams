package com.minejava.kafkastreams.compositeservice.service;

import java.io.IOException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minejava.kafkastreams.utilservice.event.DataEvent;
import com.minejava.kafkastreams.utilservice.event.EventType;
import com.minejava.kafkastreams.utilservice.exceptions.BadRequestException;
import com.minejava.kafkastreams.utilservice.exceptions.NotFoundException;
import com.minejava.kafkastreams.utilservice.http.error.HttpErrorInfo;
import com.minejava.kafkastreams.utilservice.payload.UserPayload;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("CompositeService")
public class CompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeService.class);
    private static final String USER_REQ_MAP = "/user/users";
        private static final String USER_ID_MAP = "/user/";
    private static final String PRODUCER_BINDING_NAME = "userProducer-out-0";

    private final String userServiceUrl;
       private final String userIdString;
    private final WebClient webClient;
    private final ObjectMapper mapper;

    private final StreamBridge streamBridge;

    public CompositeService(@Value("${app.user-service.host}")String userServiceHost,
                            @Value("${app.user-service.port}")String userServicePort,
                            WebClient.Builder webClient, ObjectMapper objectMapper,
                            StreamBridge streamBridge) {
        this.userServiceUrl = "http://" + userServiceHost + ":" + userServicePort + USER_REQ_MAP;
        this.userIdString = "http://" + userServiceHost + ":" + userServicePort + USER_ID_MAP;
        this.webClient = webClient.build();
        this.mapper = objectMapper;
        this.streamBridge = streamBridge;
    }

    // Get all users
    public Flux<UserPayload> getAllUsers() {
        LOG.info("Accessing all users via the url: {}", userServiceUrl);
        // maybe return empty if something goes wrong?
        return webClient
                .get()
                .uri(userServiceUrl)
                .retrieve()
                .bodyToFlux(UserPayload.class)
                .onErrorResume(error -> Flux.empty());
    }

    // Get user with given userId
    @CircuitBreaker(name = "users")
    @TimeLimiter(name = "users")
    @Retry(name = "users")
    public Mono<UserPayload> getUserById(String userId) {
        LOG.info("Getting desired used using the Id: {}", userId);
        return webClient
                .get()
                .uri(userIdString+userId)
                .retrieve()
                .bodyToMono(UserPayload.class)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    public Mono<UserPayload> createUser(UserPayload userPayload) {
        DataEvent<String, UserPayload> event = new DataEvent<>(EventType.CREATE, null, userPayload);
        boolean sent = streamBridge.send(PRODUCER_BINDING_NAME, event);
        return sent? Mono.just(userPayload) : Mono.error(new BadRequestException("Error streaming data"));
    }

    // Let us delete
    public Mono<Void> deleteUser(String userId) {
        DataEvent<String, UserPayload> event = new DataEvent<>(EventType.DELETE, userId, null);
        boolean deleted = streamBridge.send(PRODUCER_BINDING_NAME, event);
        return deleted? Mono.empty() : Mono.error(new BadRequestException("Error Deleting user with id {}" + userId));
    }

    private Throwable handleException(Throwable ex) {
        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Unexpected error found: {}", ex.toString());
            return ex;
        }
        WebClientResponseException wcre = (WebClientResponseException) ex;

        switch (HttpStatus.valueOf(wcre.getStatusCode().value())) {
            case NOT_FOUND -> {
                return new NotFoundException(getErrorMessage(wcre));
            }
            case BAD_REQUEST -> {
                return new NotFoundException("Not bad request" + getErrorMessage(wcre));
            }
            default -> {
                LOG.warn("Unexpected Http Status error: {}", wcre.getStatusCode());
                LOG.warn("Unexpected error body: {}", wcre.getResponseBodyAsString());
                return ex;
            }
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }catch (IOException ioException) {
            return ioException.getMessage();
        }
    }

}
