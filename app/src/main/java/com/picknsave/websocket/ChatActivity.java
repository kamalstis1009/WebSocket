package com.picknsave.websocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

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

    private void init() {

    }

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
        StompClient stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, ConstantKey.SOCKET_SERVER_URL);
        Toast.makeText(this, "Start connecting to server", Toast.LENGTH_SHORT).show();
        stompClient.connect();
        StompUtils.lifecycle(stompClient);

        Log.i(ConstantKey.TAG, "Subscribe chat endpoint to receive response");
        stompClient.topic(ConstantKey.CHAT_RESPONSE.replace(ConstantKey.PLACEHOLDER, userId)).subscribe(stompMessage -> {
            JSONObject jsonObject = new JSONObject(stompMessage.getPayload());
            Log.i(ConstantKey.TAG, "Receive: " + jsonObject.toString());
            runOnUiThread(() -> {
                try {
                    showText.append(jsonObject.getString("response") + "\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
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

        submitButton.setOnClickListener(v -> {
            chatUserId = chatUserIdText.getText().toString();
            if (chatUserId.length() == 0) {
                return;
            }
            submitButton.setEnabled(false);
            sendButton.setEnabled(true);
        });

        //-----------------------------------------------| Sent Message
        sendButton.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userID", chatUserId);
                jsonObject.put("fromUserID", userIdText.getText().toString());
                jsonObject.put("message", chatMessageText.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (chatUserId == null || chatUserId.length() == 0) {
                chatUserId = chatUserIdText.getText().toString();
            }
            stompClient.send(ConstantKey.CHAT, jsonObject.toString()).subscribe();
            chatMessageText.setText("");
        });

        //-----------------------------------------------| Goto Other Activity
        broadcastButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(ChatActivity.this, BroadcastActivity.class);
            startActivity(intent);
            this.finish();
        });
        groupButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(ChatActivity.this, GroupActivity.class);
            startActivity(intent);
            this.finish();
        });
    }
}