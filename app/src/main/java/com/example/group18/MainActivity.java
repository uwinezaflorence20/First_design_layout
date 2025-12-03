package com.example.group18;

import android.content.Intent; // 1. Import the Intent class
import android.os.Bundle;
import android.view.View; // 2. Import the View class
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView; // 3. Import the CardView class

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
        }



        // Find the "Welcome Back Stacy" card from the layout by its ID.
        CardView eventCard = findViewById(R.id.eventCard);

        // Set a click listener on the card.
        eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the card is clicked, create an Intent to open ListViewActivity.
                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                // Execute the intent to start the new activity.
                startActivity(intent);
            }
        });
    }

    // This method handles the click on the back arrow in the toolbar.
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
