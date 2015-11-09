package com.mycompany.bullbuy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by David on 10/27/2015.
 */
public class myAdapter extends ParseQueryAdapter<ParseObject>{

    public myAdapter(Context context, final int queryType) {

        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("PostObject");
                //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                //query.setLimit(10); //Might need to remove
                if(queryType == 0) { // MyItems
                    query.whereEqualTo("User", ParseUser.getCurrentUser().getUsername());
                }
                else if(queryType == 1){ // Watch
                    query.whereContainedIn("objectId", Watch.getIDS());
                }
                else if(queryType == 2){ // Feed
                    query.whereNotEqualTo("User", ParseUser.getCurrentUser().getUsername());
                }
                else if (queryType == 3){
                    String searchCriteria = Home.getSearchBar();
                    query.whereContains("Title_Search", searchCriteria);
                }
                
                query.addDescendingOrder("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject postObject, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.mylistview, null);
        }

        super.getItemView(postObject, v, parent);

        ParseImageView itemPhoto = (ParseImageView) v.findViewById(R.id.itemPhoto_myAdapter);
        ParseFile imageFile = postObject.getParseFile("Photo");
        if (imageFile != null) {
            itemPhoto.setParseFile(imageFile);
            itemPhoto.loadInBackground();
        }

        TextView title = (TextView) v.findViewById(R.id.itemTitle_myAdapter);
        title.setText("Title: " + postObject.getString("Title"));

        TextView description = (TextView) v.findViewById(R.id.itemDescription_myAdapter);
        description.setText("Description: " + postObject.getString("Description"));

        TextView price = (TextView) v.findViewById(R.id.itemPrice_myAdapter);
        price.setText("Price: $" + postObject.getString("Price"));

        return v;
    }
}

//Source: Tutorial - ParseQueryAdapter Tutorial GitHub - Parse