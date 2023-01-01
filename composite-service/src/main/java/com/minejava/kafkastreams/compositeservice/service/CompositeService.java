package com.minejava.kafkastreams.compositeservice.service;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeService.class);
    private static final String USER_REQ_MAP = "/user";
    private static final String PRODUCER_BINDING_NAME = "userProducer-out-0";

    private final String userServiceUrl;
    private final WebClient webClient;
    private final ObjectMapper mapper;

    private final StreamBridge streamBridge;

    public CompositeService(@Value("${app.user-service.host}")String userServiceHost,
                            @Value("${app.user-service.host}")String userServicePort,
                            WebClient.Builder webClient, ObjectMapper objectMapper,
                            StreamBridge streamBridge) {
        this.userServiceUrl = "http://" + userServiceHost + ":" + userServicePort + USER_REQ_MAP;
        this.webClient = webClient.build();
        this.mapper = objectMapper;
        this.streamBridge = streamBridge;
    }
}
