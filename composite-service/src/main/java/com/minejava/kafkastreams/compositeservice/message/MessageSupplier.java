package com.minejava.kafkastreams.compositeservice.message;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.minejava.kafkastreams.utilservice.event.DataEvent;
import com.minejava.kafkastreams.utilservice.event.EventType;
import com.minejava.kafkastreams.utilservice.payload.UserPayload;

import java.util.function.Supplier;

@Component
public class MessageSupplier {
    private final Logger LOG = LoggerFactory.getLogger(MessageSupplier.class);

    private Boolean produce;

    @Bean
    public Supplier<DataEvent<String, UserPayload>> userProducer(){
        return () -> {
            // we assume produce is set to true
            if (produce) {
                return getUserPayLoad();
            }
            return null;
        };
    }

    private DataEvent<String, UserPayload> getUserPayLoad() {
        UserPayload userPayload0 = new UserPayload("Port man", "arrive@qportmanteau.com", "Na mode");
        LOG.info("Logging from composite controller");
        return new DataEvent<>(EventType.CREATE, null, userPayload0);

    }
}
