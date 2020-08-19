package com.gupaoedu.client;

import com.gupaoedu.client.domain.Person;
import com.gupaoedu.client.domain.PersonSink;
import com.gupaoedu.client.domain.PersonSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fred
 * @date 2020/8/10 1:40 下午
 * @description http://blog.didispace.com/spring-cloud-starter-finchley-7-1/
 */
//@EnableAutoConfiguration//不会扫描 package
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  //扫描同包 package
@RestController
@EnableBinding({Source.class, PersonSource.class,PersonSink.class})
public class EchoServiceClientBootstrap {

    @Autowired
    private EchoServiceClient echoServiceClient;
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    private final Source source;

    private final PersonSource personSource;

    private final PersonSink personSink;

    public EchoServiceClientBootstrap(Source source, PersonSource personSource, PersonSink personSink) {
        this.source = source;
        this.personSource = personSource;
        this.personSink = personSink;
    }

    @GetMapping("/person")
    public Person person(String name) {
        Person person = createPerson(name);

        kafkaTemplate.send("gupao", person);
        return person;
    }

    @KafkaListener(topics = "gupao", groupId = "gupao-group")
    public void listner(Person person) {
        System.out.println(person);
    }


    @GetMapping("/stream/person")
    public Person streamPerson(String name) {
        Person person = createPerson(name);

        MessageChannel messageChannel = source.output();
        messageChannel.send(MessageBuilder.withPayload(person).build());

        return person;
    }

    @GetMapping("/stream/person/source")
    public Person streamPersonSource(String name) {
        Person person = createPerson(name);

        MessageChannel messageChannel = personSource.output();
        MessageBuilder<Person> personMessageBuilder = MessageBuilder.withPayload(person).setHeader("Content-Type", "java/pojo");
        messageChannel.send(personMessageBuilder.build());

        return person;
    }


    /**
     * 通过 注解方式 监听 数据，
     *   不处理 header
     * @param person
     */
    @StreamListener("person-source") //channel 名称， 这里配置 INput 和output 都能 正常 工作，  配置 input 时，会走 自定义 序列化协议
    public void listnerSource(Person person) {
        System.out.println(person);
    }


    private Person createPerson(String name) {
        Person person = new Person();
        person.setId(System.currentTimeMillis());
        person.setName(name);
        return person;
    }


    @GetMapping(value = "/call/echo/{message}")
    public String echo(@PathVariable String message) {

        return echoServiceClient.echo(message);
    }

    public static void main(String[] args) {
        SpringApplication.run(EchoServiceClientBootstrap.class, args);
    }



    @Bean
    public ApplicationRunner runner () {
        return args -> {
            personSink.chanel().subscribe(new MessageHandler() {//通过 spring message API 实现
                @Override
                public void handleMessage(Message<?> message) throws MessagingException {

                    MessageHeaders headers = message.getHeaders();
                    String contentType = headers.get("Content-Type", String.class);
                    Object object = message.getPayload();
                    System.out.printf("收到消息主体：%s, 消息头: %s", object, contentType);

                }
            });
        };
    }
}
