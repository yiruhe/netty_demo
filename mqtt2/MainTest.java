package com.mqtt.mqtt2;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.UUID;

public class MainTest {


    public static void main(String[] args) throws InterruptedException {


        MqttClient mqttClient = new MqttClient();

        MqttConnectOptions options = mqttClient.mqttOptions();

       options.setClientIdentifier("client_"+ UUID.randomUUID());

       // options.setWillQos(MqttQoS.AT_MOST_ONCE.value());
      //  options.setWillTopic("/test");

        mqttClient.setConsumerListener((topic, msg) -> {
            System.out.println("topic---->"+topic);
            System.out.println("msg--->"+new String(msg));
        });

        mqttClient.connectMqtt("127.0.0.1",1883);


        mqttClient.subscribe("/test", MqttQoS.EXACTLY_ONCE);

      //  mqttClient.sendPublishMessage();
    }
}
