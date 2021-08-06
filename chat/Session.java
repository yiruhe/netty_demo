package com.netty.chat;

/**
 * @author tiankong
 * @date 2019/11/17 18:43
 */
public class Session {
    private Integer userId;
    private String username;
    private String avatar;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * token
     * 用户验证用户的token是否过期
     */
    private String token;

    public Session(Integer userId, String username, String avatar, String token) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.token = token;
    }
}
