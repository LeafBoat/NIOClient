package com.example.administrator.nioclient;

public class Client {

    public ClientHandler start(ClientHandler.ConnectListener listener) {
        ClientHandler clientHandler = new ClientHandler("127.0.0.1", 5555);
        clientHandler.setConnectListener(listener);
        new Thread(clientHandler).start();
        return clientHandler;
    }
}
