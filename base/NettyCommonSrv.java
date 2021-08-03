package com.netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

public abstract class NettyCommonSrv {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private ServerBootstrap bootstrap;
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private int nWorkers = 1;
    protected final SocketAddress localAddress;

    NettyCommonSrv(SocketAddress address,int nWorkers){
        this.localAddress = address;
        this.nWorkers = nWorkers;
    }

    NettyCommonSrv(SocketAddress address){
        this.localAddress = address;
        this.nWorkers = AVAILABLE_PROCESSORS;
    }


    protected void init(){

        ThreadFactory bossFactory = new DefaultThreadFactory("netty.common.boss");
        ThreadFactory workerFactory = new DefaultThreadFactory("netty.common.worker");

        boss = initEventLoopGroup(1,bossFactory);
        worker = initEventLoopGroup(nWorkers, workerFactory);

        bootstrap= new ServerBootstrap().group(worker, worker);
    }


    protected abstract ChannelFuture bind(SocketAddress localAddress);



    protected  EventLoopGroup initEventLoopGroup(int nthread, ThreadFactory bossFactory){
        return NativeSupport.isSupportNativeET() ? new EpollEventLoopGroup(nthread, bossFactory) : new NioEventLoopGroup(nthread, bossFactory);
    }


    protected ServerBootstrap bootstrap() {
        return bootstrap;
    }


    public void shutdownGracefully() {
        boss.shutdownGracefully().awaitUninterruptibly();
        worker.shutdownGracefully().awaitUninterruptibly();
    }
}
