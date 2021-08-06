package com.netty.base;


import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
*
*
* websocket channelHandler 请求处理工厂类
* 生成 WebSocketNettyServer 中的netty 的ChannelHandler 的处理类
* Created by zhuangjiesen on 2017/9/13.
*/
public class WebSocketChannelHandlerFactory  {



    //全局websocket连接对象存储
    private static Map<String ,WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();



    /**
     * 服务端保活清除过期或者失联连接 处理器
     *  微信浏览器打开页面关闭后(或者另外的场景)不会触发close消息 ，所以又做了一层心跳机制
     *
     *
     * 策略是通过定时器
     * 1.遍历客户端发送ping消息 ，然后在缓存中 pingPongMap  标记 1已发送 (或者n 表示发送次数)
     * 2.客户端接收ping 消息会返回pong 消息 (websocket协议标准 见RFC6455)
     * 3.在接收端 doHandleRequest() 方法中(接收到非close frame )说明客户端有反馈，表示存活，删除 pingPongMap
     * 4.接着下一个周期定时器去遍历 pingPongMap 查出已经发送ping 无响应超过 MAX_RE_PING 次数
     * 5.删除超过 MAX_RE_PING 无响应的客户端
     * 6.每次接收到客户端的消息，都会调用 doHandleRequest() 方法清除 pingPongMap 表示客户端存活
     *
     * Created by zhuangjiesen on 2017/9/14.
     */
    private static class KeepAliveHandlerAdapter {

        //轮训时间 检测过期连接 定时器定时时间
        private final static int SCHEDULE_SECONDS = 30;
        private static ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1);

        /*标记状态*/
        private static volatile boolean isSent = true;

        /** 允许保活次数， 超过这个数值认为失联，清理连接**/
        private static volatile int MAX_RE_PING = 5;
        //心跳  已发送次数
        private static ConcurrentHashMap<String , Integer> pingPongChannelsMap = new ConcurrentHashMap<>();


        static {
            scheduleService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    //LogUtils.logDebug(this, "  保活，清理线程启动.... ");
                    try {
                        if (isSent) {
                            isSent = false;
                            //定时发送心跳
                            sendPingMessageToAll();

                        } else {
                            isSent = true;
                            clearNotPingPongMessage();
                        }
                    } catch (Exception e) {
                    }
                }
            } , 1L , SCHEDULE_SECONDS , TimeUnit.SECONDS);
        }


        /*
        * 给所有客户端发送ping 消息
        *
        * */
        public static void sendPingMessageToAll(){
            Collection<WebSocketSession> sessions = WebSocketChannelHandlerFactory.webSocketSessions.values();;
            if (sessions != null) {
                for (WebSocketSession socketSession : sessions) {
                    PingWebSocketFrame ping = new PingWebSocketFrame();
                    socketSession.sendMessage(ping);
                    Integer pingTimes = KeepAliveHandlerAdapter.pingPongChannelsMap.get(socketSession.getId());
                    if (pingTimes != null) {
                        KeepAliveHandlerAdapter.pingPongChannelsMap.put(socketSession.getId() , pingTimes.intValue() + 1);
                    } else {
                        KeepAliveHandlerAdapter.pingPongChannelsMap.put(socketSession.getId() , 1);
                    }
                }
            }
        }



        /*
        * 清理上次保活操作发送ping 消息得不到反馈的连接
        *
        *
        * */
        public static void clearNotPingPongMessage(){
            Collection<WebSocketSession> sessions = WebSocketChannelHandlerFactory.webSocketSessions.values();;
            if (sessions != null) {
                for (WebSocketSession socketSession : sessions) {
                    String id = socketSession.getId();
                    Integer pingTimes = KeepAliveHandlerAdapter.pingPongChannelsMap.get(id);
                    if (pingTimes != null && pingTimes.intValue() >= MAX_RE_PING) {
                        //断开连接
                        socketSession.sendMessage(new CloseWebSocketFrame());

                        //清除客户端对象
                        KeepAliveHandlerAdapter.pingPongChannelsMap.remove(id);
                        webSocketSessions.remove(id);
                    }
                }
            }
        }





        /*
        * 收到pong 消息，表示连接存活
        *
        *
        * */
        public static void receivePongMessage(String id){
            KeepAliveHandlerAdapter.pingPongChannelsMap.remove(id);
        }


    }






}
