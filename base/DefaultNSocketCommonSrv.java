package com.netty.base;

import com.nn2.SayHelloServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class DefaultNSocketCommonSrv extends NettyCommonSrv{
    DefaultNSocketCommonSrv(SocketAddress address, int nWorkers) {
        super(address, nWorkers);
    }

    DefaultNSocketCommonSrv(SocketAddress address) {
        super(address);
    }

    public DefaultNSocketCommonSrv(int port) {
        super(new InetSocketAddress(port));
        this.init();
    }

    protected void init(){
        super.init();

        bootstrap().option(ChannelOption.SO_BACKLOG, 32768)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }


    @Override
    protected ChannelFuture bind(SocketAddress localAddress) {

        ServerBootstrap boot = bootstrap();

        boot.channel(NioServerSocketChannel.class)

                .childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new InboundHandlerB()
                );
                ch.pipeline().addLast(
                        new SayHelloServerHandler()
                       );

                ch.pipeline().addLast(
                        new OutboundHandlerB()
                );
                ch.pipeline().addLast(
                        new OutboundHandlerA()
                );
            }
        });

        return boot.bind(localAddress);
    }


    public ChannelFuture start() throws InterruptedException {

        return bind(localAddress).sync();
    }
}
