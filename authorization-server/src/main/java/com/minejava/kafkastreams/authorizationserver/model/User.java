package com.minejava.kafkastreams.authorizationserver.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private Set<String> roles = new HashSet<>();

    private UUID identifier;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    public User(UUID identifier, String username, String password, String firstName, String lastName, String email, Set<String> roles) {
        this.identifier = identifier;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // DONE Auto-generated method stub
        return AuthorityUtils.commaSeparatedStringToAuthorityList(getRoles()
                                .stream()
                                .map(roles -> "ROLE_" + roles)
                                .collect(Collectors.joining()));
    }
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {

    }

    @Override
    public String getPassword() {
        // DONE Auto-generated method stub
        return password;
    }

    @JsonProperty("username")
    @Override
    public String getUsername() {
        // DONE Auto-generated method stub
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        // DONE Auto-generated method stub
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        // DONE Auto-generated method stub
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        // DONE Auto-generated method stub
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        // DONE Auto-generated method stub
        return true;
    }
    
}
