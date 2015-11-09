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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
                }
            }
        });

        messageButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
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
        Intent intent = new Intent(this, Messenger.class);
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
