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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Watch extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public final static String MESSAGE = "com.mycompany.bullbuy.Watch.MESSAGE";

    private static ArrayList<String> IDs = new ArrayList<String>();
    public static ArrayList<String> getIDS(){
        return IDs;
    }
    private myAdapter customAdapter;
    private ListView watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        watchlist = (ListView) findViewById(R.id.list_Watch);
        try {
            FileInputStream stream = openFileInput("Watchlist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String string = reader.readLine();
            while(string != null){
                IDs.add(string);
                string = reader.readLine();
            }
            reader.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        customAdapter = new myAdapter(this, 1);
        watchlist.setAdapter(customAdapter);
        customAdapter.loadObjects();

        for (String postID : IDs){
            System.out.println(postID);
        }

        watchlist.setOnItemClickListener(this);
        IDs.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_watch, menu);
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

        String postObjectID = customAdapter.getItem(position).getObjectId();

        Intent intent = new Intent(this, ViewWatchItem.class);
        intent.putExtra(MESSAGE,postObjectID);
        startActivity(intent);
    }
}
