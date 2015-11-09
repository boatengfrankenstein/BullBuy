package com.mycompany.bullbuy;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Edited by Dane to include messaging activity */

public class Messenger extends AppCompatActivity {

    //class variables
    private static String currentUserUn;
    private static String recipientUn;
    private static String conversationId;
    private EditText etMessage;
    private Button btSend;

    //--//
    private ListView messagesListView;
    private ArrayList<Message> mMessages;
    private MessageAdapter mAdapter;
    private boolean mFirstLoad;

    //--//
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        //get recipient username and conversation from intent
        Intent intent = getIntent();
        recipientUn = intent.getStringExtra("RECIPIENT_ID");
        conversationId = intent.getStringExtra("CONVERSATION_ID");
        //get current user ID
        currentUserUn = ParseUser.getCurrentUser().getUsername();
        setupMessagePosting();

        //refresh messages display every 100ms
        handler.postDelayed(runnable, 100);
    }

    //defines the runnable that is used to refresh the messages
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 100);
        }
    };

    private void refreshMessages() {
        receiveMessage();
    }

    //Set up button event handler, which posts the entered message to Parse
    private void setupMessagePosting() {
        //assign EditText and Button
        etMessage = (EditText) findViewById(R.id.txtTextBody);
        btSend = (Button) findViewById(R.id.btnSend);
        //--//
        messagesListView = (ListView) findViewById(R.id.listMessages);
        mMessages = new ArrayList<Message>();

        //automatically scroll to the bottom when new message is received
        messagesListView.setTranscriptMode(1);
        mFirstLoad = true;
        mAdapter = new MessageAdapter(Messenger.this, currentUserUn, recipientUn, mMessages);
        messagesListView.setAdapter(mAdapter);

        //Create message object in Parse when Send button is clicked
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                Message message = new Message();
                message.setSenderId(currentUserUn);
                message.setRecipientId(recipientUn);
                message.setParentConversation(conversationId);
                message.setBody(data);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        receiveMessage();
                    }
                });
                etMessage.setText("");
            }
        });
    }

    //Query messages from Parse to be loaded into the chat adapter
    private void receiveMessage() {
        //queries variable will contain two queries that are passed to the or function
        ArrayList<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();

        //initialize queries for conversations
        ParseQuery<Message> senderQuery = ParseQuery.getQuery(Message.class);
        ParseQuery<Message> recipientQuery = ParseQuery.getQuery(Message.class);
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);

        //use senderQuery to check "senderId" column for current user
        senderQuery.whereEqualTo("senderId", currentUserUn);
        //use recipientQuery to check "recipientId" column for current user
        recipientQuery.whereEqualTo("recipientId", currentUserUn);
        //add both queries to queries variable, which will be passed to the or function
        queries.add(senderQuery);
        queries.add(recipientQuery);

        //assign query to account for current user being either sender OR recipient
        query.or(queries);

        query.whereEqualTo("parentConversation", conversationId);
        query.orderByAscending("createdAt");

        /* THIS SECTION FORMER CODE - TESTING ABOVE NEW CODE
        //initialize query
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);

        //configure query
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createdAt");
        String[] uns = {currentUserUn, recipientUn};
        query.whereContainedIn("senderId", Arrays.asList(uns));
        query.whereContainedIn("recipientId", Arrays.asList(uns));
        query.whereEqualTo("parentConversation", conversationId);
        */

        //fetch messages by executing query
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); //update adapter

                    //Scroll to bottom
                    if (mFirstLoad) {
                        messagesListView.setSelection(mAdapter.getCount());
                        mFirstLoad = false;
                    }
                }
                else {
                    Toast.makeText(Messenger.this, "Error from Messenger.receiveMessage().", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messenger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
