package com.gupaoedu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collections;
import java.util.Map;

/**
 * @author fred
 * @date 2020/8/10 11:38 上午
 * @description todo
 */
//@EnableAutoConfiguration //不会扫描 package
//@ComponentScan
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ServiceProviderBootstrap {



    public static void main(String[] args) {
        SpringApplication.run(ServiceProviderBootstrap.class, args);
    }
}
