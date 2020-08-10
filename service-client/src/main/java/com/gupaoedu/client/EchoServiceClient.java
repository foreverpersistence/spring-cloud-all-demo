package com.gupaoedu.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 3、定义 feign 接口
 */
@FeignClient("service-provider") //应用(服务)名称， 否则 ClientException: Load balancer does not have available server for client: echoServiceClient
public interface EchoServiceClient {

    @GetMapping("/echo/{message}")
    public String echo(@PathVariable String message);
}
