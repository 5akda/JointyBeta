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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventUI extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText txtEventName,txtLocation,txtDecription;
    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();
    Event newEvent;
    String date,time;
    String userEventList;
    long numOfEvent;
    Spinner mSpinner;

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

                String eventCode = String.valueOf(numOfEvent+1);

                newEvent.setName(txtEventName.getText().toString().trim());
                newEvent.setLoaction(txtLocation.getText().toString().trim());
                newEvent.setDescription(txtDecription.getText().toString().trim());
                newEvent.setDate(date);
                newEvent.setTime(time);
                newEvent.setHost(ActiveStatus.username);
                newEvent.setContact(ActiveStatus.lineid);
                newEvent.setCode(eventCode);

                String validation = validE(newEvent.getName(),newEvent.getLoaction(),newEvent.getDescription());
                if(validation.equals("") && haveSelected(newEvent.getCategory())){

                    jointyDB.child("eventdata").child(eventCode).setValue(newEvent);

                    ActiveStatus.eventList = ActiveStatus.eventList+eventCode+"x";
                    jointyDB.child("userdata").child(ActiveStatus.username).child("eventList").setValue(ActiveStatus.eventList);

                    Snackbar.make(v, "Create Event Successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    ActiveStatus.tempEventName = newEvent.getName();
                }
                else{
                    Snackbar.make(v, validation, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        //category selector
        mSpinner = (Spinner)findViewById(R.id.cate);
        String[] catalog = getResources().getStringArray(R.array.category);
        ArrayAdapter<String> adapterCate = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,catalog);
        mSpinner.setAdapter(adapterCate);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) newEvent.setCategory("");
                else if(position>0 && position<3) newEvent.setCategory("0");
                else if(position>2 && position<6) newEvent.setCategory("1");
                else if(position>5 && position<9) newEvent.setCategory("2");
                else if(position>8 && position<12) newEvent.setCategory("3");
                else if(position>11 && position<14) newEvent.setCategory("4");
                else newEvent.setCategory("5");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newEvent.setCategory("");
            }
        });

        //date picker
        Button btDate = (Button) findViewById(R.id.btDate);
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

                    case R.id.nav_search:
                        Intent i3 = new Intent(CreateEventUI.this,SearchLineUI.class);
                        i3.setFlags(i3.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i3);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_profile:
                        Intent i4 = new Intent(CreateEventUI.this,ProfileUI.class);
                        i4.setFlags(i4.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i4);
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

    // Input Validation
    public static String validE(String nameE, String locatE, String descE){
        boolean nam = false,
                loc = false,
                des = false;
        if(!nameE.equals(ActiveStatus.tempEventName)){
            nam = (nameE.length()>4 && nameE.length()<31);
        }
        loc = (locatE.length()>4 && locatE.length()<31);
        des = (descE.length()>=0 && descE.length()<51);

        if(nam){
            if(loc){
                if(des){
                    return "";
                }
                else return "Please Re-Check Description";
            }
            else return "Please Re-Check Location";
        }
        else return "Please Re-Check Name";
    }

    public static boolean dateCheck(String date, String time){
        // Check Here //
        return true;
    }

    public static boolean haveSelected(String catE){
        if(catE.equals("")) return false;
        else return true;
    }

}