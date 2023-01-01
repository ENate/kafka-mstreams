package com.minejava.kafkastreams.userservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.minejava.kafkastreams.userservice.model.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<User> findByUsername(String username);
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByUsernameOrEmail(String username, String email);
}
