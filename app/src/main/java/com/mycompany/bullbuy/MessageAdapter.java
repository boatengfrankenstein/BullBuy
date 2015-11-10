package com.mycompany.bullbuy;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Dane on 11/8/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    //class variables
    private String currentUserId;
    private String recipientUn;

    public MessageAdapter(Context context, String currentUserId, String recipientUn, List<Message> messages) {
        super(context, 0, messages);
        this.currentUserId = currentUserId;
        this.recipientUn = recipientUn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = getItem(position);
        final boolean isMe = message.getSenderId().equals(currentUserId);

        if (isMe) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_left, parent, false);
        }
        else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_right, parent, false);
        }
        final ViewHolder holder = new ViewHolder();
        holder.body = (TextView) convertView.findViewById(R.id.txtMessage);

        holder.body.setText(message.getBody());
        return convertView;
    }

    final class ViewHolder {
        public TextView body;
    }
}
