package com.parzival48.jointy;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchLineUI extends AppCompatActivity {

    EditText txtUsername;
    TextView showLine;
    Button searchButton,openButton;
    String lineID = "",url;
    DatabaseReference jointyDB = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_line_ui);

        txtUsername = (EditText)findViewById(R.id.txtUserSearch);
        searchButton = (Button)findViewById(R.id.btSearch);
        openButton = (Button)findViewById(R.id.btOpenLine);
        showLine = (TextView)findViewById(R.id.showLine);

        openButton.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idFound(txtUsername.getText().toString().trim());
            }
        });


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
                        Intent i1 = new Intent(SearchLineUI.this,FeedUI.class);
                        i1.setFlags(i1.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i1);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_create:
                        Intent i2 = new Intent(SearchLineUI.this,CreateEventUI.class);
                        i2.setFlags(i2.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i2);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_search:
                        break;

                    case R.id.nav_profile:
                        Intent i4 = new Intent(SearchLineUI.this,ProfileUI.class);
                        i4.setFlags(i4.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i4);
                        overridePendingTransition(0,0);
                        break;

                }
                return false;
            }
        });
    }

    private void idFound(final String user){
        showLine = (TextView)findViewById(R.id.showLine);
        openButton = (Button)findViewById(R.id.btOpenLine);
        jointyDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    lineID = dataSnapshot.child("userdata").child(user).child("lineid").getValue().toString();
                    showLine.setText(lineID);
                    configLine(lineID);
                    openButton.setVisibility(View.VISIBLE);
                }
                catch (Exception e){
                    showLine.setText("- Not found -");
                    openButton.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showLine.setText("- Not found -");
                openButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void configLine(final String line){
        openButton = (Button)findViewById(R.id.btOpenLine);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://line.me/ti/p/~"+line;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
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
}
