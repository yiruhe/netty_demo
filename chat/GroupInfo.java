package com.netty.chat;

import io.netty.channel.group.ChannelGroup;

/**
 * @author tiankong
 * @date 2019/11/19 18:21
 */
public class GroupInfo {
    private ChatGroupDo group;
    private ChannelGroup channelGroup;

    public ChatGroupDo getGroup() {
        return group;
    }

    public void setGroup(ChatGroupDo group) {
        this.group = group;
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    public GroupInfo(ChatGroupDo group, ChannelGroup channelGroup) {
        this.group = group;
        this.channelGroup = channelGroup;
    }
}
