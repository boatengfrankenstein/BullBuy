package com.mycompany.bullbuy;

import com.parse.Parse;

/**
 * Created by David on 10/17/2015.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.initialize(this, "ccYKDfZye5qoPhwpxQuulIl0lUfaoStwqdkDOkrq", "Bml4HLucg8nCri8z5oSQx5gy3mNzyxGtoEEqO7Ka");
    }
}
