package com.parzival48.jointy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileUI extends AppCompatActivity {

    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();
    String eventInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_ui);

        getUserInfo();



        //Bottom Navigation Config
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_feed:
                        Intent i1 = new Intent(ProfileUI.this,FeedUI.class);
                        i1.setFlags(i1.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i1);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_create:
                        Intent i2 = new Intent(ProfileUI.this,CreateEventUI.class);
                        i2.setFlags(i2.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i2);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_profile:
                        break;

                }
                return false;
            }
        });
    }

    //Config Press Back Again To Logout
    private long backPressTime;
    private Toast backToast;
    @Override
    public void onBackPressed() {

        if(backPressTime+2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast = Toast.makeText(getBaseContext(),"Press Back Again to Sign Out"
                    ,Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressTime = System.currentTimeMillis();

    }

    //Get User Event List
    private void getUserInfo(){
        jointyDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ActiveStatus.eventList = dataSnapshot.child("userdata").child(ActiveStatus.username).child("eventList").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ActiveStatus.arrayOfEvents = ActiveStatus.eventList.split("x");
        showTextView();

    }

    //Show Status from Active User
    private void showTextView(){
        TextView username = (TextView)findViewById(R.id.profile_user);
        username.setText(ActiveStatus.username);
        TextView lineid = (TextView)findViewById(R.id.profile_line);
        lineid.setText(ActiveStatus.lineid);
        TextView event = (TextView)findViewById(R.id.profile_event);
        event.setText("Loading ...");

        String myEvents = "";
        int num = ActiveStatus.arrayOfEvents.length;
        for(int i=0; i<num; i++){
            myEvents = myEvents+eventDescription(ActiveStatus.arrayOfEvents[i]);
            Toast.makeText(ProfileUI.this,ActiveStatus.arrayOfEvents[i],Toast.LENGTH_LONG).show();
        }

        event.setText(myEvents);
    }

    //Get Event Description
    private String eventDescription(final String code){
        jointyDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventInfo += dataSnapshot.child("eventdata").child("2").child("name").getValue().toString();
                eventInfo += "\n    ";
                eventInfo += dataSnapshot.child("eventdata").child("2").child("loaction").getValue().toString();
                eventInfo += "\n    ";
                eventInfo += dataSnapshot.child("eventdata").child("2").child("date").getValue().toString();
                eventInfo += " - ";
                eventInfo += dataSnapshot.child("eventdata").child("2").child("time").getValue().toString();
                eventInfo += "\n";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return(eventInfo);

    }

}
