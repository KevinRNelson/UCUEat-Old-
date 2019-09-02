package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapActivity extends AppCompatActivity {
    private TextView mTextMessage;

    // =============================================================================================
    // Navigation Bar
    // =============================================================================================

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_eateries:
                    startActivity(new Intent(MapActivity.this, MainActivity.class));
                    return true;
                case R.id.nav_map:
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(MapActivity.this, SearchActivity.class));
                    return true;
                case R.id.nav_favorites:
                    startActivity(new Intent(MapActivity.this, FavoritesActivity.class));
                    return true;
                case R.id.nav_account:
                    startActivity(new Intent(MapActivity.this, AccountActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.map_message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.nav_map);
    }

}
