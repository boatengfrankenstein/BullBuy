package com.mycompany.bullbuy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    public final static String MESSAGE = "com.mycompany.bullbuy.Login.MESSAGE";


    private Button loginButton;
    private Button registerButton;

    private EditText eMail;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /*Parse.enableLocalDatastore(this);

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();*/

        eMail = (EditText) findViewById(R.id.eMail_LOGIN);
        password = (EditText) findViewById(R.id.password_LOGIN);

        loginButton = (Button) findViewById(R.id.loginButton_LOGIN);
        registerButton = (Button) findViewById(R.id.registerButton_LOGIN);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        Intent intent = getIntent();
        String _email = intent.getStringExtra(Register.MESSAGE);
        eMail.setText(_email);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.loginButton_LOGIN:
                loginClick(v);
                break;
            case R.id.registerButton_LOGIN:
                registerClick(v);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void loginClick(View view) {

        /*Verify in database
        * If verified, go to home
        * Else display not logged in*/
        if((eMail.getText().toString().endsWith("@mail.usf.edu")
                || eMail.getText().toString().endsWith("@usf.edu")
                || eMail.getText().toString().endsWith("@cse.usf.edu"))
            && password.getText().length() > 5) {
            loginButton.setEnabled(false);
            ParseUser.logInInBackground(eMail.getText().toString(), password.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (parseUser != null) {
                        if (!parseUser.getBoolean("emailVerified")) {
                            displayPopup("Verify E-Mail");
                            loginButton.setEnabled(true);
                        } else {
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            loginButton.setEnabled(true);
                        }
                    } else {
                        displayPopup("Not registered.");
                        loginButton.setEnabled(true);
                    }
                }
            });
        }
        else if(!eMail.getText().toString().endsWith("@mail.usf.edu")
                && !eMail.getText().toString().endsWith("@usf.edu")
                && !eMail.getText().toString().endsWith("@cse.usf.edu")) {
            displayPopup("Need a USF E-Mail to Use the App. Please register.");
        }
        else if(password.getText().length() <= 5) {
            displayPopup("Incorrect password");
        }
    }

    /*Send user to registration form
    * Populate email field in register activity, if user entered it in the activity*/

    public void registerClick(View view) {
        Intent intent = new Intent(this, Register.class);
        String message = eMail.getText().toString();

        intent.putExtra(MESSAGE,message);
        startActivity(intent);
    }

    private void displayPopup(String message){
        Toast toast = Toast.makeText(Login.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
