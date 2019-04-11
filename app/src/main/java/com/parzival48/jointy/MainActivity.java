package com.parzival48.jointy;

import android.content.Intent;
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



public class MainActivity extends AppCompatActivity {

    EditText txtusername,txtpassword;
    String username,password,serverPass,serverLine;
    boolean userExist;
    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configSignUpButton();

        txtusername = (EditText)findViewById(R.id.txtUsername);
        txtpassword = (EditText)findViewById(R.id.txtPassword);

        Button signin = (Button)findViewById(R.id.btSignin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Loading ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                username = txtusername.getText().toString().trim();
                password = txtpassword.getText().toString().trim();
                jointyDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userExist = dataSnapshot.child("userdata").child(username).exists();
                        try{
                            serverPass = dataSnapshot.child("userdata").child(username).child("password").getValue().toString();
                            serverLine = dataSnapshot.child("userdata").child(username).child("lineid").getValue().toString();
                            ActiveStatus.username = username;
                            ActiveStatus.lineid = serverLine;

                        }
                        catch (Exception e){
                            serverPass = " ";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(userExist && match(password,serverPass)){
                    Intent i = new Intent(MainActivity.this,FeedUI.class);
                    i.setFlags(i.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                }
                else{
                    Snackbar.make(v, "Incorrect Username or Password", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void configSignUpButton(){
        Button SignUpButton = (Button) findViewById(R.id.btSignup);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpUI.class));
            }
        });
    }

    private boolean match(String client,String server){
        if(client.equals(server)){
            return true;
        }
        else{
            return false;
        }
    }



}
