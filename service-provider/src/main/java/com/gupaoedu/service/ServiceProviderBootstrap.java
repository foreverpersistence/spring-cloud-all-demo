package com.gupaoedu.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author fred
 * @date 2020/8/10 11:38 上午
 * @description todo
 */
//@EnableAutoConfiguration //不会扫描 package
//@ComponentScan
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceProviderBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProviderBootstrap.class, args);
    }
}
