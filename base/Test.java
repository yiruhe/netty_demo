package com.netty.base;

import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Test {

    //存放channel
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup("ChannelGroups", GlobalEventExecutor.INSTANCE);

    public static Map<String,ChannelFuture> nettyMap = Maps.newConcurrentMap();

    public static void main(String[] args) throws InterruptedException {
        DefaultNSocketCommonSrv defaultNSocketCommonSrv = new DefaultNSocketCommonSrv(8866);


        new Thread(()->{
            ChannelFuture start = null;
            try {
                start = defaultNSocketCommonSrv.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            assert start != null;
            CHANNEL_GROUP.add(start.channel());
        }).start();

        Bootstrap bootstrap = new Bootstrap();
        AcceptorIdleStateTrigger acceptorIdleStateTrigger = new AcceptorIdleStateTrigger();


        Thread.sleep(20000);

        AcceptorIdleStateTrigger idleStateTrigger = new AcceptorIdleStateTrigger();

        NettyNetWatchdog nettyNetWatchdog = new NettyNetWatchdog(null, new HashedWheelTimer(), 1, "", true) {

            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                        this,
                        new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                        idleStateTrigger
                };
            }
        };

        ChannelFuture future;
        //进行连接
        bootstrap.handler(new ChannelInitializer<Channel>() {

                    //初始化channel
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(nettyNetWatchdog.handlers());
                    }
                });

        future = bootstrap.connect("",1);
        future.sync();

            // 以下代码在synchronized同步块外面是安全的



      //  ChannelFuture channelFuture = nettyMap.get("1");

      //  channelFuture.channel().close();
       // defaultNSocketCommonSrv.shutdownGracefully();
    }
}
