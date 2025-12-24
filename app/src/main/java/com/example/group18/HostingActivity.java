package com.example.group18;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class HostingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);

        ListView hostingListView = findViewById(R.id.hostingListView);

        // Sample data for the list
        String[] hostingData = {
            "My Past Event 1",
            "My Upcoming Event A",
            "Draft Event X",
            "Archived Event Y",
            "My Past Event 2",
            "My Upcoming Event B",
                "My Past Event 1",
                "My Upcoming Event A",
                "Draft Event X",
                "Archived Event Y",
                "My Past Event 2",
                "My Upcoming Event B",
                "My Past Event 1",
                "My Upcoming Event A",
                "Draft Event X",
                "Archived Event Y",
                "My Past Event 2",
                "My Upcoming Event B"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, hostingData);

        hostingListView.setAdapter(adapter);
    }
}
