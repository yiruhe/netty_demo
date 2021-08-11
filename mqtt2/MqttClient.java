package com.mqtt.mqtt2;

import com.netty.base.ChannelHandlerHolder;
import com.netty.client.ConnectionWatchdog;
import com.netty.client.DefaultNSocketClientConnector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.LinkedList;
import java.util.List;

public class MqttClient extends DefaultNSocketClientConnector {

    private final MqttConnectOptions info = new MqttConnectOptions();
    protected Channel channel;
    protected MqttConsumerListener  consumerListener;


    public ConnectionWatchdog  dog(){
        return  dog;
    }

    public void notTryConnect(){
        dog.stop();
    }

    public MqttConnectOptions mqttOptions() {
        return info;
    }


    public void setConsumerListener(MqttConsumerListener consumerListener) {
        this.consumerListener = consumerListener;
    }

    public ChannelFuture connectMqtt(String host, int port) throws InterruptedException {

        ChannelFuture connect = super.connect(host, port, new ChannelHandlerHolder() {
            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                        new IdleStateHandler(10, 0, 0),
                        MqttEncoder.INSTANCE,
                        new MqttDecoder(),
                        new DefaultMqttHandler(
                                MqttClient.this,
                                consumerListener)
                };
            }
        });
        channel = connect.channel();
        return  connect;
    }


    protected void pubMessage(MqttMessageData mqttMessage) {

        int msgId= MqttMessageId.messageId();
        if (! channel.isActive()) {
            //NettyLog.debug("channel is close");
            return;
        }

        if(mqttMessage.getQos()!=0){
            MqttMessageCache.put(msgId,mqttMessage);
        }

        channel.writeAndFlush(MqttProtocolUtil.publishMessage(mqttMessage));

    }


    public void sendPublishMessage(String topic, String message, int qosValue) {

        MqttMessageData mqttMessageData = new MqttMessageData();

        mqttMessageData.setTopic(topic);
        mqttMessageData.setQos(qosValue);
        mqttMessageData.setMessageId(MqttMessageId.messageId());
        mqttMessageData.setPayload(message.getBytes());
        mqttMessageData.setDup(false);

        pubMessage(mqttMessageData);
    }


    /**
     * 订阅
     * @param topic
     * @param qos
     */
    public void subscribe( String topic,MqttQoS qos){

        SubscribeMessage subscribeMessage = new SubscribeMessage(topic, qos);
        subscribe(subscribeMessage);
    }

    public void subscribe(SubscribeMessage... messages){
        if (info != null) {
            int messageId = MqttMessageId.messageId();
            if (!channel.isActive()) {
                System.out.println("channel is close");
                return;
            }

            List<MqttTopicSubscription> subscribeTopics = getSubscribeTopics(messages);

            MqttSubscribeMessage mqttSubscribeMessage = MqttProtocolUtil.subscribeMessage(subscribeTopics, messageId);

            channel.writeAndFlush(mqttSubscribeMessage);
        }

    }

    public static List<MqttTopicSubscription> getSubscribeTopics(SubscribeMessage[] sMsgObj) {
        if (sMsgObj != null) {
            List<MqttTopicSubscription> list = new LinkedList<>();
            for (SubscribeMessage sb : sMsgObj) {
                MqttTopicSubscription mqttTopicSubscription = new MqttTopicSubscription(sb.getTopic(),
                        sb.getQos());
                list.add(mqttTopicSubscription);
            }
            return list;
        } else {
            return null;
        }
    }



}
