package com.example.group18;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.example.group18.db.EventContract;
import com.example.group18.db.EventDbHelper;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private EventDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database helper
        dbHelper = new EventDbHelper(this);

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
        // Refresh the event count every time the activity is shown.
        // This ensures the count is up-to-date after you add a new event.
        updateEventCountInTab();
    }

    private void setupTabLayout() {
        // Add tabs programmatically now that they are removed from XML
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addTab(tabLayout.newTab().setText("Calendar")); // Initial text
        tabLayout.addTab(tabLayout.newTab().setText("Hosting"));

        // Add a listener to know when a tab is selected.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // The "Calendar" tab is at position 1 (the second tab).
                if (tab.getPosition() == 1) {
                    // Open the ListViewActivity to show all events from the database.
                    Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed for this functionality
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Also open the list if the user re-clicks the tab.
                if (tab.getPosition() == 1) {
                    Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Queries the database to get the total number of events and updates the "Calendar" tab's text.
     */
    private void updateEventCountInTab() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Use the built-in DatabaseUtils to efficiently get the count of entries in the events table.
        long eventCount = DatabaseUtils.queryNumEntries(db, EventContract.EventEntry.TABLE_NAME);

        // Get the specific tab we want to update (the "Calendar" tab at position 1).
        TabLayout.Tab calendarTab = tabLayout.getTabAt(1);
        if (calendarTab != null) {
            // Update the text to show the count.
            calendarTab.setText("Calendar (" + eventCount + ")");
        }
    }
}
