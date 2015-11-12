package com.mycompany.bullbuy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ViewMyItem extends AppCompatActivity implements View.OnClickListener {

    //declare variables, edittexts, buttons, and parsefile
    private EditText itemTitle;
    private EditText itemPrice;
    private EditText itemDescription;
    private String initialTitle;
    private String initialPrice;
    private String initialDescription;
    private ParseObject _PostObject;
    private String postObjectID;
    private  ParseImageView itemPhoto;
    private Button updateButton;
    private  Button deleteButton;
    private File photoFile = null;
    private String photoPath;
    private Bitmap photoBitmap;
    private byte[] photoBytes;
    private ParseFile parsefile;
    private boolean photoUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_item);

        itemTitle = (EditText) findViewById(R.id.title_ViewMyItem);
        itemPrice = (EditText) findViewById(R.id.price_ViewMyItem);
        itemDescription = (EditText) findViewById(R.id.description_ViewMyItem);
        updateButton = (Button) findViewById(R.id.updateButton_ViewMyItem);
        deleteButton = (Button) findViewById(R.id.deleteButton_ViewMyItem);

        // get id to know which postobject was selected in home activity to query table for that postobject
        Intent intent = getIntent();
        postObjectID = intent.getStringExtra(MyItems.MESSAGE);

        itemPhoto = (ParseImageView) findViewById(R.id.photo_ViewMyItem);

        // get the parsefile of the postobject and postobject itself and load on screen
        ParseQuery<ParseObject> query = new ParseQuery<>("PostObject");
        query.getInBackground(postObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject PostObject, ParseException e) {
                if(e == null){
                    _PostObject = PostObject;

                    ParseFile imageFile = PostObject.getParseFile("Photo");
                    if(imageFile != null) {
                        itemPhoto.setParseFile(imageFile);
                        itemPhoto.loadInBackground();
                    }

                    itemTitle.setText(PostObject.get("Title").toString());
                    initialTitle = itemTitle.getText().toString();

                    itemPrice.setText(PostObject.get("Price").toString()); //CHECK
                    initialPrice = itemPrice.getText().toString();

                    itemDescription.setText(PostObject.get("Description").toString());
                    initialDescription = itemDescription.getText().toString();
                }
            }
        });

        updateButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        itemPhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updateButton_ViewMyItem:
                updateClicked(v);
                break;
            case R.id.deleteButton_ViewMyItem:
                    deleteClicked(v);
                    break;
            case R.id.photo_ViewMyItem:
                photoClicked(v);
                break;
        }
    }

    public void updateClicked(View v){
        //update the post if an update was actually made, update is not attempted if nothing changes
        if(!photoUpdated &&
                initialTitle.equals(itemTitle.getText().toString())&&
                initialPrice.equals(itemPrice.getText().toString()) &&
                initialDescription.equals(itemDescription.getText().toString())){

            Toast.makeText(this, "No Update Performed", Toast.LENGTH_SHORT).show();

        }

        // need to check fields meet minimum requirements before updating like in postitem i think
        else if(itemTitle.getText().toString().length() < 4) {
            Toast.makeText(this, "Item title has to be at least 4 characters", Toast.LENGTH_SHORT).show();
        }
        else if (!(itemPrice.getText().toString().matches("\\d+\\.\\d\\d"))){
            Toast.makeText(this, "Please enter a valid currency format for price(ex: 0.00)", Toast.LENGTH_SHORT).show();
        }
        else if (itemDescription.getText().toString().length() <= 9) {
            Toast.makeText(this, "Item description must be at least 10 characters", Toast.LENGTH_SHORT).show();
        }

        else{

            //only modified fields are updated
            if(photoUpdated){
                _PostObject.put("Photo", parsefile);
            }

            if(!initialTitle.equals(itemTitle.getText().toString())) {
                _PostObject.put("Title", itemTitle.getText().toString());
            }
            if(!initialPrice.equals(itemPrice.getText().toString())) {
                _PostObject.put("Price", itemPrice.getText().toString());
            }
            if(!initialDescription.equals(itemDescription.getText().toString())) {
                _PostObject.put("Description", itemDescription.getText().toString());
            }

            _PostObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ViewMyItem.this, "Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewMyItem.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Go to the MyItems activity
            Intent intent = new Intent(this, MyItems.class);
            startActivity(intent);
        }
    }

    public void deleteClicked(View v){
        /* Delete the postobject from the table
         * Go to myitems
         */
        _PostObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ViewMyItem.this, "Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewMyItem.this, MyItems.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ViewMyItem.this, "Could not delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void photoClicked(View v){
        // can't take photo if device does not have camera
        photoUpdated = true; // note photo was updated
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Toast.makeText(this, "The device has no camera.", Toast.LENGTH_SHORT).show();
        }
        else {
             /* go to camera. create a file in external storage,
             * designate that file to be storage location for the picture taken
             * onactivityresult invoked after this method (when camera returns)
             */
            Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePic.resolveActivity(getPackageManager()) != null) {
                try{
                    photoFile = File.createTempFile("JPEG1", ".JPEG", Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES));
                    photoPath = photoFile.getAbsolutePath();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(photoFile != null){
                    takePic.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                startActivityForResult(takePic, 1);
            }
        }

    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent photo){
        /* If picture was taken and everything was successful,
         * decode the file (path was saved to a string in photoclicked method before starting cameractivity
         * display resulting bitmap
         * compress the bitmap to avoid overhead of large photo file
         * resulting bytearrayoutputstream converted to bytearray,
         * which is then used to make a parsefile and save it to cloud
         */
        if(reqCode == 1 && resCode == RESULT_OK){
            photoBitmap = BitmapFactory.decodeFile(photoPath);
            itemPhoto.setImageBitmap(photoBitmap);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(photoBitmap != null) {
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                photoBytes = stream.toByteArray();
            }
            parsefile = new ParseFile("post.JPEG",photoBytes);
            parsefile.saveInBackground(); // upload file to parse
        }
    }
}
