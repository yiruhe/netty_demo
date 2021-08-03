package com.netty.base;

import io.netty.channel.ChannelHandler;

/**
 * 存放ChannelHandler
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] handlers();
}
