package com.netty.base;

import io.netty.channel.ChannelFuture;

public class Test2 {
    public static void main(String[] args) {
        ChannelFuture channelFuture = Test.nettyMap.get("1");

        channelFuture.channel().close();
    }
}
