package com.netty.client;

import com.google.common.collect.Lists;
import com.nn2.SayHelloServerHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

import java.util.List;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        DefaultNSocketClientConnector defaultNSocketClientConnector = new DefaultNSocketClientConnector(1);

        List<ChannelHandler> list = Lists.newArrayList();

        list.add(new SayHelloServerHandler());

        ChannelFuture connect = defaultNSocketClientConnector.connect("127.0.0.1", 8877,list);




    }
}
