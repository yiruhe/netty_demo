package com.mqtt.mqtt2;

import io.netty.handler.codec.mqtt.MqttQoS;

public class SubscribeMessage {
    private String topic;
    private MqttQoS qos;

    public SubscribeMessage(){}
    public SubscribeMessage(String topic, MqttQoS qos) {
        this.topic = topic;
        this.qos = qos;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttQoS getQos() {
        return qos;
    }

    public void setQos(MqttQoS qos) {
        this.qos = qos;
    }
}
