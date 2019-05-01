package com.parzival48.jointy;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


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

                    case R.id.nav_search:
                        Intent i3 = new Intent(FeedUI.this,SearchLineUI.class);
                        i3.setFlags(i3.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i3);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_profile:
                        Intent i4 = new Intent(FeedUI.this,ProfileUI.class);
                        i4.setFlags(i4.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i4);
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
                    viewHolder.setDateTime(convertDate(model.getDate())+" - "+model.getTime());
                    viewHolder.setHost("Host: "+model.getHost());
                    viewHolder.setDescription(model.getDescription());
                    viewHolder.setContact("LINE ID: "+model.getContact());
                    viewHolder.setMember("With: "+model.getParticipant());
                    viewHolder.setColor(model.getCategory());

                    boolean isJoined = haveJoined(model.getCode());


                    if(!model.getHost().equals(ActiveStatus.username) && !isJoined ){
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

        public void setColor(String category){
            int i = Integer.valueOf(category);
            int[] color = {R.color.cPink,
                            R.color.cOrange,
                            R.color.cBlue,
                            R.color.cGreen,
                            R.color.cViolet,
                            R.color.cBrown};
            int[] pics = {R.drawable.type0,
                    R.drawable.type1,
                    R.drawable.type2,
                    R.drawable.type3,
                    R.drawable.type4,
                    R.drawable.type5};
            LinearLayout card = (LinearLayout)mView.findViewById(R.id.cardView);
            ImageView img = (ImageView)mView.findViewById(R.id.image);
            card.setBackgroundResource(color[i]);
            img.setImageResource(pics[i]);
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
            // Deleted
        }

        public void setMember(String member){
            TextView t = (TextView)mView.findViewById(R.id.emember);
            t.setText(member);
        }

        public void configClickCard(final Event model){

            CardView holder = (CardView)mView.findViewById(R.id.parent_layout);
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ActiveStatus.tempCode.equals(model.getCode()) && ActiveStatus.arrayOfEvents.length<4 && joinable(model.getDate(),model.getTime())){

                        DatabaseReference jointyDB;
                        jointyDB = FirebaseDatabase.getInstance().getReference();
                        ActiveStatus.eventList = ActiveStatus.eventList+model.getCode()+"x";
                        jointyDB.child("userdata").child(ActiveStatus.username).child("eventList").setValue(ActiveStatus.eventList);
                        ActiveStatus.tempCode = model.getCode();
                        String member = model.getParticipant();
                        member = member + ActiveStatus.username + "  ";
                        jointyDB.child("eventdata").child(model.getCode()).child("participant").setValue(member);

                        Snackbar.make(v, "Joined !", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else if(!joinable(model.getDate(),model.getTime())){
                        Snackbar.make(v, "This event has expired.", Snackbar.LENGTH_LONG)
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
            ActiveStatus.username = null;
            ActiveStatus.lineid = null;
            ActiveStatus.eventList = null;
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
        try{
            ActiveStatus.arrayOfEvents = ActiveStatus.eventList.split("x");
        }
        catch (Exception e){

        }
    }

    private String convertDate(String d){
        try{
            String date = "";
            String[] mdy = d.split("/");
            switch (mdy[1]){
                case "01":
                    date = mdy[0] + "/Jan/" + mdy[2];
                    break;
                case "02":
                    date = mdy[0] + "/Feb/" + mdy[2];
                    break;
                case "03":
                    date = mdy[0] + "/Mar/" + mdy[2];
                    break;
                case "04":
                    date = mdy[0] + "/Apr/" + mdy[2];
                    break;
                case "05":
                    date = mdy[0] + "/May/" + mdy[2];
                    break;
                case "06":
                    date = mdy[0] + "/Jun/" + mdy[2];
                    break;
                case "07":
                    date = mdy[0] + "/Jul/" + mdy[2];
                    break;
                case "08":
                    date = mdy[0] + "/Aug/" + mdy[2];
                    break;
                case "09":
                    date = mdy[0] + "/Sep/" + mdy[2];
                    break;
                case "10":
                    date = mdy[0] + "/Oct/" + mdy[2];
                    break;
                case "11":
                    date = mdy[0] + "/Nov/" + mdy[2];
                    break;
                case "12":
                    date = mdy[0] + "/Dec/" + mdy[2];
                    break;
            }
            return date;
        }
        catch(Exception e){
            return "Loading ...";
        }
    }

    public static boolean joinable(String eventDate,String eventTime){
        Calendar c = Calendar.getInstance();
        String subEventDate[] = eventDate.split("/");
        String subEventTime[] = eventTime.split(":");
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.HOUR_OF_DAY,Integer.parseInt(subEventTime[0]));
        c2.set(Calendar.MINUTE,Integer.parseInt(subEventTime[1]));
        c2.set(Calendar.SECOND,0);
        c2.set(Calendar.MILLISECOND,0);
        c2.set(Calendar.DAY_OF_MONTH,Integer.parseInt(subEventDate[0]));
        c2.set(Calendar.MONTH,Integer.parseInt(subEventDate[1])-1);
        c2.set(Calendar.YEAR,Integer.parseInt(subEventDate[2]));

        Date d = c.getTime();
        Date d2 = c2.getTime();
        if (d2.compareTo(d) > 0) {
            return true;
        }
        return false;



    }
}
