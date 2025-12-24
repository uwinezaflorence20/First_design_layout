package com.example.group18;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.group18.api.ApiClient;
import com.example.group18.api.ApiService;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // --- Find UI elements ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        CardView eventCard = findViewById(R.id.eventCard);
        Button createButton = findViewById(R.id.btnCreate);
        tabLayout = findViewById(R.id.tabLayout);

        // --- Setup Toolbar ---
        setSupportActionBar(toolbar);

        // --- Setup Click Listeners ---
        eventCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
            startActivity(intent);
        });

        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Activity3.class);
            startActivity(intent);
        });

        // --- Setup the Tab Layout ---
        setupTabLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the event count from the API every time the activity is shown
        updateEventCountInTab();
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addTab(tabLayout.newTab().setText("Calendar")); // Initial text
        tabLayout.addTab(tabLayout.newTab().setText("Hosting"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                    startActivity(intent);
                } else if (tab.getPosition() == 2) { // <<< Handle Hosting tab click
                    Intent intent = new Intent(MainActivity.this, HostingActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                    startActivity(intent);
                } else if (tab.getPosition() == 2) { // <<< Also handle re-selection
                    Intent intent = new Intent(MainActivity.this, HostingActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Fetches event data from the API and updates the "Calendar" tab with the total count.
     */
    private void updateEventCountInTab() {
        Call<List<Event>> call = apiService.getAllEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int eventCount = response.body().size();
                    TabLayout.Tab calendarTab = tabLayout.getTabAt(1);
                    if (calendarTab != null) {
                        calendarTab.setText("Calendar (" + eventCount + ")");
                    }
                } else {
                    // If it fails, just show the plain text
                    TabLayout.Tab calendarTab = tabLayout.getTabAt(1);
                    if (calendarTab != null) {
                        calendarTab.setText("Calendar");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                // On network failure, also revert to plain text
                TabLayout.Tab calendarTab = tabLayout.getTabAt(1);
                if (calendarTab != null) {
                    calendarTab.setText("Calendar");
                }
                Toast.makeText(MainActivity.this, "Failed to get event count.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
