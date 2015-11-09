package com.mycompany.bullbuy;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_left, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.youHeader = (TextView) convertView.findViewById(R.id.txtSender);
            holder.themHeader = (TextView) convertView.findViewById(R.id.txtReceiver);
            holder.body = (TextView) convertView.findViewById(R.id.txtMessage);
            convertView.setTag(holder);
        }

        final Message message = (Message)getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final boolean isMe = message.getSenderId().equals(currentUserId);
        final boolean isThem = message.getRecipiendId().equals(recipientUn);

        //show message as "You" or "Them" based on current user
        if (isMe) {
            holder.youHeader.setVisibility(View.VISIBLE);
            holder.themHeader.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }
        else if (isThem) {
            holder.youHeader.setVisibility(View.GONE);
            holder.themHeader.setVisibility(View.VISIBLE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        }
        holder.body.setText(message.getBody());
        return convertView;
    }

    final class ViewHolder {
        public TextView youHeader;
        public TextView themHeader;
        public TextView body;
    }
}
