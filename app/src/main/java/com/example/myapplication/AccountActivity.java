package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {
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
                    startActivity(new Intent(AccountActivity.this, MainActivity.class));
                    return true;
                case R.id.nav_map:
                    startActivity(new Intent(AccountActivity.this, MapActivity.class));
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(AccountActivity.this, SearchActivity.class));
                    return true;
                case R.id.nav_favorites:
                    startActivity(new Intent(AccountActivity.this, FavoritesActivity.class));
                    return true;
                case R.id.nav_account:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.account_message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.nav_account);

    }

}
