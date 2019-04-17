package com.parzival48.jointy;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpUI extends AppCompatActivity {

    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();

    EditText txtUsername,txtPassword,txtLineid,txtConfirm;
    User newProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_ui);

        configSignInButton();

        txtUsername = (EditText)findViewById(R.id.txtUsernameR);
        txtPassword = (EditText)findViewById(R.id.txtPasswordR);
        txtLineid = (EditText)findViewById(R.id.txtLineR);
        txtConfirm = (EditText)findViewById(R.id.txtConfirmR);
        newProfile = new User();

        Button CreateButton = (Button) findViewById(R.id.btCreateAccount);
        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Loading ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                newProfile.setUsername(txtUsername.getText().toString().trim());
                newProfile.setPassword(txtPassword.getText().toString().trim());
                newProfile.setLineid(txtLineid.getText().toString().trim());
                newProfile.setEventList("");

                String confirm = txtConfirm.getText().toString().trim();
                String checkValid = valid(newProfile,confirm);
                if(!checkValid.equals("")){
                    Snackbar.make(v, "Please Re-Check "+checkValid, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    jointyDB.child("userdata").child(newProfile.getUsername()).setValue(newProfile);
                    Snackbar.make(v, "Create Profile Successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }



    private void configSignInButton(){
        Button SignInButton = (Button) findViewById(R.id.btLogin);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String valid(User u,String confirm){
        String usr = "Username ",
                psw = "Password ",
                line = "LINE ID";

        if(u.getUsername().length()>3 && u.getUsername().length()<21){
            if(u.getUsername().matches("[A-Za-z0-9]+")){
                char ch = u.getUsername().charAt(0);
                if((ch>='A' && ch<='Z') || (ch>='a' && ch<='z')){
                    usr = "";
                }
            }
        }

        if(u.getPassword().equals(confirm)){
            if(u.getPassword().length()>3 && u.getPassword().length()<21){
                if(u.getPassword().matches("[A-Za-z0-9#$!_]+")){
                    psw = "";
                }
            }
        }

        if(u.getLineid().length()>1 && u.getLineid().length()<31){
            if(u.getLineid().matches("[A-Za-z0-9,_,-]+")){
                line = "";
            }
        }

        return(usr+psw+line);
    }



}
