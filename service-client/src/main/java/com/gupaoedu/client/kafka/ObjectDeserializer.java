package com.gupaoedu.client.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * @author fred
 * @date 2020/8/18 3:02 下午
 * @description 自定义反序列化
 */
public class ObjectDeserializer implements Deserializer<Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectDeserializer.class);
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Serializable deserialize(String topic, byte[] data) {

        Serializable object = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        try(ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            object = (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        LOGGER.info("当前 topic：[{}], 反序列化对象：[{}]", topic, object);

        return object;
    }

    @Override
    public void close() {

    }
}
