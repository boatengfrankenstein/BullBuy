package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class Home extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private static EditText searchBar;
    private Button searchButton;
    private Button clearButton;
    private ListView listItems;
    private myAdapter customAdapter;
    private myAdapter searchAdapter;

    private Spinner spinner;

    public static String getSearchBar(){
        return searchBar.getText().toString().toLowerCase();
    }

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

        spinner = (Spinner) findViewById(R.id.spinner_Home);
        ArrayAdapter<CharSequence> menuAdapter = ArrayAdapter.createFromResource(this, R.array.app_menu, android.R.layout.simple_spinner_item);
        menuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(menuAdapter);
        spinner.setOnItemSelectedListener(this);

        customAdapter = new myAdapter(this, 2);
        listItems.setAdapter(customAdapter);
        customAdapter.loadObjects();
        listItems.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //listItems.removeAllViewsInLayout();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Class selection = null;
        switch(parent.getSelectedItem().toString()){
            case "Home":
                return;
            case "Sell an item":
                selection = PostItem.class;
                break;
            case "Watchlist":
                selection = Watch.class;
                break;
            case "Campus":
                selection = USFLocations.class;
                break;
            case "Messaging":
                selection = Messenger.class;
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
