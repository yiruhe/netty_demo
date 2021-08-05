package com.netty.client;

import com.netty.base.ChannelHandlerHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

public class DefaultNSocketClientConnector extends NettyClientConnector{

    private  ConnectionWatchdog dog;

    private int tryCount=0;

    private ChannelHandlerHolder channelHandlers;


    public ChannelFuture connect(String host, int port, ChannelHandlerHolder channelHandlers) throws InterruptedException {

            this.channelHandlers = channelHandlers;

        return  this.connect(host,port);
    }


    @Override
    public ChannelFuture connect(String host, int port) throws InterruptedException {

        final Bootstrap boot = bootstrap();


        dog  = new ConnectionWatchdog(boot,timer,port,host){

            @Override
            public ChannelHandler[] handlers() {

                if(channelHandlers != null){
                    ChannelHandler[] handlers = channelHandlers.handlers();
                    ChannelHandler[] handlerArr = new ChannelHandler[handlers.length+1];
                    handlerArr[0]=this;
                    System.arraycopy(handlers,0,handlerArr,1,handlers.length);
                    return  handlerArr;
                }else{
                    return new ChannelHandler[]{
                            this
                    };
                }

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


            future = doConnection(host,port);
           // future = boot.connect(host, port);
        }

        /*future.addListener(new ChannelFutureListener(){

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
        });*/


        ChannelFuture sync = future.sync();

        return sync;
    }

    private ChannelFuture doConnection(String host, int port) {

        final Bootstrap boot = bootstrap();
        ChannelFuture future = boot.connect(host, port);
        future.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {  // 没有连接成功，进行 “重连”
                    if(tryCount < 3){
                        tryCount++;
                        future.channel().eventLoop().schedule(() -> {
                            System.out.println("Connection Netty Server failed, trying to reconnect..."+tryCount);

                            doConnection(host, port);

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

        return future;
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
