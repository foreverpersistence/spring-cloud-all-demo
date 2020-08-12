package com.gupaoedu.service.provider;

import com.gupaoedu.service.annotation.Limited;
import com.gupaoedu.service.annotation.TimeOut;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.*;

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


    @TimeOut(value = 50L, fallback = "fallback")
//    @HystrixCommand(
//            fallbackMethod = "fallback",
//            commandProperties = {
//            // https://github.com/Netflix/Hystrix/wiki/Configuration#execution.isolation.strategy
//            @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
//            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="50")
//    })
    @GetMapping("/echo/{message}")
    public String echo(@PathVariable String message) {
        await();
        return "[ECHO: "+ getPort() + " ] : " + message;
    }

    /**
     * 熔断 方法
     * @param abc
     * @return
     */
    public String fallback(String abc) {
        return "FALLBACK-" + abc;
    }

    private final Random random = new Random();

    private void await() {
        int wait = random.nextInt(100);
        System.out.printf("【当前线程： %s】当前方法执行 消耗：%d  ms", Thread.currentThread().getName(), wait);
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

//    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @TimeOut(value = 50L, fallback = "fallbackHello")
    @GetMapping("/hello")
    public String hello() throws InterruptedException, ExecutionException, TimeoutException {
        //模拟超时， 切换线程
//        Future<String> future = executorService.submit(this::doHello);
//        return future.get(50, TimeUnit.MILLISECONDS);
        return doHello();
    }

    private String doHello() {
        await();
        return "hello";
    }

    //参数类型 一致
    public String fallbackHello() {
        return "fallbackHello-";
    }


    @Limited(10)  //限流
    @GetMapping("/world")
    public String world() {
        await();
        return "world";
    }

}
