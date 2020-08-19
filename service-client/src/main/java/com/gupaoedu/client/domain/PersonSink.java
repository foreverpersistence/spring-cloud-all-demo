package com.gupaoedu.client.domain;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface PersonSink {
    String Channel = "person-sink";
    @Input(Channel)
    SubscribableChannel chanel();
}
