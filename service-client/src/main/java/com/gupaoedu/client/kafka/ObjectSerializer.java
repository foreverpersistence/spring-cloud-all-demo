package com.gupaoedu.client.kafka;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * @author fred
 * @date 2020/8/16 5:31 下午
 * @description 自定义序列化协议
 */
public class ObjectSerializer implements Serializer<Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectSerializer.class);
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String topic, Serializable data) {

        LOGGER.info("当前 topic: [{}], 序列化对象:[{}]", topic, data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] dataArray = null;

        try(ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            //将对象写入 到 ObjectOutputStream
            oos.writeObject(data);
            //将写入的 数据， 通过 字节数组方式获取
            dataArray = outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException();
        }
        return dataArray;
    }

    @Override
    public void close() {

    }
}
