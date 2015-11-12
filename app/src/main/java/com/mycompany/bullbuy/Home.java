package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.parse.ParseUser;

public class Home extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    // declare buttons, edit texts, listviews, myadapters, and spinner used in activity
    private static EditText searchBar;
    private Button searchButton;
    private Button clearButton;
    private ListView listItems;
    private myAdapter customAdapter;
    private myAdapter searchAdapter;

    private Spinner spinner;

    // accessed by myAdapter to query the table for what the user is searching
    public static String getSearchBar(){
        return searchBar.getText().toString().toLowerCase();
    }

    // message passed with intent
    public final static String MESSAGE = "com.mycompany.bullbuy.Home.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchBar = (EditText) findViewById(R.id.searchText_Home);
        searchButton = (Button) findViewById(R.id.searchButton_Home);
        clearButton = (Button) findViewById(R.id.clearButton_Home);
        listItems = (ListView) findViewById(R.id.list_Home);

        searchButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        // populate spinner with string array in strings file, spinner used as menu
        spinner = (Spinner) findViewById(R.id.spinner_Home);
        ArrayAdapter<CharSequence> menuAdapter = ArrayAdapter.createFromResource(this, R.array.app_menu, android.R.layout.simple_spinner_item);
        menuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(menuAdapter);
        spinner.setOnItemSelectedListener(this);

        /*set adapter used to load postobjects from table to the listview and load
         *  more on this in myAdapter.java
         */
        customAdapter = new myAdapter(this, 2);
        listItems.setAdapter(customAdapter);
        customAdapter.loadObjects();
        listItems.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.searchButton_Home:
                searchButtonClicked(v);
                break;
            case R.id.clearButton_Home:
                clearButtonClicked(v);
                break;
        }
    }

    private void searchButtonClicked(View view){
        if(searchBar.getText().toString().equals("")){
            Toast.makeText(this, "No search criteria entered", Toast.LENGTH_SHORT).show();
        }
        /* search adapter used to query the table for postobjects that match the search criteria
         * set the adapter to the searchadapter and load
         */
        else{
            searchAdapter = new myAdapter(this, 3);
            listItems.setAdapter(searchAdapter);
            searchAdapter.loadObjects();
        }
    }

    private void clearButtonClicked(View view){
        // clear search criteria and view all postobjects
        if(listItems.getAdapter() == searchAdapter){
            listItems.setAdapter(customAdapter);
        }
        searchBar.setText("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        /* If a post is clicked, go to view item activity.
         *  Pass objectid to be able to query in next activity
         *  Check current adapter to ensure correct id is passed
         */

        Intent intent = new Intent(this, ViewItem.class);
        String postObjectID;
        if (listItems.getAdapter() == searchAdapter){
            postObjectID = searchAdapter.getItem(position).getObjectId();
        }
        else{
            postObjectID = customAdapter.getItem(position).getObjectId();
        }

        intent.putExtra(MESSAGE, postObjectID);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Class selection = null;
        switch(parent.getSelectedItem().toString()){
            case "Home":
                return;
            case "Sell Item":
                selection = PostItem.class;
                break;
            case "Watch List":
                selection = Watch.class;
                break;
            case "Campus Locations":
                selection = USFLocations.class;
                break;
            case "Messaging":
                selection = Conversations.class;
                break;
            case "My Items":
                selection = MyItems.class;
                break;
            case "Log Out":
                ParseUser.logOut();
                selection = Login.class;
                break;
            default:
                return;
        }
        Intent intent = new Intent(this, selection);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
