package com.minejava.kafkastreams.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minejava.kafkastreams.userservice.service.UserService;
import com.minejava.kafkastreams.utilservice.payload.UserPayload;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // endpoint to retrive all users
    @GetMapping("/users")
    public Flux<UserPayload> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public Mono<UserPayload> getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }
}
