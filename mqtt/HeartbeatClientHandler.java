package com.netty.mqtt;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {
    //private static final Logger logger = LoggerFactory.getLogger(HeartbeatClientHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                //NettyLog.info("client WRITER_IDLE");
            } else if (e.state() == IdleState.READER_IDLE) {
             //   TranDataProtoUtil.writeAndFlushTranData(ctx, TranDataProtoUtil.getPingInstance());
                ping(ctx.channel());
             //   NettyLog.info("client READER_IDLE");
                return;

            } else if (e.state() == IdleState.ALL_IDLE) {
               // NettyLog.info("client ALL_IDLE");
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    public void ping(Channel channel) {

         MqttFixedHeader fixedHeader =
                new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);

         io.netty.handler.codec.mqtt.MqttMessage pingreq = MqttMessageFactory.newMessage(fixedHeader, null, null);
        channel.writeAndFlush(pingreq);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        super.channelRead(ctx, msg);
    }
}
