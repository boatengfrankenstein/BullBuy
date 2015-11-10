package com.mycompany.bullbuy;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Dane on 11/8/2015.
 */

@ParseClassName("conversation")
public class Conversation extends ParseObject {
    public String getItemTitle() {
        return getString("itemTitle");
    }

    public String getSellerUn() {
        return getString("sellerUn");
    }

    public String getBuyerUn() {
        return getString("buyerUn");
    }

    public String getParticipants() {
        return getString("participants");
    }

    public void setItemTitle(String itemTitle) {
        put("itemTitle", itemTitle);
    }

    public void setSellerUn(String sellerUn) {
        put("sellerUn", sellerUn);
    }

    public void setBuyerUn(String buyerUn) {
        put("buyerUn", buyerUn);
    }

    public void setParticipants(String participants) {
        put("participants", participants);
    }
}
