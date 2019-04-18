package com.parzival48.jointy;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpUI extends AppCompatActivity {

    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();

    EditText txtUsername,txtPassword,txtLineid,txtConfirm;
    User newProfile;
    boolean exist = false;

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
                String checkValid = validU(newProfile,confirm);
                if(!checkValid.equals("")){
                    Snackbar.make(v, checkValid, Snackbar.LENGTH_LONG)
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


    // Input Validation

    private String validU(User u,String confirm){
        boolean usr = false,
                psw = false,
                line = false;

        if(u.getUsername().length()>3 && u.getUsername().length()<21){
            if(u.getUsername().matches("[A-Za-z0-9]+")){
                final String username = u.getUsername();
                jointyDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        exist = dataSnapshot.child("userdata").child(username).exists();
                        exist = dataSnapshot.child("userdata").child(username).exists();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                usr = !(exist);
            }
        }

        if(u.getPassword().equals(confirm)){
            if(u.getPassword().length()>3 && u.getPassword().length()<21){
                psw = (u.getPassword().matches("[A-Za-z0-9#$!_]+"));

            }
        }

        if(u.getLineid().length()>1 && u.getLineid().length()<31){
            line = (u.getLineid().matches("[A-Za-z0-9,_,-]+"));
        }

        if(usr){
            if(psw){
                if(line){
                    return "";
                }
                else return "Please Re-Check Your LINE ID";
            }
            else return "Please Re-Check Your Password";
        }
        else return "Please Re-Check Your Username";

    }



}
