package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseObject;
import com.parse.ParseQuery;



public class Home extends AppCompatActivity implements View.OnClickListener{

    private Button postButton;
    private Button watchButton;
    private Button campusLocationsButton;
    private Button messengerButton;
    private Button myItemsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        postButton = (Button) findViewById(R.id.postButton_HOME);
        watchButton = (Button) findViewById(R.id.watchButton_HOME);
        campusLocationsButton = (Button) findViewById(R.id.campusLocations_HOME);
        messengerButton = (Button) findViewById(R.id.messenger_HOME);
        myItemsButton = (Button) findViewById(R.id.myItems_HOME);

        postButton.setOnClickListener(this);
        watchButton.setOnClickListener(this);
        campusLocationsButton.setOnClickListener(this);
        messengerButton.setOnClickListener(this);
        myItemsButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.postButton_HOME:
                postClicked(v);
                break;
            case R.id.watchButton_HOME:
                watchClicked(v);
                break;
            case R.id.campusLocations_HOME:
                usfCampusClicked(v);
                break;
            case R.id.messenger_HOME:
                messengerClicked(v);
                break;
            case R.id.myItems_HOME:
                myItemsClicked(v);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void postClicked(View view){
        Intent intent = new Intent(this, PostItem.class);
        startActivity(intent);
    }

    public void watchClicked(View view){
        Intent intent = new Intent(this, Watch.class);
        startActivity(intent);
    }

    public void usfCampusClicked(View view){
        /*Need to make new activity that contains map. I
        think I need to go through the process again.*/
        Intent intent = new Intent(this, USFLocations.class);
        startActivity(intent);
    }

    public void messengerClicked(View view){
        Intent intent = new Intent(this, Messenger.class);
        startActivity(intent);
    }

    public void myItemsClicked(View view){
        Intent intent = new Intent(this, MyItems.class);
        startActivity(intent);
    }
}
