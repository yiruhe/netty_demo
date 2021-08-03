package com.netty.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

public class OutboundHandlerB extends ChannelOutboundHandlerAdapter {

    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerB bind");
    }

    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerB connect");
    }

    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerB disconnect");
    }

    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerB close");
    }

    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerB deregister");
    }

    public void read(ChannelHandlerContext ctx) throws Exception {
        System.out.println("OutboundHandlerB read");
        ctx.read();
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerB write");
    }

    public void flush(ChannelHandlerContext ctx) throws Exception {
        System.out.println("OutboundHandlerB flush");
    }
}
