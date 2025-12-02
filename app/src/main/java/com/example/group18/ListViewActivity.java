package com.example.group18;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ListViewActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);

        // Setup toolbar
        setupToolbar();

        // Setup ListView
        setupListView();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Events List");
        }
    }

    private void setupListView() {
        // Sample data for ListView
        String[] events = {
                "Welcome Back Stacy - Lombard Palace Cafe",
                "Tech Meetup - Silicon Valley Center",
                "Music Festival - Golden Gate Park",
                "Art Exhibition - SFMOMA",
                "Food Fair - Ferry Building",
                "Startup Pitch Night - Union Square",
                "Yoga in the Park - Dolores Park",
                "Comedy Night - The Fillmore",
                "Wine Tasting - Napa Valley",
                "Book Reading - City Lights Bookstore",
                "Jazz Concert - Blue Note",
                "Photography Walk - Presidio",
                "Farmers Market - Embarcadero",
                "Networking Event - Salesforce Tower",
                "Film Screening - Castro Theatre",
                "Dance Workshop - Mission District",
                "Cooking Class - Chinatown",
                "Charity Run - Marina Green",
                "Poetry Slam - Cafe Revolution",
                "Gaming Tournament - Metreon",
                "Film Screening - Castro Theatre",
                "Dance Workshop - Mission District",
                "Cooking Class - Chinatown",
                "Charity Run - Marina Green",
                "Poetry Slam - Cafe Revolution",
                "Gaming Tournament - Metreon"
        };

        // Create ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                events
        );

        // Set adapter to ListView
        listView.setAdapter(adapter);

        // Set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedEvent = events[position];
                Toast.makeText(ListViewActivity.this,
                        "Selected: " + selectedEvent,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}