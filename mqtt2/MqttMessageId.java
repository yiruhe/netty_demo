package com.mqtt.mqtt2;

import java.util.concurrent.atomic.AtomicInteger;

public class MqttMessageId {

    private static AtomicInteger index = new AtomicInteger(1);
    /**
     * 获取messageId
     * @return id
     */
    public  static int  messageId(){

        int andIncrement = index.getAndIncrement();

        if (index.get() == Integer.MAX_VALUE)index.set(1);

        return andIncrement;


    }
}
