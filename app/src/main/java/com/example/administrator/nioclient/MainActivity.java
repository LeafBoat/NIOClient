package com.example.administrator.nioclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.nioclient.chat.Client;
import com.example.administrator.nioclient.chat.ConnectionCallBack;

public class MainActivity extends AppCompatActivity {

    private EditText accounteET;
    private EditText friendidET;
    private String account;
    private boolean isSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();
    }

    private void initdata() {
        findViews();
    }

    private void findViews() {
        accounteET = (EditText) findViewById(R.id.account);
        friendidET = (EditText) findViewById(R.id.friendid);
    }

    public void connect(final View v) {
        Client client = Client.Builder.getBuilder().setServerHost("172.26.52.1", 5555).build();
        if (TextUtils.isEmpty(accounteET.getText().toString())) {
            Toast.makeText(this, "请填写账号", Toast.LENGTH_SHORT).show();
            return;
        }
        client.login(accounteET.getText().toString(), new ConnectionCallBack() {
            @Override
            public void onConnect() {
                isSuccessful = true;
            }

            @Override
            public void onFail() {
                isSuccessful = false;
            }
        });
    }

    public void toActivity(View view) {
        if (!isSuccessful) {
            Toast.makeText(this, "还未连接成功", Toast.LENGTH_SHORT).show();
            return;
        }
        account = accounteET.getText().toString();
        try {
            Intent intent = new Intent(MainActivity.this, MessageActivity.class);
            intent.putExtra("friend", friendidET.getText().toString());
            intent.putExtra("account", account);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
