package com.netty.mqtt;

import com.netty.base.ChannelHandlerHolder;
import com.netty.client.DefaultNSocketClientConnector;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class MainTest {


    public static void main(String[] args) throws InterruptedException {


        DefaultNSocketClientConnector defaultNSocketClientConnector = new DefaultNSocketClientConnector(1);

        defaultNSocketClientConnector.connect("127.0.0.1", 1883, new ChannelHandlerHolder() {
            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                        new IdleStateHandler(10, 0, 0),

                        MqttEncoder.INSTANCE,
                        new MqttDecoder(),
                        new DefaultMqttHandler()

                };
            }
        });





    }
}
