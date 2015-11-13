package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    // message to be passed with intent
    public final static String MESSAGE = "com.mycompany.bullbuy.Login.MESSAGE";


    // declare buttons and edit texts used in activity
    private Button loginButton;
    private Button registerButton;
    private EditText eMail;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* If there is a current user logged on and the user has verified email then keep logged on
         *  when log out is selected in menu in home activity the PARSEUSER becomes null.
         */

        if(ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getBoolean("emailVerified")){
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        eMail = (EditText) findViewById(R.id.eMail_LOGIN);
        password = (EditText) findViewById(R.id.password_LOGIN);

        loginButton = (Button) findViewById(R.id.loginButton_LOGIN);
        registerButton = (Button) findViewById(R.id.registerButton_LOGIN);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        //When user clicks register, autofill the email edittext. Smooth transition purposes
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

    public void loginClick(View view) {

        /*Verify in database
        *   Checks password, and whether email was verified
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
        } // Non-usf emails not accepted so don't bother attempting to log them in
        else if(!eMail.getText().toString().endsWith("@mail.usf.edu")//allowing usf.edu and cse.usf.edu for professor to be able to grade (release 1 only)
                && !eMail.getText().toString().endsWith("@usf.edu")
                && !eMail.getText().toString().endsWith("@cse.usf.edu")) {
            displayPopup("Need a USF E-Mail to Use the App.");
        }
        /*Passwords required to be longer larger than 5 characters at registration so
            attempting to login user with less than 4 wouldn't make sense*/
        else if(password.getText().length() <= 5) {
            displayPopup("Incorrect password");
        }
    }

    /*Send user to registration form
    * Populate email field in register activity, if user entered it in the activity
    */

    public void registerClick(View view) {
        Intent intent = new Intent(this, Register.class);
        String message = eMail.getText().toString();

        intent.putExtra(MESSAGE,message);
        startActivity(intent);
    }

    // used this in a few java files where multiple toasts showed to user to avoid Toast.... each time
    private void displayPopup(String message){
        Toast toast = Toast.makeText(Login.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
