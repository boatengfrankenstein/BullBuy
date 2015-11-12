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

    // decalre imageview, edittexts, and button
    private ImageView photoImageView;
    private EditText itemTitle;
    private EditText itemPrice;
    private EditText itemDescription;
    private Button postButton;

    // declare variables
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

    private void photoClicked(View view){
        // can't take photo if device does not have camera
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            displayPopup("The device has no camera.");
        }
        else {
            /* go to camera. create a file in external storage,
             * designate that file to be storage location for the picture taken
             * onactivityresult invoked after this method (when camera returns)
             */
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
        /* If picture was taken and everything was successful,
         * decode the file (path was saved to a string in photoclicked method before starting cameractivity
         * display resulting bitmap
         * compress the bitmap to avoid overhead of large photo file
         * resulting bytearrayoutputstream converted to bytearray,
         * which is then used to make a parsefile and save it to cloud
         */
        if(reqCode == TAKE_PIC && resCode == RESULT_OK){
            img = BitmapFactory.decodeFile(picPath);
            photoImageView.setImageBitmap(img);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(img != null) {
                img.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                photoData = stream.toByteArray();
            }

            file = new ParseFile("post.JPEG",photoData);
            file.saveInBackground();
        }
    }

    private void postClicked(){
        /* Ensure photo, title, description, and price are all supplied by user and
         * meet minimum requirements (character length, format)
        */
        if(file == null) {
            displayPopup("Photo is required");
        }
        else if(itemTitle.getText().toString().length() < 4){
            displayPopup("Item title has to be at least 4 characters");
        }
        else if(!(itemPrice.getText().toString().matches("\\d+\\.\\d\\d"))){
            displayPopup("Please insert a valid currency format for price.");
        }
        else if (itemDescription.getText().toString().length() <= 9) {
            displayPopup("Item description must be at least 10 characters");
        }
        /* make a new postobject, fill the "row"/postobject with corresponding values
         * associate the parsefile saved earlier with the current postobject
         * save the postobject, display apporpriate message
         */
        else if(file != null) {
            ParseObject postObject = new ParseObject("PostObject");
            postObject.put("Title", itemTitle.getText().toString());
            postObject.put("Title_Search", itemTitle.getText().toString().toLowerCase() + " "
                    + itemDescription.getText().toString().toLowerCase());
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

    // used this in a few java files where multiple toasts showed to user to avoid Toast.... each time
    private void displayPopup(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
