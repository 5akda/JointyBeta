package com.parzival48.jointy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReferenceFromUrl("https://jointy-db.firebaseio.com/eventdata");
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

                viewHolder.setName(model.getName());
                viewHolder.setLocation(model.getLoaction());
                viewHolder.setDateTime(model.getDate()+" - "+model.getTime());
                viewHolder.setHost(model.getHost());
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
            TextView t_name = (TextView)mView.findViewById(R.id.ename);
            t_name.setText(name);
        }
        public void setLocation(String location){
            TextView t_name = (TextView)mView.findViewById(R.id.elocation);
            t_name.setText(location);
        }
        public void setDateTime(String datetime){
            TextView t_name = (TextView)mView.findViewById(R.id.edate);
            t_name.setText(datetime);
        }
        public void setHost(String host){
            TextView t_name = (TextView)mView.findViewById(R.id.ehost);
            t_name.setText(host);
        }
    }

}
