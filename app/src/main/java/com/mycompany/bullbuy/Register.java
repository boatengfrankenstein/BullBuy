package com.mycompany.bullbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Register extends AppCompatActivity implements View.OnClickListener{
    // passing intent to login activity
    public final static String MESSAGE = "com.mycompany.bullbuy.Register.MESSAGE";

    // declare edittexts and button
    private EditText name;
    private EditText email;
    private EditText password;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name_REGISTER);
        email = (EditText) findViewById(R.id.eMail_REGISTER);
        password = (EditText) findViewById(R.id.password_REGISTER);
        registerButton = (Button) findViewById(R.id.registerButton_REGISTER);

        //get email that was entered in registration so user does not have to type it again
        Intent intent = getIntent();
        String _email = intent.getStringExtra(Login.MESSAGE);
        email.setText(_email);
        registerButton.setOnClickListener(this);

        // Log out any current users
        ParseUser.getCurrentUser().logOut();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.registerButton_REGISTER:
                registerClick(v);
        }

    }

    public void registerClick(View view) {
        /* ensure that name, email, and password meet requirements
         * verify the emails usf related, email will be sent there
         * user must verify before able to login
         * make parseuser with checked input, sign up in background
         * if sign up was successful, go to login pass email with intent
         */
        if (name.getText().toString().length() >= 1
            && ((email.getText().toString().endsWith("@mail.usf.edu")
                || (email.getText().toString().endsWith("@cse.usf.edu"))
                ||  (email.getText().toString().endsWith("@usf.edu"))))
            && password.getText().toString().length() > 5){

            ParseUser newUser = new ParseUser();
            newUser.put("name", name.getText().toString());
            newUser.setUsername(email.getText().toString());
            newUser.setEmail(email.getText().toString());
            newUser.setPassword(password.getText().toString());
            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        displayPopup("Registered");

                        Intent intent = new Intent(Register.this, Login.class);
                        String message = email.getText().toString();
                        intent.putExtra(MESSAGE, message);
                        startActivity(intent);

                    }

                    else{
                        displayPopup("Registration Unsuccessful");
                    }
                }
            });


        }

        // display messages corresponding to any requirements not met
        else{
            if(name.getText().toString().length() < 1){
                displayPopup("Enter a name");
            }
            else if(!(email.getText().toString().endsWith("@mail.usf.edu"))
                    && !(email.getText().toString().endsWith("@usf.edu"))
                    && !(email.getText().toString().endsWith("@cse.usf.edu"))){
                displayPopup("Need a @usf.edu or @mail.usf.edu to register");
            }
            else if(password.getText().toString().length() <= 5){
                displayPopup("Password must have at least 6 characters");
            }
        }
    }

    // used this in a few java files where multiple toasts showed to user to avoid Toast.... each time
    private void displayPopup(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
