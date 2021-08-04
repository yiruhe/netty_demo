package com.netty.client;

import io.netty.channel.ChannelFuture;

public interface ClientConnector {

    ChannelFuture connect(String host, int port) throws InterruptedException;

    void shutdownGracefully();

}
