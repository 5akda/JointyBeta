package com.parzival48.jointy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                newEvent.setDescpition(txtDecription.getText().toString().trim());
                newEvent.setDate(date);
                newEvent.setTime(time);
                newEvent.setHost(ActiveStatus.username);
                newEvent.setContact(ActiveStatus.lineid);
                jointyDB.child("eventdata").child(String.valueOf(numOfEvent+1)).setValue(newEvent);
                jointyDB.child("userdata").child(ActiveStatus.username).child("eventlist").child(String.valueOf(numOfEvent+1));

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
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(c.getTime());
        TextView txtDate = (TextView)findViewById(R.id.dateView);
        txtDate.setText(currentDate);
        date = txtDate.getText().toString();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView txtTime = (TextView)findViewById(R.id.timeView);
        txtTime.setText(hourOfDay+":"+minute);
        time = txtTime.getText().toString().trim();
    }
}
