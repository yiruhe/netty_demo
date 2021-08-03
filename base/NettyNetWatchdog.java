package com.netty.base;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

public abstract class NettyNetWatchdog extends ChannelInboundHandlerAdapter implements TimerTask,ChannelHandlerHolder{


    private final Bootstrap bootstrap;
    private Timer timer;
    private int port;
    private String host;
    private volatile boolean reconnect = true;
    private int attempts;

    public NettyNetWatchdog(Bootstrap bootstrap, Timer timer, int port,String host, boolean reconnect) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("链接关闭");
        if(reconnect){
          //  System.out.println("链接关闭，将进行重连");
            if (attempts < 12) {
                attempts++;
                //重连的间隔时间会越来越长
                int timeout = 2 << attempts;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
        ctx.fireChannelInactive();
    }


    @Override
    public void run(Timeout timeout) throws Exception {


        ChannelFuture connect;

        synchronized (bootstrap){
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(handlers());
                }
            });

            connect = bootstrap.connect(host, port);
        }


        connect.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                boolean succeed = channelFuture.isSuccess();

                if (!succeed) {
                 //   System.out.println("重连失败");
                    channelFuture.channel().pipeline().fireChannelInactive();
                }else{
                 //   System.out.println("重连成功");
                }
            }
        });


    }
}
