package com.parzival48.jointy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

public class SignUpUI extends AppCompatActivity {

    //////////////////////////////////////////////////////////////

    EditText txtUsername,txtPassword,txtLineid;
    Button btCreateAccount;
    User newProfile;

    DatabaseReference userDB;
    //////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_ui);

        configSignInButton();
    }

    private void configSignInButton(){
        Button SignInButton = (Button) findViewById(R.id.btLogin);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpUI.this, MainActivity.class));
            }
        });
    }
}
