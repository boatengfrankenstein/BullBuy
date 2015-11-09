package com.mycompany.bullbuy;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Dane on 11/8/2015.
 */

@ParseClassName("message")
public class Message extends ParseObject {
    public String getRecipiendId() {
        return getString("recipientId");
    }

    public String getSenderId() {
        return getString("senderId");
    }

    public String getBody() {
        return getString("body");
    }

    public String getParentConversation() {return getString("parentConversation"); }

    public void setRecipientId(String recipientId) {
        put("recipientId", recipientId);
    }

    public void setSenderId(String senderId) {
        put("senderId", senderId);
    }

    public void setBody(String body) {
        put("body", body);
    }

    public void setParentConversation(String parentConversation) {
        put("parentConversation", parentConversation);
    }
}
