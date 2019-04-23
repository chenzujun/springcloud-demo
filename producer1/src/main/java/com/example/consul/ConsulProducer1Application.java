package com.example.consul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author chenjun
 * @date 2019/4/23
 * @since V1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ConsulProducer1Application {

    public static void main(String[] args) {
        SpringApplication.run(ConsulProducer1Application.class, args);
    }
}
