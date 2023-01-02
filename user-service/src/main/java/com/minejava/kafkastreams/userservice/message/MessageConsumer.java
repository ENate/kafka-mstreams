package com.minejava.kafkastreams.userservice.message;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.minejava.kafkastreams.userservice.mapper.UserMapper;
import com.minejava.kafkastreams.userservice.model.User;
import com.minejava.kafkastreams.userservice.service.UserService;
import com.minejava.kafkastreams.utilservice.event.DataEvent;
import com.minejava.kafkastreams.utilservice.exceptions.BadRequestException;
import com.minejava.kafkastreams.utilservice.payload.UserPayload;

import reactor.core.publisher.Mono;


@Component
public class MessageConsumer {
    private final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

    private final UserMapper userMapper;
    private final UserService userService;

    public MessageConsumer(UserMapper userMapper, UserService uService) {
        this.userMapper = userMapper;
        this.userService = uService;
    }

    @Bean
    public Consumer<DataEvent<String, UserPayload>> userConsumer() {
        return event -> {
            LOG.info("Consuming message created at {}", event.getEventCreatedAt());

            // switch cases for CRUD operations
            switch(event.getEventType()) {
                case CREATE:
                // Create a userPayload from util-service
                UserPayload userPayload = event.getData();
                LOG.info("Creating a user service for {}", userPayload);
                // Map the created user to userData service
                User user = userMapper.userPayloadToUserService(userPayload);
                // Save user to data base via user service
                userService.saveUser(user)
                                .onErrorMap(
                                    DuplicateKeyException.class, 
                                    ex -> new BadRequestException("Duplicate key, username " + user.getUsername() +
                                    " or email address " + user.getEmail() +" had already been used ")
                                )
                                .subscribe(u -> LOG.info("Details for Created user:  {}", u));
                    break;            
                //
                case READ:
                    // Create a user key or Id via userpayload
                    userPayload = event.getData();
                    // Do you have to map to user service?
                    user = userMapper.userPayloadToUserService(userPayload);
                    // Retrive the use given the userId after check
                    String myUserId = event.getKey() !=null? event.getKey() : user.getId();
                    // Throw an error not found if request cannot be performed
                    LOG.info("Retriving infor user with id {}", myUserId);
                    // subscribe to the user via the subscribe method
                    userService.getUserById(myUserId)
                                .subscribe(y -> LOG.info("Finding user by given Id {} found ", myUserId));

                    break;
                case UPDATE:
                break;
                case DELETE:
                    // create user payload to delete
                    userPayload = event.getData();
                    // convert userpayload to user service
                    user = userMapper.userPayloadToUserService(userPayload);
                    // Check whether userId is null and user event key if the case?
                    String userId = event.getKey() != null? event.getKey() : user.getId();
                    LOG.info("Deleting user with the following ID: {}", userId);
                    userService.deleteUserById(userId)
                                .subscribe(x -> LOG.info("User with given Id  {} deleted successfully ", userId));
                    break;
                default:
                    Mono.just(event.getData());
            }
        };
    }
}
