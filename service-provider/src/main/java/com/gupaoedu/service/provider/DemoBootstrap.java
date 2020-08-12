package com.gupaoedu.service.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.Map;

/**
 * @author fred
 * @date 2020/8/11 7:58 下午
 * @description todo
 */
@EnableAutoConfiguration
public class DemoBootstrap {

    //todo  场景？

    @Autowired
    private Map<String,String> allStringBean = Collections.emptyMap();


    //通过 Qualifier  value() 属性 依赖查找
    @Autowired
    @Qualifier("a")
    private String aBean;
    @Autowired
    @Qualifier("b")
    private String bBean;
    @Autowired
    @Qualifier("c")
    private String cBean;

    //不通过 Qualifier  value() 属性 依赖查找

    @Autowired
//    @Qualifier  // 查找分组
    @Group
    private  Map<String, String> groupStringBeans = Collections.emptyMap();

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            System.out.println(allStringBean);

            System.out.println("a=" + aBean);
            System.out.println("b=" + bBean);
            System.out.println("c=" + cBean);

            System.out.println(groupStringBeans);
        };
    }

    @Bean
    public String a() {
        return "String-a";
    }

    //b and c 分组 @Qualifier

    @Bean
    @Qualifier
    public String b() {
        return "String-b";
    }
    @Bean
//    @Qualifier
    @Group
    public String c() {
        return "String-c";
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(DemoBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}



//自定义注解
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier
@interface Group {

}

