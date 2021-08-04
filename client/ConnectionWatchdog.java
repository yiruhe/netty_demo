package com.netty.client;

import com.netty.base.ChannelHandlerHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@ChannelHandler.Sharable
public abstract class ConnectionWatchdog  extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {


    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;
    private final String host;

    private volatile boolean reconnect = true;
    private int attempts;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        attempts = 0;

        //logger.info("Connects with {}.", channel);
        System.out.println("Connects with"+channel);
        ctx.fireChannelActive();
    }

    public void stop(){
        reconnect = false;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        boolean doReconnect = reconnect;

        if(doReconnect){
            if(attempts < 12){
                System.out.println("链接关闭，将进行重连"+attempts);
                attempts++;
                long timeout = 2 << attempts;
                timer.newTimeout(this, timeout, MILLISECONDS);
            }else
            {
                System.out.println("链接关闭，将取消重连"+attempts);
                System.out.println("线程关闭");
                stop();
                timer.stop();
                bootstrap.config().group().shutdownGracefully();;
            }

        }


        ctx.fireChannelInactive();
    }


    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port, String host) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        ChannelFuture future;

        synchronized (bootstrap){

            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host,port);
        }

        future.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess();

                //logger.warn("Reconnects with {}, {}.", host+":"+port, succeed ? "succeed" : "failed");

                if (!succeed) {
                    System.out.println("重连失败");
                    f.channel().pipeline().fireChannelInactive();
                }else{
                    System.out.println("重连");
                }
            }
        });

    }
}
