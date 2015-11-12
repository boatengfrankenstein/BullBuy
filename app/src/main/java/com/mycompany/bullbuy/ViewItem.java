package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ViewItem extends AppCompatActivity implements View.OnClickListener {

    // declare parseimageview, textviews, button, other variables
    private String postObjectID;
    private ParseImageView itemPhoto;
    private TextView itemTitle;
    private TextView itemPrice;
    private TextView itemDescription;
    private Button messageButton;
    private Button addButton;

    // Added by Dane - gets item creator's name, item's title,
    // and current user
    private String thisObjectTitle;
    private String username;
    private static String currentUserUn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        itemPhoto = (ParseImageView) findViewById(R.id.photo_ViewItem);
        itemTitle = (TextView) findViewById(R.id.title_ViewItem);
        itemPrice = (TextView) findViewById(R.id.price_ViewItem);
        itemDescription = (TextView) findViewById(R.id.description_ViewItem);
        addButton = (Button) findViewById(R.id.addButton_ViewItem);
        messageButton = (Button) findViewById(R.id.messageSellerButton_ViewItem);

        // get id to know which postobject was selected in home activity to query table for that postobject
        Intent intent = getIntent();
        postObjectID = intent.getStringExtra(Home.MESSAGE);

        //Added by Dane - initialize conversation strings
        username = "";
        thisObjectTitle = "";
        currentUserUn = ParseUser.getCurrentUser().getUsername();

        // get the parsefile of the postobject and postobject itself and load on screen
        ParseQuery<ParseObject> query = new ParseQuery<>("PostObject");
        query.getInBackground(postObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject PostObject, ParseException e) {
                if(e == null) {
                    ParseFile photoFile = PostObject.getParseFile("Photo");
                    if(photoFile != null) {
                        itemPhoto.setParseFile(photoFile);
                        itemPhoto.loadInBackground();
                    }

                    itemTitle.setText(PostObject.get("Title").toString());
                    itemPrice.setText('$' + PostObject.get("Price").toString());
                    itemDescription.setText(PostObject.get("Description").toString());
                    //Added by Dane - gets item's title and creator's username for messaging function
                    setThisObjectTitle(PostObject.get("Title").toString());
                    setUsername(PostObject.get("User").toString());
                }
            }
        });

        addButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
    }

    //Quick function to set username of item's poster - added by Dane
    public void setUsername(String string) {
        username = string;
    }

    //Quick function to set title of the item - added by Dane
    public void setThisObjectTitle(String string) {
        thisObjectTitle = string;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.messageSellerButton_ViewItem:
                messageSellerClicked();
                break;
            case R.id.addButton_ViewItem:
                addClicked();
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
                        conversation.setParticipants(username + " " + currentUserUn);
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

    /* If user wants to add the item to the watchlist, then add the object's id to the file
     * in internal storage. Objectids later used to determine which object to query Parse for
     */
    private void addClicked(){
        File file = new File(getFilesDir().getAbsolutePath() + "/Watchlist");

        int fileMode;
        if(file.exists()) {
            fileMode = MODE_APPEND;
        }
        else {
            fileMode = MODE_PRIVATE;
        }

        try {
            FileOutputStream stream = openFileOutput("Watchlist", fileMode);
            stream.write((postObjectID + "\n").getBytes());
            stream.close();
            Toast.makeText(this, "Added to Watchlist", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, Watch.class);
        startActivity(intent);
    }
}
