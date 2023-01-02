package com.minejava.kafkastreams.userservice.service;

import org.springframework.stereotype.Service;

import com.minejava.kafkastreams.userservice.dao.UserDataService;
import com.minejava.kafkastreams.userservice.mapper.UserMapper;
import com.minejava.kafkastreams.userservice.model.User;
import com.minejava.kafkastreams.utilservice.exceptions.NotFoundException;
import com.minejava.kafkastreams.utilservice.http.ServerAddress;
import com.minejava.kafkastreams.utilservice.payload.UserPayload;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("UserService")
public class UserService {

    // Define fields
    private final UserDataService userDataService;
    private final UserMapper userMapper;
    private final ServerAddress serverAddress;
    

    public UserService(UserMapper mapper, UserDataService uDataService, ServerAddress address) {
        this.serverAddress = address;
        this.userMapper = mapper;
        this.userDataService = uDataService;
    }

     // Flux to get all users payload
     public Flux<UserPayload> getAllUsers() {
        return userDataService.getAllUsers()
                .map(userMapper::userServiceToUserPayload)
                .map(u -> {u.setServiceAddress(serverAddress.getHostAddress());
                return u;});
    }

    // Mono to retieve user by given Id:
    public Mono<UserPayload> getUserById(String userId) {
        return userDataService.getUserById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Not found User with Id: "+ userId)))
                .map(userMapper::userServiceToUserPayload)
                .map(u -> {u.setServiceAddress(serverAddress.getHostAddress());
                    return u;});
    }
    /*
     * Mono call to get user by email string
     * @param email - represents email of a given user
     */
    public Mono<UserPayload> getUserByEmail(String email) {
        /*
         * Mono call to get user by email string
         * @param email - represents email of a given user
         */
        return userDataService.getUserById(email)
                .switchIfEmpty(Mono.error(new NotFoundException("Not found User with Id: "+ email)))
                .map(userMapper::userServiceToUserPayload)
                .map(u -> {u.setServiceAddress(serverAddress.getHostAddress());
                    return u;});
    }

    /*
     * Find user by username defined in the UserRepository
     * @param username - represents user name string
     */

    public Mono<UserPayload> getUserByUsername(String username) {
        return userDataService.getUserById(username)
                .switchIfEmpty(Mono.error(new NotFoundException("Not found User with Id: "+ username)))
                .map(userMapper::userServiceToUserPayload)
                .map(u -> {u.setServiceAddress(serverAddress.getHostAddress());
                    return u;});
    }
    public Mono<Boolean> isUserExists(String id) {
        return userDataService.isUserExists(id);
    }

    public Mono<Boolean> isUserExists(String username, String email) {
        return userDataService.isUserExists(username, email);
    }

    public Mono<User> saveUser(User user) {
        return userDataService.saveUser(user);
    }

    public Mono<Void> deleteUserById(String id) {
        return userDataService.deleteUserById(id);
    }

    public Mono<Void> deleteAllUsers() {
        return userDataService.deleteAllUsers();
    }

    public Mono<Long> countAllUsers() {
        return userDataService.countAllUsers();
    }
}
