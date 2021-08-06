package com.netty.chat;

import io.netty.channel.group.ChannelGroup;

/**
 * @author tiankong
 * @date 2019/11/20 11:22
 */
public class RoomInfo {
    private ChatRoomDo chatRoomDo;
    private ChannelGroup channelGroup;

    public RoomInfo() {
    }

    public RoomInfo(ChatRoomDo chatRoomDo, ChannelGroup channelGroup) {
        this.chatRoomDo = chatRoomDo;
        this.channelGroup = channelGroup;
    }

    public ChatRoomDo getChatRoomDo() {
        return chatRoomDo;
    }

    public void setChatRoomDo(ChatRoomDo chatRoomDo) {
        this.chatRoomDo = chatRoomDo;
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }
}
