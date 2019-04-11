package com.parzival48.jointy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class CreateEventUI extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText txtEventName,txtLocation,txtDecription;
    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();
    Event newEvent;
    String date,time;
    String userEventList;
    long numOfEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_ui);

        newEvent = new Event();
        txtEventName = (EditText)findViewById(R.id.txtEventName);
        txtLocation = (EditText)findViewById(R.id.txtLocation);
        txtDecription = (EditText)findViewById(R.id.txtDescription);

        jointyDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numOfEvent = dataSnapshot.child("eventdata").getChildrenCount();
                userEventList = dataSnapshot.child("userdata").child(ActiveStatus.username).child("eventList").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //create event process
        Button btCreate = (Button)findViewById(R.id.btCreateEvent);
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Loading ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                newEvent.setName(txtEventName.getText().toString().trim());
                newEvent.setLoaction(txtLocation.getText().toString().trim());
                newEvent.setDescription(txtDecription.getText().toString().trim());
                newEvent.setDate(date);
                newEvent.setTime(time);
                newEvent.setHost(ActiveStatus.username);
                newEvent.setContact(ActiveStatus.lineid);
                jointyDB.child("eventdata").child(String.valueOf(numOfEvent+1)).setValue(newEvent);

                userEventList = userEventList+"# "+newEvent.getName()+"\n";
                userEventList = userEventList+"   "+newEvent.getLoaction()+"\n";
                userEventList = userEventList+"   "+newEvent.getDate()+" - "+newEvent.getTime()+"\n";
                jointyDB.child("userdata").child(ActiveStatus.username).child("eventList").setValue(userEventList);


                Snackbar.make(v, "Create Event Successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //date picker
        Button btDate = (Button)findViewById(R.id.btDate);
        btDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });
        //time picker
        Button btTime = (Button)findViewById(R.id.btTime);
        btTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
            }
        });

        //Bottom Navigation Config
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_feed:
                        Intent i1 = new Intent(CreateEventUI.this,FeedUI.class);
                        i1.setFlags(i1.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i1);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_create:
                        break;

                    case R.id.nav_profile:
                        Intent i3 = new Intent(CreateEventUI.this,ProfileUI.class);
                        i3.setFlags(i3.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i3);
                        overridePendingTransition(0,0);
                        break;

                }
                return false;
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String todayDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(c.getTime());
        String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(c.getTime());
        TextView txtDate = (TextView)findViewById(R.id.dateView);
        txtDate.setText(currentDate);
        date = txtDate.getText().toString();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView txtTime = (TextView)findViewById(R.id.timeView);
        if(minute == 0){
            txtTime.setText(hourOfDay+":00");
        }
        else{
            txtTime.setText(hourOfDay+":"+minute);
        }
        time = txtTime.getText().toString().trim();
    }
}
