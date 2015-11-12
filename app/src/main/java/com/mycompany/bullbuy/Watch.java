package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Watch extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // message used to pass intent
    public final static String MESSAGE = "com.mycompany.bullbuy.Watch.MESSAGE";

    //declare arraylist (with acccessor function), myAdapter, and listview
    private static ArrayList<String> IDs = new ArrayList<>();
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

        //open the internally stored file, read the postobjectids, and add them to the arraylist
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

        /* customadapter used to query Parse for those postobjects (ids found in internal file are used)
         * load postobjects
        */
        customAdapter = new myAdapter(this, 1);
        watchlist.setAdapter(customAdapter);
        customAdapter.loadObjects();

        watchlist.setOnItemClickListener(this);
        IDs.clear(); // clearids in case user removes so dont query parse for that id nexttime
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // if user selects an item save its postobjectid
        String postObjectID = customAdapter.getItem(position).getObjectId();

        // go to viewwatchitem and pass postobjectd with the intent
        Intent intent = new Intent(this, ViewWatchItem.class);
        intent.putExtra(MESSAGE,postObjectID);
        startActivity(intent);
    }
}
