package com.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private int readIdleTimeCnt = 0;
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            switch (event.state()) {
                case READER_IDLE:
                    System.out.println("客户端[读空闲]"+readIdleTimeCnt);
                    readIdleTimeCnt++;
                    break;

            }

            if (readIdleTimeCnt >= 3) {
                //System.out.println("客户端[读空闲]次数 超过3次，服务端关闭连接...");
                System.out.println("超过3次，服务端关闭连接");
                throw new Exception("NO SIGNAL");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
