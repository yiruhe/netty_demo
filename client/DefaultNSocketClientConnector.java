package com.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DefaultNSocketClientConnector extends NettyClientConnector{

    private  ConnectionWatchdog dog;

    private int tryCount=0;

    private List<ChannelHandler> channelHandlers = new ArrayList<>();


    public ChannelFuture connect(String host, int port, List<ChannelHandler> channelHandlers) throws InterruptedException {

        this.channelHandlers.addAll(channelHandlers);

        return  this.connect(host,port);
    }


    @Override
    public ChannelFuture connect(String host, int port) throws InterruptedException {

        final Bootstrap boot = bootstrap();


        dog  = new ConnectionWatchdog(boot,timer,port,host){

            @Override
            public ChannelHandler[] handlers() {

                channelHandlers.add(0,this);

                /*return new ChannelHandler[]{
                        this*//*,
                        //每隔30s的时间触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                        new IdleStateHandler(2, 0, 0, TimeUnit.SECONDS),
                        //实现userEventTriggered方法，并在state是WRITER_IDLE的时候发送一个心跳包到sever端，告诉server端我还活着
                        new ConnectorIdleStateTrigger()*//*
                };*/

                return channelHandlers.toArray(new ChannelHandler[0]);
            }

        };

        ChannelFuture future;
        synchronized (boot){
            boot.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();
                    pipeline.addLast(dog.handlers());

                }
            });

            future = boot.connect(host, port);
        }

        future.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {  // 没有连接成功，进行 “重连”
                    if(tryCount < 3){
                        tryCount++;
                        future.channel().eventLoop().schedule(() -> {
                            System.out.println("Connection Netty Server failed, trying to reconnect..."+tryCount);

                            try {
                                connect(host, port);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }, 3000, TimeUnit.MILLISECONDS);    // 设置 3s 进行一次 “重连”
                    }else{
                        System.out.println("连接失败，关闭服务");
                        boot.config().group().shutdownGracefully();
                    }

                } else {
                    System.out.println("Connect Netty Server successfully!");
                }
            }
        });

        ChannelFuture sync = future.sync();

        return sync;
    }

    public DefaultNSocketClientConnector() {
        init();
    }

    public DefaultNSocketClientConnector(int worker) {
        super(worker);
        init();
    }

    public void init(){
        super.init();
        bootstrap().channel(NioSocketChannel.class);

        bootstrap().option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false);

    }

    @Override
    public void shutdownGracefully() {
        if(dog != null){
            dog.stop();
        }
        super.shutdownGracefully();
    }
}
