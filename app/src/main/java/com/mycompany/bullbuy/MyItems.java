package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

        //defaultAdapter = new ParseQueryAdapter<ParseObject>(this, "PostObject");
        //defaultAdapter.setTextKey("Title");
        //defaultAdapter.setImageKey("Photo");

        customAdapter = new myAdapter(this, 0);

        feed = (ListView) findViewById(R.id.list_MyItems);
        feed.setAdapter(customAdapter);
        customAdapter.loadObjects();
        //feed.setAdapter(defaultAdapter);
        //defaultAdapter.loadObjects();

        feed.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_items, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "HELLO", Toast.LENGTH_SHORT).show();

        // not an optimal solution because i have to query table for object again next activity. might need to edit, ok for now.
        Intent intent = new Intent(this, ViewMyItem.class);
        String postObjectID = customAdapter.getItem(position).getObjectId();

        intent.putExtra(MESSAGE,postObjectID);
        startActivity(intent);
    }

}
