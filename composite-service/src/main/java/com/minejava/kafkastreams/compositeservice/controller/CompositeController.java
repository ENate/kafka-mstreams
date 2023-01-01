package com.minejava.kafkastreams.compositeservice.controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minejava.kafkastreams.compositeservice.service.CompositeService;
import com.minejava.kafkastreams.utilservice.payload.UserPayload;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/composite-service")
public class CompositeController {
    private final CompositeService compositeService;

    public CompositeController(CompositeService cService) {
        this.compositeService = cService;
    }


    @PostMapping("/user")
    public Mono<UserPayload> createUser(@RequestBody UserPayload user) {
        return compositeService.createUser(user);
    }

    @GetMapping("/user")
    public Flux<UserPayload> getAllUsers() {
        return compositeService.getAllUsers();
    }

    @GetMapping("/user/{userId}")
    public Mono<UserPayload> getUser(@PathVariable String userId) {
        return compositeService.getUserById(userId);
    }

    @DeleteMapping("/user/{userId}")
    public Mono<Void> deleteUser(@PathVariable String userId) {
        return compositeService.deleteUser(userId);
    }
}
