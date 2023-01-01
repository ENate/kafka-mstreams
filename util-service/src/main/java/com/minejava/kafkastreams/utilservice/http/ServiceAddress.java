package com.minejava.kafkastreams.utilservice.http;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceAddress {
    private final String port;
    private String hostAddress = null;

    public ServiceAddress(@Value("${server.port}") String port) {
        this.port = port;
    }

    public String getHostAddress() {
        if(hostAddress == null) {
            hostAddress = getHostName() + "/" + getIpAddress() + ":" + port;
        }

        return hostAddress;
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown-Host";
        }
    }

    private String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown-IP";
        }
    }
}
