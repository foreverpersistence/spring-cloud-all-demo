package com.gupaoedu.client.domain;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PersonSource {

    String CHANNEL = "person-source";
    @Output(CHANNEL)
    MessageChannel output();
}
