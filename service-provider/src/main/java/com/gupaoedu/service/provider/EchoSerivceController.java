package com.gupaoedu.service.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fred
 * @date 2020/8/10 11:35 上午
 * @description todo
 */
@RestController
public class EchoSerivceController {

//    @LocalServerPort  // Jconsole
//    @Value("${server.port}")
//    private int port;

    private final Environment environment;

    public EchoSerivceController(Environment environment) {
        this.environment = environment;
    }



    //获取 随机 端口的 方法
    private String getPort() {
        return environment.getProperty("local.server.port"); //依赖注入时 动态 注入
    }

    @GetMapping("/echo/{message}")
    public String echo(@PathVariable String message) {
        return "[ECHO: "+ getPort() + " ] : " + message;
    }
}
