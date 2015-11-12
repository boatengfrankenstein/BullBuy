package com.mycompany.bullbuy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/* myAdapter extends ParseQueryAdapter<ParseObject>
 * this class contains methods used to query and adapt results to our desired view
 */
public class myAdapter extends ParseQueryAdapter<ParseObject>{

    public myAdapter(Context context, final int queryType) {

        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("PostObject");
                // MyItems - get postobjects posted by the user
                if(queryType == 0) {
                    query.whereEqualTo("User", ParseUser.getCurrentUser().getUsername());
                }
                /* Watch - get postobjects that the user added to watchlist
                 *  when user adds to watch list, id of that postobject is stored in internal file
                 *  watch.getids holds those ids
                 */
                else if(queryType == 1){
                    query.whereContainedIn("objectId", Watch.getIDS());
                }
                // Home - get postobjects that the user did not post
                else if(queryType == 2){
                    query.whereNotEqualTo("User", ParseUser.getCurrentUser().getUsername());
                }
                /* search - get post objects where user search is contained
                 * in title or description of a postobject
                 */
                else if (queryType == 3){
                    String searchCriteria = Home.getSearchBar();
                    query.whereContains("Title_Search", searchCriteria);
                }

                // organize postobjects most recent to least recent
                query.addDescendingOrder("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject postObject, View v, ViewGroup parent) {
        /* Display the details of the postobject
         * - Associate postoject photo with ParseImageView (load)
         * - Post title, description, and price are also added to the item view
         * This is how each individual item in the list will be displayed
         */

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
        title.setText(postObject.getString("Title"));

        TextView price = (TextView) v.findViewById(R.id.itemPrice_myAdapter);
        price.setText("$" + postObject.getString("Price"));

        TextView description = (TextView) v.findViewById(R.id.itemDescription_myAdapter);
        description.setText(postObject.getString("Description"));

        return v;
    }
}