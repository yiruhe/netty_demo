package com.netty.mqtt;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultMqttHandler extends SimpleChannelInboundHandler<Object> {

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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MqttFixedHeader connectFixedHeader =
                new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
                MqttVersion verinfo = MqttVersion.MQTT_3_1_1;
        //super.channelActive(ctx);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(verinfo.protocolName(),
                verinfo.protocolLevel(), false, false, false,
                0, false, false, 60);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload("21212","",
                new byte[1048], "", "".getBytes());
        MqttConnectMessage mqttSubscribeMessage = new MqttConnectMessage(connectFixedHeader, mqttConnectVariableHeader,
                mqttConnectPayload);
        ctx.writeAndFlush(mqttSubscribeMessage);

     /*   MqttFixedHeader connectFixedHeader =
                new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnectVariableHeader connectVariableHeader =
                new MqttConnectVariableHeader(PROTOCOL_NAME_MQTT_3_1_1, PROTOCOL_VERSION_MQTT_3_1_1, true, true, false,
                        0, false, false, 20);
        MqttConnectPayload connectPayload = new MqttConnectPayload(clientId, null, null, userName, password);
        MqttConnectMessage connectMessage =
                new MqttConnectMessage(connectFixedHeader, connectVariableHeader, connectPayload);
        ctx.writeAndFlush(connectMessage);*/
        System.out.println("Sent CONNECT");


    }


    /**
     * 	从客户端收到新的数据时，这个方法会在收到消息时被调用
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception, IOException {
        MqttMessage mqttMessage = (MqttMessage) msg;
        System.out.println(" info of mqtt is --"+mqttMessage);

        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();

        MqttMessageType mqttMessageType = mqttFixedHeader.messageType();


        switch(mqttMessageType){
            case CONNECT:
               // connectBack((MqttConnAckMessage)mqttMessage);
                connack(ctx.channel(),(MqttConnectMessage) mqttMessage);
                break;
            case CONNACK:
                connectBack(ctx.channel(),(MqttConnAckMessage)mqttMessage);
                ////	PUBACK报文是对QoS 1等级的PUBLISH报文的响应
                break;
            case PUBLISH:		 //PUBLISH控制包从客户端发送到服务器或从服务器发送到客户端以传输消息
                puback(ctx.channel(),(MqttPublishMessage)mqttMessage);
                break;
            case SUBSCRIBE:		//	客户端订阅主题
                //	客户端向服务端发送SUBSCRIBE报文用于创建一个或多个订阅，每个订阅注册客户端关心的一个或多个主题。
                //	为了将应用消息转发给与那些订阅匹配的主题，服务端发送PUBLISH报文给客户端。
                //	SUBSCRIBE报文也（为每个订阅）指定了最大的QoS等级，服务端根据这个发送应用消息给客户端
                // 	to do
                suback(ctx.channel(), (MqttSubscribeMessage)mqttMessage);
                break;
            case UNSUBSCRIBE:	//	客户端取消订阅
                //	客户端发送UNSUBSCRIBE报文给服务端，用于取消订阅主题
                //	to do
                unsub(ctx.channel(), mqttMessage);
                break;
            case PINGREQ:		//	客户端发起心跳
                //	客户端发送PINGREQ报文给服务端的
                //	在没有任何其它控制报文从客户端发给服务的时，告知服务端客户端还活着
                //	请求服务端发送 响应确认它还活着，使用网络以确认网络连接没有断开
                pingresp(ctx.channel(), mqttMessage);
                break;
                //DISCONNECT报文是客户端发给服务端的最后一个控制报文，表示客户端正常断开连接，而服务端不需要返回消息了，处理业务逻辑便可。
            case DISCONNECT:	//	客户端主动断开连接
                //	DISCONNECT报文是客户端发给服务端的最后一个控制报文， 服务端必须验证所有的保留位都被设置为0
                //	to do
                break;
            case PUBACK: // qos 1回复确认  ---> 删除消息

                break;
            case PUBREL: //sender 回复 client qos 2   删除消息
                MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
                int messageId = messageIdVariableHeader.messageId();
                handlePubRel(ctx.channel(),false,messageId);

                break;
            case PUBREC: // client 回复 sender qos 2   当Sender收到PUBREC，它就可以安全的丢弃掉初始Packet Identifier为P的PUBLISH数据包
                handlePubrec(ctx.channel(),mqttMessage);
                break;
            case PUBCOMP:  //删除保存的消息

                break;
            default:
                break;

        }



    }

    private void unsub(Channel channel, MqttMessage mqttMessage) {

            MqttFixedHeader fixedHeader = new MqttFixedHeader(
                    MqttMessageType.UNSUBSCRIBE,
                    false,
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    0
            );
            MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(1);

            List<String> topicFilters = new ArrayList<>();

            MqttUnsubscribePayload payload = new MqttUnsubscribePayload(topicFilters);
             new MqttUnsubscribeMessage(
                    fixedHeader,
                    variableHeader,
                    payload
            );

    }

    private void handlePubcomp(MqttMessage message){
        //this.client.getPendingPublishes().remove(variableHeader.messageId());
    }


    private void handlePubrec(Channel channel, MqttMessage message){

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
        MqttMessage pubrelMessage = new MqttMessage(fixedHeader, variableHeader);
        channel.writeAndFlush(pubrelMessage);
    }


    /**
     * 发送qos2 publish  确认消息 第二步
     */
    protected   void  handlePubRel(Channel channel,boolean isDup,int messageId){

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(fixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
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

        System.out.println("接受到的消息："+data);
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
                MqttFixedHeader mqttFixedHeaderBack2 = new MqttFixedHeader(MqttMessageType.PUBREC,false, MqttQoS.AT_MOST_ONCE,false,0x02);
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


    public  void connectBack(Channel channel,MqttConnAckMessage mqttConnAckMessage){
        MqttConnAckVariableHeader mqttConnAckVariableHeader = mqttConnAckMessage.variableHeader();
        switch ( mqttConnAckVariableHeader.connectReturnCode()){
            case CONNECTION_ACCEPTED:
                System.out.println("连接成功！");

                List<MqttTopicSubscription> list = new LinkedList<>();
                    MqttTopicSubscription mqttTopicSubscription = new MqttTopicSubscription("/test",
                            MqttQoS.AT_LEAST_ONCE);
                    list.add(mqttTopicSubscription);
                 subMessage(channel,list,1);
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


    private void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId){


        MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(mqttTopicSubscriptions);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE,false, MqttQoS.AT_LEAST_ONCE,false,0);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubscribeMessage mqttSubscribeMessage = new MqttSubscribeMessage(mqttFixedHeader,mqttMessageIdVariableHeader,mqttSubscribePayload);
        //log.info("SUBSCRIBE 发送订阅消息");
        channel.writeAndFlush(mqttSubscribeMessage);
    }


}
