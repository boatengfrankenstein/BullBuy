package com.mycompany.bullbuy;

import android.content.Intent;
import android.graphics.Bitmap;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ViewItem extends AppCompatActivity implements View.OnClickListener {

    private String postObjectID;
    private ParseObject _PostObject;

    private ParseImageView itemPhoto;
    private TextView itemTitle;
    private TextView itemPrice;
    private TextView itemDescription;
    private Button messageButton;
    private Button addButton;


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

        Intent intent = getIntent();
        postObjectID = intent.getStringExtra(Home.MESSAGE);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PostObject");

        query.getInBackground(postObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject PostObject, ParseException e) {
                if(e == null) {
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

        addButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);

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
        Intent intent = new Intent(this, Messenger.class);
        startActivity(intent);
    }

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
