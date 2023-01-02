package com.minejava.kafkastreams.compositeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.minejava.kafkastreams")
public class CompositeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompositeServiceApplication.class, args);
    }
}
