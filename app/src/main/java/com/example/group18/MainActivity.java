package com.example.group18; // Make sure this package name matches yours

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- START: Code to set up Toolbar and Tabs ---

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
        }

        // Find the TabLayout from the layout
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Clear any tabs defined in XML to avoid duplicates
        tabLayout.removeAllTabs();

        // Add the "Events" tab
        tabLayout.addTab(tabLayout.newTab().setText("Events"));

        // --- Create and add the styled "Calendar 2" tab ---
        SpannableString spannableString = new SpannableString("Calendar 2");
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 9, 10, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        tabLayout.addTab(tabLayout.newTab().setText(spannableString));

        // Add the "Hosting" tab
        tabLayout.addTab(tabLayout.newTab().setText("Hosting"));

        // --- END: Code to set up Toolbar and Tabs ---
    }

    // This handles the click on the back arrow in the toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
