package com.mycompany.bullbuy;

import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class PostItem extends AppCompatActivity implements View.OnClickListener {

    static final int TAKE_PIC = 1;

    private ImageView photoImageView;
    private EditText itemTitle;
    private EditText itemPrice;
    private EditText itemDescription;
    private Button postButton;

    private String picPath;
    File pictureFile = null;
    private byte[] photoData;
    private Bitmap img;
    private ParseFile file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        photoImageView = (ImageView) findViewById(R.id.photo_PostItem);
        itemTitle = (EditText) findViewById(R.id.itemTitle_PostItem);
        itemPrice = (EditText) findViewById(R.id.itemPrice_PostItem);
        itemDescription = (EditText) findViewById(R.id.itemDescription_PostItem);
        postButton = (Button) findViewById(R.id.postButton_PostItem);

        photoImageView.setOnClickListener(this);
        postButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photo_PostItem:
                photoClicked(v);
                break;
            case R.id.postButton_PostItem:
                postClicked();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_item, menu);
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

    private void photoClicked(View view){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            displayPopup("The device has no camera.");
        }
        else {
            Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePic.resolveActivity(getPackageManager()) != null) {
                try{
                    pictureFile = File.createTempFile("JPEG", ".JPEG", Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES));
                    picPath = pictureFile.getAbsolutePath();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(pictureFile != null){
                    takePic.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
                }
                startActivityForResult(takePic, TAKE_PIC);
            }
        }

    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent photo){
        if(reqCode == TAKE_PIC && resCode == RESULT_OK){
            //Bundle getPic = photo.getExtras();
            //img = (Bitmap) getPic.get("data");
            img = BitmapFactory.decodeFile(picPath);
            photoImageView.setImageBitmap(img);
            // get bytes. unless i decide to save image to a file instead. i probably should.
            // declared byte[] data up top
            // see photoclicked method
            //upload file to parse
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(img != null) {
                img.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                photoData = stream.toByteArray();
            }
            file = new ParseFile("post.JPEG",photoData);
            file.saveInBackground();
        }
    }

    private void postClicked(){
        /*Need to save photo first I believe. Still need to
        decide whether I want to save photo as file
        first or just do bitmap (thumbnail)*/
        if(file == null) {
            displayPopup("Photo is required");
        }
        else if(itemTitle.getText().toString().length() <= 4){
            displayPopup("Item title has to be at least 4 characters");
        }
        else if(!(itemPrice.getText().toString().matches("\\d+\\.\\d\\d"))){
            displayPopup("Please insert a valid currency format for price.");
        }
        else if (itemDescription.getText().toString().length() <= 9) {
            displayPopup("Item description must be at least 10 characters");
        }
        //save photo to parse
        else if(file != null) {
            ParseObject postObject = new ParseObject("PostObject");
            postObject.put("Title", itemTitle.getText().toString());
            postObject.put("Price", itemPrice.getText().toString());
            postObject.put("Description", itemDescription.getText().toString());
            postObject.put("Photo", file);
            postObject.put("User", ParseUser.getCurrentUser().getUsername());
            postObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        displayPopup("Posted");
                    } else {
                        displayPopup("Post Failed. Try Again.");
                    }
                }
            });
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }

        else {
            displayPopup("Error");
        }
    }

    private void displayPopup(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
