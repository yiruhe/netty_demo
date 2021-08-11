package com.netty.mqtt;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultClientMqttHandler extends ChannelInboundHandlerAdapter {

    /**
     * 	客户端与服务端第一次建立连接时执行 在channelActive方法之前执行
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    /**
     * 	客户端与服务端 断连时执行 channelInactive方法之后执行
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }


    /**
     * 	从客户端收到新的数据时，这个方法会在收到消息时被调用
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception, IOException {
        MqttMessage mqttMessage = (MqttMessage) msg;
        System.out.println(" info of mqtt is --"+mqttMessage);

        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();

        MqttMessageType mqttMessageType = mqttFixedHeader.messageType();





    }

    /**
     * 	订阅确认
     * @param channel
     * @param mqttMessage
     */
    public static void suback(Channel channel, MqttSubscribeMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = mqttMessage.variableHeader();
        //	构建返回报文， 可变报头
        MqttMessageIdVariableHeader variableHeaderBack = MqttMessageIdVariableHeader.from(messageIdVariableHeader.messageId());
        Set<String> topics = mqttMessage.payload().topicSubscriptions().stream().map(mqttTopicSubscription -> mqttTopicSubscription.topicName()).collect(Collectors.toSet());
        //log.info(topics.toString());
        List<Integer> grantedQoSLevels = new ArrayList<>(topics.size());
        for (int i = 0; i < topics.size(); i++) {
            grantedQoSLevels.add(mqttMessage.payload().topicSubscriptions().get(i).qualityOfService().value());
        }
        //	构建返回报文	有效负载
        MqttSubAckPayload payloadBack = new MqttSubAckPayload(grantedQoSLevels);
        //	构建返回报文	固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2+topics.size());
        //	构建返回报文	订阅确认
        MqttSubAckMessage subAck = new MqttSubAckMessage(mqttFixedHeaderBack,variableHeaderBack, payloadBack);
      //  log.info("back--"+subAck.toString());
        channel.writeAndFlush(subAck);
    }


    /**
     * 	取消订阅确认
     * @param channel
     * @param mqttMessage
     */
    public static void unsuback(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        //	构建返回报文	可变报头
        MqttMessageIdVariableHeader variableHeaderBack = MqttMessageIdVariableHeader.from(messageIdVariableHeader.messageId());
        //	构建返回报文	固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2);
        //	构建返回报文	取消订阅确认
        MqttUnsubAckMessage unSubAck = new MqttUnsubAckMessage(mqttFixedHeaderBack,variableHeaderBack);
       // log.info("back--"+unSubAck.toString());
        channel.writeAndFlush(unSubAck);
    }

    /**
     * 	心跳响应
     * @param channel
     * @param mqttMessage
     */
    public static void pingresp (Channel channel, MqttMessage mqttMessage) {
        //	心跳响应报文	11010000 00000000  固定报文
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage mqttMessageBack = new MqttMessage(fixedHeader);
       // log.info("back--"+mqttMessageBack.toString());
        channel.writeAndFlush(mqttMessageBack);
    }





    private void puback(Channel channel, MqttPublishMessage mqttMessage) {


        MqttFixedHeader mqttFixedHeaderInfo = mqttMessage.fixedHeader();
        MqttQoS qos = (MqttQoS) mqttFixedHeaderInfo.qosLevel();
        byte[] headBytes = new byte[mqttMessage.payload().readableBytes()];
        mqttMessage.payload().readBytes(headBytes);
        String data = new String(headBytes);

        switch (qos) {
            case AT_MOST_ONCE: 		//	至多一次
                break;
            case AT_LEAST_ONCE:		//	至少一次
                //	构建返回报文， 可变报头
                MqttMessageIdVariableHeader mqttMessageIdVariableHeaderBack = MqttMessageIdVariableHeader.from(mqttMessage.variableHeader().packetId());
                //	构建返回报文， 固定报头
                MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.PUBACK,mqttFixedHeaderInfo.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeaderInfo.isRetain(), 0x02);
                //	构建PUBACK消息体
                MqttPubAckMessage pubAck = new MqttPubAckMessage(mqttFixedHeaderBack, mqttMessageIdVariableHeaderBack);
               // log.info("back--"+pubAck.toString());
                channel.writeAndFlush(pubAck);
                break;
            case EXACTLY_ONCE:		//	刚好一次
                //	构建返回报文， 固定报头
                MqttFixedHeader mqttFixedHeaderBack2 = new MqttFixedHeader(MqttMessageType.PUBREC,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
                //	构建返回报文， 可变报头
                MqttMessageIdVariableHeader mqttMessageIdVariableHeaderBack2 = MqttMessageIdVariableHeader.from(mqttMessage.variableHeader().packetId());
                MqttMessage mqttMessageBack = new MqttMessage(mqttFixedHeaderBack2,mqttMessageIdVariableHeaderBack2);
              //  log.info("back--"+mqttMessageBack.toString());
                channel.writeAndFlush(mqttMessageBack);
                break;
            default:
                break;
        }




    }


    public static void connack (Channel channel, MqttConnectMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        MqttConnectVariableHeader mqttConnectVariableHeaderInfo = mqttMessage.variableHeader();

        MqttConnAckVariableHeader mqttConnAckVariableHeader1 = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, mqttConnectVariableHeaderInfo.isCleanSession());


        MqttFixedHeader mqttFixedHeader1 = new MqttFixedHeader(MqttMessageType.CONNACK, mqttFixedHeader.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeader.isRetain(), 0x02);

        //	构建CONNACK消息体
        MqttConnAckMessage connAck = new MqttConnAckMessage(mqttFixedHeader1, mqttConnAckVariableHeader1);


        channel.writeAndFlush(connAck);
    }


    public  void connectBack(MqttConnAckMessage mqttConnAckMessage){
        MqttConnAckVariableHeader mqttConnAckVariableHeader = mqttConnAckMessage.variableHeader();
        switch ( mqttConnAckVariableHeader.connectReturnCode()){
            case CONNECTION_ACCEPTED:
                System.out.println("连接成功！");

                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                System.out.println("login error 用户名密码错误");
                break;
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                System.out.println("clientId  不允许链接");
                break;
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                System.out.println("服务不可用");
                break;
            case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                System.out.println("mqtt 版本不可用");
                break;
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                System.out.println("未授权登录");
                break;
        }

    }


}
