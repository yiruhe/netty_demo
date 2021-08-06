package com.netty.chat;

import io.netty.util.AttributeKey;

/**
 * @author tiankong
 * @date 2019/11/17 19:36
 */
public interface Attributes {
    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
    AttributeKey<Boolean> IS_LOGIN = AttributeKey.newInstance("isLogin");
}
