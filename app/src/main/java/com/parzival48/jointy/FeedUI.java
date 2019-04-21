package com.parzival48.jointy;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedUI extends AppCompatActivity {


    DatabaseReference jointyDB = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://jointy-db.firebaseio.com/eventdata");

    DatabaseReference userDB = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://jointy-db.firebaseio.com/userdata");

    private RecyclerView mEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_ui);


        //Bottom Navigation Config
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_feed:
                        break;

                    case R.id.nav_create:
                        Intent i2 = new Intent(FeedUI.this,CreateEventUI.class);
                        i2.setFlags(i2.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i2);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_profile:
                        Intent i3 = new Intent(FeedUI.this,ProfileUI.class);
                        i3.setFlags(i3.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i3);
                        overridePendingTransition(0,0);
                        break;

                }
                return false;
            }
        });

        //feed config
        jointyDB.keepSynced(true);

        mEventList = (RecyclerView)findViewById(R.id.myrecycleview);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Event,EventHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventHolder>
                (Event.class,R.layout.row_event,EventHolder.class,jointyDB) {
            @Override
            protected void populateViewHolder(EventHolder viewHolder, Event model, int position) {
                getUserInfo();
                if(!(model.getHost().equals("Admin"))){
                    viewHolder.setName(model.getName());
                    viewHolder.setLocation(model.getLoaction());
                    viewHolder.setDateTime(model.getDate()+" - "+model.getTime());
                    viewHolder.setHost("Host: "+model.getHost());
                    viewHolder.setDescription(model.getDescription());
                    viewHolder.setContact("LINE ID: "+model.getContact());
                    viewHolder.setColor(model.getCode());

                    boolean isJoined = haveJoined(model.getCode());

                    if(!model.getHost().equals(ActiveStatus.username) && !isJoined){
                        viewHolder.configClickCard(model);
                    }
                }
            }
        };
        mEventList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EventHolder extends RecyclerView.ViewHolder {
        View mView;

        public EventHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView t = (TextView)mView.findViewById(R.id.ename);
            t.setText(name);
        }


        public void setColor(String code){
            int i = Integer.valueOf(code)%4;
            int[] color = {R.color.card1,R.color.card2,R.color.card3,R.color.card4};
            LinearLayout card = (LinearLayout)mView.findViewById(R.id.cardView);
            card.setBackgroundResource(color[i]);
        }


        public void setLocation(String location){
            TextView t = (TextView)mView.findViewById(R.id.elocation);
            t.setText(location);
        }

        public void setDateTime(String datetime){
            TextView t = (TextView)mView.findViewById(R.id.edate);
            t.setText(datetime);
        }

        public void setHost(String host){
            TextView t = (TextView)mView.findViewById(R.id.ehost);
            t.setText(host);
        }

        public void setDescription(String Description){
            TextView t = (TextView)mView.findViewById(R.id.edescription);
            t.setText(Description);
        }

        public void setContact(String contact){
            TextView t = (TextView)mView.findViewById(R.id.econtact);
            t.setText(contact);
        }

        public void configClickCard(final Event model){
            CardView holder = (CardView)mView.findViewById(R.id.parent_layout);
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ActiveStatus.tempCode.equals(model.getCode()) && ActiveStatus.arrayOfEvents.length<4){
                        DatabaseReference jointyDB;
                        jointyDB = FirebaseDatabase.getInstance().getReference();
                        ActiveStatus.eventList = ActiveStatus.eventList+model.getCode()+"x";
                        jointyDB.child("userdata").child(ActiveStatus.username).child("eventList").setValue(ActiveStatus.eventList);
                        ActiveStatus.tempCode = model.getCode();
                        Snackbar.make(v, "Joined !", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }

            });

        }

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

    private boolean haveJoined(String code){
        int num = ActiveStatus.arrayOfEvents.length;
        for(int i=0; i<num; i++){
            if(code.equals(ActiveStatus.arrayOfEvents[i])){
                return true;
            }
        }
        return false;
    }

    private void getUserInfo(){
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ActiveStatus.eventList = dataSnapshot.child(ActiveStatus.username).child("eventList").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ActiveStatus.arrayOfEvents = ActiveStatus.eventList.split("x");
    }

}
