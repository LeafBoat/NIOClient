package com.example.administrator.nioclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private EditText accounteET;
    private ClientHandler clientHandler;
    private EditText friendidET;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();
    }

    private void initdata() {
        findViews();
        clientHandler = new Client().start(new ClientHandler.ConnectListener() {
            @Override
            public void onConnectSuccesss() {
                System.out.println("连接服务器成功");
                String msg = accounteET.getText().toString() + "\r\nsave";
                try {
                    MessageParser.sendMessage(MessageParser.getSocketChannel(), msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("friend", friendidET.getText().toString());
                intent.putExtra("account", account);
                startActivity(intent);
            }

            @Override
            public void onConnectFailed() {

            }
        });
    }

    private void findViews() {
        accounteET = (EditText) findViewById(R.id.account);
        friendidET = (EditText) findViewById(R.id.friendid);
    }

    public void connect(View v) {
        account = accounteET.getText().toString();
        String msg = account+"\r\n+save\r\n";
        try {
            clientHandler.sendMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
