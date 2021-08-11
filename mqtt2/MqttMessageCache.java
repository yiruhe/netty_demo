package com.mqtt.mqtt2;

import java.util.concurrent.ConcurrentHashMap;

public class MqttMessageCache {

    private static ConcurrentHashMap<Integer, MqttMessageData> message = new ConcurrentHashMap<>();

    public static  boolean put(Integer messageId, MqttMessageData mqttMessage){

        return message.put(messageId,mqttMessage)==null;

    }

    public static MqttMessageData get(Integer messageId){

        return  message.get(messageId);

    }


    public static MqttMessageData del(Integer messageId){
        return message.remove(messageId);
    }
}
