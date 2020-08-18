package com.gupaoedu.client;

import com.gupaoedu.client.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fred
 * @date 2020/8/10 1:40 下午
 * @description todo
 */
//@EnableAutoConfiguration//不会扫描 package
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  //扫描同包 package
@RestController
public class EchoServiceClientBootstrap {

    @Autowired
    private EchoServiceClient echoServiceClient;
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @GetMapping("/person")
    public Person person(String name) {
        Person person = new Person();
        person.setId(System.currentTimeMillis());
        person.setName(name);

        kafkaTemplate.send("gupao", person);
        return person;
    }

    @KafkaListener(topics = "gupao", groupId = "gupao-group")
    public void listner(Person person) {
        System.out.println(person);
    }



    @GetMapping(value = "/call/echo/{message}")
    public String echo(@PathVariable String message) {

        return echoServiceClient.echo(message);
    }

    public static void main(String[] args) {
        SpringApplication.run(EchoServiceClientBootstrap.class, args);
    }
}
