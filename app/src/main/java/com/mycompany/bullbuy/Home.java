package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class Home extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button postButton;
    private Button watchButton;
    private Button campusLocationsButton;
    private Button messengerButton;
    private Button myItemsButton;
    private static EditText searchBar;
    private Button searchButton;
    private Button clearButton;
    private ListView listItems;
    private myAdapter customAdapter;
    private myAdapter searchAdapter;

    public static String getSearchBar(){
        return searchBar.getText().toString();
    }

    public final static String MESSAGE = "com.mycompany.bullbuy.Home.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        postButton = (Button) findViewById(R.id.postButton_HOME);
        watchButton = (Button) findViewById(R.id.watchButton_HOME);
        campusLocationsButton = (Button) findViewById(R.id.campusLocations_HOME);
        messengerButton = (Button) findViewById(R.id.messenger_HOME);
        myItemsButton = (Button) findViewById(R.id.myItems_HOME);
        searchBar = (EditText) findViewById(R.id.searchText_Home);
        searchButton = (Button) findViewById(R.id.searchButton_Home);
        clearButton = (Button) findViewById(R.id.clearButton_Home);
        listItems = (ListView) findViewById(R.id.list_Home);

        postButton.setOnClickListener(this);
        watchButton.setOnClickListener(this);
        campusLocationsButton.setOnClickListener(this);
        messengerButton.setOnClickListener(this);
        myItemsButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        customAdapter = new myAdapter(this, 2);
        listItems.setAdapter(customAdapter);
        customAdapter.loadObjects();
        listItems.setOnItemClickListener(this);

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
            case R.id.searchButton_Home:
                searchButtonClicked(v);
                break;
            case R.id.clearButton_Home:
                clearButtonClicked(v);
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

    private void postClicked(View view){
        Intent intent = new Intent(this, PostItem.class);
        startActivity(intent);
    }

    private void watchClicked(View view){
        Intent intent = new Intent(this, Watch.class);
        startActivity(intent);
    }

    private void usfCampusClicked(View view){
        /*Need to make new activity that contains map. I
        think I need to go through the process again.*/
        Intent intent = new Intent(this, USFLocations.class);
        startActivity(intent);
    }

    private void messengerClicked(View view){
        Intent intent = new Intent(this, Messenger.class);
        startActivity(intent);
    }

    private void myItemsClicked(View view){
        Intent intent = new Intent(this, MyItems.class);
        startActivity(intent);
    }

    private void searchButtonClicked(View view){
        if(searchBar.getText().toString().equals("")){
            Toast.makeText(this, "No search criteria entered", Toast.LENGTH_SHORT).show();
        }

        else{
            searchAdapter = new myAdapter(this, 3);
            listItems.setAdapter(searchAdapter);
            searchAdapter.loadObjects();
        }


    }

    private void clearButtonClicked(View view){
        if(listItems.getAdapter() == searchAdapter){
            listItems.setAdapter(customAdapter);
            //listItems.loadObjects(); to have latest info. don't think i would need
        }
        searchBar.setText("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // not an optimal solution because i have to query table for object again next activity. might need to edit, ok for now.
        // maybe could just blow up view item or something idl might just leave it how it s bc time
        Intent intent = new Intent(this, ViewItem.class);
        String postObjectID = customAdapter.getItem(position).getObjectId();

        intent.putExtra(MESSAGE, postObjectID);
        startActivity(intent);
    }
}
