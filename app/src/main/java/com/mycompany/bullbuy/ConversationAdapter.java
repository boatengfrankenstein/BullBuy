package com.mycompany.bullbuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by Dane on 11/9/2015.
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

    public ConversationAdapter(Context context, List<Conversation> conversations) {
        super(context, 0, conversations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.itemTitle = (TextView) convertView.findViewById(R.id.conversationHeader);
            holder.dateCreated = (TextView) convertView.findViewById(R.id.dateConversationCreated);
            convertView.setTag(holder);
        }
        final Conversation conversation = (Conversation) getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        //get date & time of conversation's creation
        Date date = conversation.getCreatedAt();

        //assign conversation item views
        holder.itemTitle.setText(conversation.getItemTitle());
        holder.dateCreated.setText((date.toString()));


        return convertView;
    }

    final class ViewHolder {
        public TextView itemTitle;
        public TextView dateCreated;
    }
}
