package com.picknsave.websocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

@SuppressWarnings({"FieldCanBeLocal", "ResultOfMethodCallIgnored", "CheckResult"})
public class ChatActivity extends AppCompatActivity {

    private Button broadcastButton;
    private Button groupButton;
    private Button chatButton;

    private TextView userIdText;
    private EditText chatUserIdText;
    private Button submitButton;
    private EditText chatMessageText;
    private Button sendButton;
    private TextView showText;

    private String userId;
    private String chatUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //-----------------------------------------------| findViewById
        broadcastButton = findViewById(R.id.broadcast);
        groupButton = findViewById(R.id.groups);
        chatButton = findViewById(R.id.chat);
        chatButton.setEnabled(false);

        userIdText = findViewById(R.id.id);
        userId = String.valueOf(new Random().nextInt(100));
        userIdText.setText(userId);

        chatUserIdText = findViewById(R.id.chat_user_id);
        submitButton = findViewById(R.id.submit);
        submitButton.setEnabled(false);

        chatMessageText = findViewById(R.id.chat_message);
        sendButton = findViewById(R.id.send);
        sendButton.setEnabled(false);

        showText = findViewById(R.id.show);

        //-----------------------------------------------| StompClient Connect
        // Start connecting to server
        StompClient mClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, ConstantKey.SOCKET_SERVER_URL);
        mClient.connect();
        StompUtils.lifecycle(mClient); //Connection callback

        // Subscribe chat endpoint to receive response by RX Java
        mClient.topic(ConstantKey.CHAT_RESPONSE.replace(ConstantKey.PLACEHOLDER, userId)).subscribe(new Consumer<StompMessage>() {
            @Override
            public void accept(StompMessage message) throws Exception {
                JSONObject json = new JSONObject(message.getPayload());
                Log.i(ConstantKey.TAG, "Receive: " + json.toString());
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showText.append(json.getString("response") + "\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //-----------------------------------------------| Button Enable
        chatUserIdText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!submitButton.isEnabled())
                    submitButton.setEnabled(true);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatUserId = chatUserIdText.getText().toString();
                if (chatUserId.length() == 0) {
                    return;
                }
                submitButton.setEnabled(false);
                sendButton.setEnabled(true);
            }
        });

        //-----------------------------------------------| Sent Message
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("userID", chatUserId);
                    json.put("fromUserID", userIdText.getText().toString());
                    json.put("message", chatMessageText.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (chatUserId == null || chatUserId.length() == 0) {
                    chatUserId = chatUserIdText.getText().toString();
                }
                mClient.send(ConstantKey.CHAT, json.toString()).subscribe();
                chatMessageText.setText("");
            }
        });

        //-----------------------------------------------| Goto Other Activity
        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChatActivity.this, BroadcastActivity.class);
                ChatActivity.this.startActivity(intent);
                ChatActivity.this.finish();
            }
        });
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChatActivity.this, GroupActivity.class);
                ChatActivity.this.startActivity(intent);
                ChatActivity.this.finish();
            }
        });
    }
}