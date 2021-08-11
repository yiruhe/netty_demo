package com.mqtt.mqtt2;

public interface MqttConsumerListener {

    /**
     * 接收到的消息(确认过的)
     * @param topic
     * @param msg
     */
    void receiveMessage(String topic, byte[] msg);

}
