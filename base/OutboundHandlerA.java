package com.netty.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

public class OutboundHandlerA extends ChannelOutboundHandlerAdapter {

    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerA bind");
    }

    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerA connect");
    }

    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerA disconnect");
    }

    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerA close");
    }

    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerA deregister");
    }

    public void read(ChannelHandlerContext ctx) throws Exception {
        System.out.println("OutboundHandlerA read");
        super.read(ctx);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerA write");
    }

    public void flush(ChannelHandlerContext ctx) throws Exception {
        System.out.println("OutboundHandlerA flush");
    }
}
