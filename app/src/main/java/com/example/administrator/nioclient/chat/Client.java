package com.example.administrator.nioclient.chat;

import android.text.TextUtils;

public class Client {

    final String serverHost;
    final int port;
    /**
     * 服务器连接时间
     */
    final long timeout;
    String selfAccount;
    String friendAccount;
    private ClientHandler clientHandler;

    Client(Builder builder) {
        serverHost = builder.serverHost;
        port = builder.port;
        timeout = builder.timeout;
        selfAccount = builder.selfAccount;
        friendAccount = builder.friendAccount;
    }

    public static class Builder {

        String serverHost;
        int port;
        private long timeout;
        private String selfAccount;
        private String friendAccount;
        private static Client client;

        private static final Builder builder = new Builder();

        private Builder() {
        }

        public static Builder getBuilder() {
            return builder;
        }

        public Builder setServerHost(String serverHost, int port) {
            this.serverHost = serverHost;
            this.port = port;
            return this;
        }

        public Builder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setSelfAccount(String selfAccount) {
            this.selfAccount = selfAccount;
            return this;
        }

        public Builder setFriendAccount(String friendAccount) {
            this.friendAccount = friendAccount;
            return this;
        }

        public Client build() {
            if (client == null) {
                synchronized (this) {
                    if (client == null) {
                        client = new Client(this);
                    }
                }
            }
            return client;
        }
    }

    /**
     * 登录服务器
     *
     * @param selfAccount 用户账号
     * @param ccb         监听连接事件
     */
    public void login(String selfAccount, ConnectionCallBack ccb) {
        if (!TextUtils.isEmpty(selfAccount)) {
            this.selfAccount = selfAccount;
        }
        clientHandler = new ClientHandler(serverHost, port);
        Header header = new Header.Builder().addHeader("type", "login").addHeader("from", this.selfAccount).build();
        clientHandler.login(header, ccb);
    }

    public void setReplyListener(ClientHandler.ReadListener replyListener) {
        if (clientHandler != null) {
            clientHandler.setReadListener(replyListener);
        }
    }

    /**
     * 发送消息
     *
     * @param selfAccount   用户账号
     * @param friendAccount 好友账号
     * @param message       发送内容
     */
    public void sendMessage(String selfAccount, String friendAccount, String message) {
        if (!TextUtils.isEmpty(selfAccount)) {
            this.selfAccount = selfAccount;
        }
        if (!TextUtils.isEmpty(friendAccount)) {
            this.friendAccount = friendAccount;
        }
        if (clientHandler != null) {
            Header header = new Header.Builder().addHeader("type", "chat").addHeader("from", this.selfAccount).addHeader("to", this.friendAccount).build();
            message = header.toString() + "\n" + message;
            clientHandler.sendMsg(message);
        }
    }
}
