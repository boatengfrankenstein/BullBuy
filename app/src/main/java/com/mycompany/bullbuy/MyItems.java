package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MyItems extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // message id to be passed with intent
    public final static String MESSAGE = "com.mycompany.bullbuy.MyItems.MESSAGE";

    //declare listview and myadapter
    private ListView feed;
    private myAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        feed = (ListView) findViewById(R.id.list_MyItems);

        /* custom adapter used to fetch data and populate list,
         * 0 indicates to get postobjects posted by user
         */
        customAdapter = new myAdapter(this, 0);
        feed.setAdapter(customAdapter);
        customAdapter.loadObjects();
        feed.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Go to viewmyitem if a post is clicked, pass objectid of selected post to that activity
        Intent intent = new Intent(this, ViewMyItem.class);
        String postObjectID = customAdapter.getItem(position).getObjectId();

        intent.putExtra(MESSAGE,postObjectID);
        startActivity(intent);
    }

}
