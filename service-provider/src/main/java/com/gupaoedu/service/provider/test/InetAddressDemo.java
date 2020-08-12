package com.gupaoedu.service.provider.test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author fred
 * @date 2020/8/11 4:33 下午
 * @description todo
 */
public class InetAddressDemo {

    public static void main(String[] args) throws UnknownHostException {

        Stream.of(InetAddress.getAllByName("www.baidu.com"))
                .forEach(System.out::println);
//
//        www.baidu.com/61.135.169.121
//        www.baidu.com/61.135.169.125
        //


        Random random = new Random();

        int i = random.nextInt(2);
        System.out.println(i);

        // rondbin

    }
}
