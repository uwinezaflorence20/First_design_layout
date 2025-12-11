package com.example.group18;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group18.db.EventContract;
import com.example.group18.db.EventDbHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

// Implement the adapter's click listener interface
public class ListViewActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private RecyclerView eventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private EventDbHelper dbHelper;

    private final ActivityResultLauncher<Intent> startCreateOrEditEventActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // This will now refresh the list for both new and edited events.
                    Toast.makeText(this, "List updated.", Toast.LENGTH_SHORT).show();
                    String filter = getIntent().getStringExtra("EVENT_FILTER");
                    loadEventsFromDb(filter != null ? filter : "ALL");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        toolbar = findViewById(R.id.toolbar);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        dbHelper = new EventDbHelper(this);

        setupToolbar();
        setupRecyclerView();

        String filter = getIntent().getStringExtra("EVENT_FILTER");
        loadEventsFromDb(filter != null ? filter : "ALL");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String filter = getIntent().getStringExtra("EVENT_FILTER");
            if ("TODAY".equals(filter)) {
                getSupportActionBar().setTitle("Events Today");
            } else {
                getSupportActionBar().setTitle("All Events");
            }
        }
    }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        // Pass 'this' as the listener when creating the adapter
        adapter = new EventAdapter(this, eventList, this);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(adapter);
    }

    private void loadEventsFromDb(String filter) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // <<< UPDATED: Add CATEGORY and TYPE to the columns being requested >>>
        String[] projection = {
                EventContract.EventEntry._ID,
                EventContract.EventEntry.COLUMN_NAME_TITLE,
                EventContract.EventEntry.COLUMN_NAME_DATE,
                EventContract.EventEntry.COLUMN_NAME_LOCATION,
                EventContract.EventEntry.COLUMN_NAME_IMAGE_URI,
                EventContract.EventEntry.COLUMN_NAME_CATEGORY, // Get category
                EventContract.EventEntry.COLUMN_NAME_TYPE       // Get type
        };

        String selection = null;
        String[] selectionArgs = null;

        if ("TODAY".equals(filter)) {
            String todayDate = new SimpleDateFormat("M/d/yyyy", Locale.US).format(new Date());
            selection = EventContract.EventEntry.COLUMN_NAME_DATE + " = ?";
            selectionArgs = new String[]{todayDate};
        }

        Cursor cursor = db.query(
                EventContract.EventEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null
        );

        eventList.clear();
        while (cursor.moveToNext()) {
            // <<< UPDATED: Read all data from the cursor >>>
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.EventEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(EventContract.EventEntry.COLUMN_NAME_TITLE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(EventContract.EventEntry.COLUMN_NAME_DATE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(EventContract.EventEntry.COLUMN_NAME_LOCATION));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(EventContract.EventEntry.COLUMN_NAME_IMAGE_URI));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(EventContract.EventEntry.COLUMN_NAME_CATEGORY));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(EventContract.EventEntry.COLUMN_NAME_TYPE));

            // <<< UPDATED: Pass all data to the Event model constructor >>>
            eventList.add(new Event(id, title, date, location, imageUri, category, type));
        }
        cursor.close();

        if (eventList.isEmpty()) {
            Toast.makeText(this, "No events found.", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    // Implement the interface methods for Edit and Delete
    @Override
    public void onEditClick(Event event) {
        Intent intent = new Intent(this, Activity3.class);

        // Pack all of the event's data into the Intent as "extras"
        intent.putExtra("EVENT_MODE", "EDIT");
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("EVENT_TITLE", event.getTitle());
        intent.putExtra("EVENT_LOCATION", event.getLocation());
        intent.putExtra("EVENT_DATE", event.getDate());
        intent.putExtra("EVENT_IMAGE_URI", event.getImageUri());
        // <<< UPDATED: Add the category and type to the intent >>>
        intent.putExtra("EVENT_CATEGORY", event.getCategory());
        intent.putExtra("EVENT_TYPE", event.getType());

        // Launch the activity using the launcher that refreshes the list on return.
        startCreateOrEditEventActivity.launch(intent);
    }

    @Override
    public void onDeleteClick(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete '" + event.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteEventFromDb(event);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEventFromDb(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = EventContract.EventEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(event.getId()) };
        int deletedRows = db.delete(EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);

        if (deletedRows > 0) {
            Toast.makeText(this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
            String filter = getIntent().getStringExtra("EVENT_FILTER");
            loadEventsFromDb(filter != null ? filter : "ALL");
        } else {
            Toast.makeText(this, "Error deleting event.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
