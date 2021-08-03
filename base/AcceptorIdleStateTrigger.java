package com.netty.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class AcceptorIdleStateTrigger  extends ChannelInboundHandlerAdapter {


    private int readIdleTimeCnt = 0;



    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        IdleState state = event.state();
        switch (state){
            case READER_IDLE: // 读空闲
                readIdleTimeCnt++;
        }

        if (readIdleTimeCnt >= 3) {
         //   System.out.println("客户端[读空闲]次数 超过3次，服务端关闭连接...");
         //   ctx.channel().writeAndFlush("idle close");
            ctx.close();
        }

    }
}
