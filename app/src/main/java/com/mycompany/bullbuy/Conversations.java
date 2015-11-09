package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Conversations extends AppCompatActivity {

    //class variables
    TextView tvItem;
    TextView tvDate;
    private ListView conversationsListView;
    private ArrayList<Conversation> cConversations;
    private ConversationAdapter cAdapter;
    private String currentUserUn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        conversationsListView = (ListView) findViewById(R.id.list_Conversations);
        cConversations = new ArrayList<Conversation>();
        cAdapter = new ConversationAdapter(Conversations.this, cConversations);
        conversationsListView.setAdapter(cAdapter);

        loadConversations();
    }

    //create clickable list of user's conversations by querying them from Parse.
    public void loadConversations() {
        //get current user's username
        currentUserUn = ParseUser.getCurrentUser().getUsername();

        //queries variable will contain two queries that are passed to the or function
        ArrayList<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();

        //initialize queries for conversations
        ParseQuery<Conversation> buyerQuery = ParseQuery.getQuery(Conversation.class);
        ParseQuery<Conversation> sellerQuery = ParseQuery.getQuery(Conversation.class);
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);

        //use buyerQuery to check "buyerUn" column for current user
        buyerQuery.whereEqualTo("buyerUn", currentUserUn);
        //use sellerQuery to check "sellerUn" column for current user
        sellerQuery.whereEqualTo("sellerUn", currentUserUn);
        //add both queries to queries variable, which will be passed to the or function
        queries.add(buyerQuery);
        queries.add(sellerQuery);

        //assign query to account for current user being either buyer OR seller
        query.or(queries);

        query.orderByDescending("createdAt");
        //query.whereEqualTo("sellerUn", currentUserUn);
        query.whereEqualTo("buyerUn", currentUserUn);

        //fetch conversations by executing query
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> listOfConversations, ParseException e) {
                if (e == null) {
                    cConversations.clear();
                    cConversations.addAll(listOfConversations);
                    cAdapter.notifyDataSetChanged(); //update adapter

                    //make list clickable
                    conversationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(cConversations, i);
                        }
                    });
                }
                else {
                    Toast.makeText(Conversations.this, "Error from Conversations.loadConversations().", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openConversation(ArrayList<Conversation> a, int pos) {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        String oid = a.get(pos).getObjectId();
        query.whereEqualTo("objectId", oid);
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getApplicationContext(), Messenger.class);
                    intent.putExtra("RECIPIENT_ID", objects.get(0).getSellerUn());
                    intent.putExtra("CONVERSATION_ID", objects.get(0).getObjectId());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Conversations.this, "Error from Conversations.openConversation().", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversations, menu);
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
