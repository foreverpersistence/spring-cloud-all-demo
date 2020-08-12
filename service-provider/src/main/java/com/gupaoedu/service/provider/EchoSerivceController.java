package com.gupaoedu.service.provider;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

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

    @HystrixCommand(commandProperties = {
            // https://github.com/Netflix/Hystrix/wiki/Configuration#execution.isolation.strategy
            @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="50")
    })
    @GetMapping("/echo/{message}")
    public String echo(@PathVariable String message) {
        await();
        return "[ECHO: "+ getPort() + " ] : " + message;
    }


    private final Random random = new Random();

    private void await() {
        int wait = random.nextInt(100);
        System.out.println("当前方法执行 消耗：" + wait + " ms");
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }


}
