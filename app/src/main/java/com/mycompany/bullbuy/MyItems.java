package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

public class MyItems extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ParseQueryAdapter<ParseObject> defaultAdapter;
    private ListView feed;

    public final static String MESSAGE = "com.mycompany.bullbuy.MyItems.MESSAGE";

    private myAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        customAdapter = new myAdapter(this, 0);

        feed = (ListView) findViewById(R.id.list_MyItems);
        feed.setAdapter(customAdapter);
        customAdapter.loadObjects();
        //feed.setAdapter(defaultAdapter);
        //defaultAdapter.loadObjects();

        feed.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // not an optimal solution because i have to query table for object again next activity. might need to edit, ok for now.
        Intent intent = new Intent(this, ViewMyItem.class);
        String postObjectID = customAdapter.getItem(position).getObjectId();

        intent.putExtra(MESSAGE,postObjectID);
        startActivity(intent);
    }

}
