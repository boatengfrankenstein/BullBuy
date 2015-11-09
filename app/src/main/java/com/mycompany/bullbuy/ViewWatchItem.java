package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ViewWatchItem extends AppCompatActivity implements View.OnClickListener {

    private String postObjectID;
    private ParseObject _PostObject;

    private ArrayList<String> IDs = new ArrayList<String>();

    private ParseImageView itemPhoto;
    private TextView itemTitle;
    private TextView itemPrice;
    private TextView itemDescription;
    private Button messageButton;
    private Button removeButton;

    // Added by Dane - gets item creator's name, item's title,
    // and current user
    private String thisObjectTitle;
    private String username;
    private static String currentUserUn;
    private String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_watch_item);

        itemPhoto = (ParseImageView) findViewById(R.id.photo_ViewWatchItem);
        itemTitle = (TextView) findViewById(R.id.title_ViewWatchItem);
        itemPrice = (TextView) findViewById(R.id.price_ViewWatchItem);
        itemDescription = (TextView) findViewById(R.id.description_ViewWatchItem);
        messageButton = (Button) findViewById(R.id.messageSellerButton_ViewWatchItem);
        removeButton = (Button) findViewById(R.id.removeButton_ViewWatchItem);

        Intent intent = getIntent();
        postObjectID = intent.getStringExtra(Watch.MESSAGE);

        //Added by Dane - initialize conversation strings
        username = "";
        thisObjectTitle = "";
        currentUserUn = ParseUser.getCurrentUser().getUsername();
        conversationId = "";

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PostObject");
        query.getInBackground(postObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject PostObject, ParseException e) {
                if(e == null){
                    _PostObject = PostObject;
                    ParseFile photoFile = PostObject.getParseFile("Photo");
                    itemPhoto.setParseFile(photoFile);
                    itemPhoto.loadInBackground();

                    itemTitle.setText(PostObject.get("Title").toString());
                    itemPrice.setText('$' + PostObject.get("Price").toString());
                    itemDescription.setText(PostObject.get("Description").toString());
                    //Added by Dane - gets item's title and creator's username for messaging function
                    setThisObjectTitle(PostObject.get("Title").toString());
                    setUsername(PostObject.get("User").toString());
                }
            }
        });

        messageButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
    }

    //Quick function to set username of item's poster - added by Dane
    public void setUsername(String string) {
        username = string;
    }

    //Quick function to set title of the item - added by Dane
    public void setThisObjectTitle(String string) {
        thisObjectTitle = string;
    }

    //Quick function to set objectId of newly created conversation - added by Dane
    public void setThisConversationId(String string) {
        conversationId = string;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.messageSellerButton_ViewWatchItem:
                messageSellerClicked();
                break;
            case R.id.removeButton_ViewWatchItem:
                removeClicked();
                break;
        }
    }

    private void messageSellerClicked(){
        // Edited by Dane to create a new conversation if one does not exist for this item,
        // then send the user to the Conversations page

        //only create a conversation if one does not already exist
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo("itemTitle", thisObjectTitle);
        query.whereEqualTo("buyerUn", currentUserUn);
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> conversationMatch, ParseException e) {
                if (e == null) {
                    if (conversationMatch.size() == 0) {
                        //create conversation with object creator's username as recipient of messages
                        Conversation conversation = new Conversation();
                        conversation.setItemTitle(thisObjectTitle);
                        conversation.setSellerUn(username);
                        conversation.setBuyerUn(currentUserUn);
                        //save conversation to Parse
                        conversation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                launchNextActivity(true);
                            }
                        });
                    } else {
                        launchNextActivity(false);
                    }
                }
            }
        });
    }

    //function added by Dane
    //launch Conversations with different message, depending on
    // whether the conversation already existed or not
    private void launchNextActivity(boolean b) {
        if (b) {
            Toast.makeText(this, "Conversation opened; see top of list.", Toast.LENGTH_LONG).show();
        }
        else if (!b) {
            Toast.makeText(this, "Conversation already existed; choose from conversations list.", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, Conversations.class);
        startActivity(intent);
    }

    private void removeClicked(){
        try {
            FileInputStream stream = openFileInput("Watchlist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String string = reader.readLine();
            while(string != null){
                IDs.add(string);
                string = reader.readLine();
            }
            reader.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IDs.remove(postObjectID);
       // Watch.IDs.remove(postObjectID);

        try {
            FileOutputStream stream = openFileOutput("Watchlist", MODE_PRIVATE);

            for (String postID : IDs){
                stream.write((postID + "\n").getBytes());
                //System.out.println(postID);
            }

            stream.close();
            Toast.makeText(this, "Removed From Watchlist", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, Watch.class);
        startActivity(intent);
    }
}
