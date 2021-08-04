package com.netty.client;

import com.netty.base.NativeSupport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class NettyClientConnector implements ClientConnector {

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    protected final HashedWheelTimer timer = new HashedWheelTimer(new ThreadFactory() {

        private AtomicInteger threadIndex = new AtomicInteger(0);

        public Thread newThread(Runnable r) {
            return new Thread(r, "NettyClientConnectorExecutor_" + this.threadIndex.incrementAndGet());
        }
    });

    private Bootstrap bootstrap;
    private EventLoopGroup worker;
    private int nWorkers;


    public NettyClientConnector() {
        this(AVAILABLE_PROCESSORS << 1);
    }

    public NettyClientConnector(int nWorkers) {
        this.nWorkers = nWorkers;
    }


    protected  void init(){
        DefaultThreadFactory defaultThreadFactory = new DefaultThreadFactory("client.connector");
         worker = initEventLoopGroup(nWorkers, defaultThreadFactory);
         bootstrap = new Bootstrap().group(worker);

    }

    protected EventLoopGroup initEventLoopGroup(int nWorkers, ThreadFactory workerFactory) {
        return NativeSupport.isSupportNativeET() ? new EpollEventLoopGroup(nWorkers, workerFactory) : new NioEventLoopGroup(nWorkers, workerFactory);
    }

    public void shutdownGracefully() {
        worker.shutdownGracefully();
        timer.stop();
    }


    protected Bootstrap bootstrap() {
        return bootstrap;
    }

}
