package com.parzival48.jointy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class FeedUI extends AppCompatActivity {

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
    }
}