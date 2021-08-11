package com.mqtt.mqtt2;

import cn.jpush.api.report.MessageStatus;

import java.io.Serializable;

public class MqttMessageData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String topic;
    private byte[] payload;

    private int qos;
    private boolean retained;
    private boolean dup;
    private int messageId;

    private volatile MessageStatus status;

    public String getStringId() {
        return String.valueOf(messageId);
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
