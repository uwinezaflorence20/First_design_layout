package com.example.group18;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group18.api.ApiClient;
import com.example.group18.api.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private RecyclerView eventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    
    // API Service for network calls
    private ApiService apiService;

    private final ActivityResultLauncher<Intent> startCreateOrEditEventActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "List updated.", Toast.LENGTH_SHORT).show();
                    String filter = getIntent().getStringExtra("EVENT_FILTER");
                    loadEventsFromApi(filter != null ? filter : "ALL");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        toolbar = findViewById(R.id.toolbar);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);

        // Initialize the API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        setupToolbar();
        setupRecyclerView();

        String filter = getIntent().getStringExtra("EVENT_FILTER");
        loadEventsFromApi(filter != null ? filter : "ALL");
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
        adapter = new EventAdapter(this, eventList, this);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(adapter);
    }

    // <<< REPLACED: Fetch events from API instead of Local DB >>>
    private void loadEventsFromApi(String filter) {
        Call<List<Event>> call = apiService.getAllEvents();
        
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body();
                    eventList.clear();

                    if ("TODAY".equals(filter)) {
                        // Filter for today's events manually since API returns all
                        String todayDate = new SimpleDateFormat("M/d/yyyy", Locale.US).format(new Date());
                        for (Event event : allEvents) {
                            if (todayDate.equals(event.getDate())) {
                                eventList.add(event);
                            }
                        }
                    } else {
                        eventList.addAll(allEvents);
                    }

                    if (eventList.isEmpty()) {
                        Toast.makeText(ListViewActivity.this, "No events found.", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListViewActivity.this, "Failed to retrieve data from server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(ListViewActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    @Override
    public void onEditClick(Event event) {
        Intent intent = new Intent(this, Activity3.class);
        intent.putExtra("EVENT_MODE", "EDIT");
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("EVENT_TITLE", event.getTitle());
        intent.putExtra("EVENT_LOCATION", event.getLocation());
        intent.putExtra("EVENT_DATE", event.getDate());
        intent.putExtra("EVENT_TIME", event.getTime());
        intent.putExtra("EVENT_DESCRIPTION", event.getDescription());
        intent.putExtra("EVENT_IMAGE_URI", event.getImageUri());
        intent.putExtra("EVENT_CATEGORY", event.getCategory());
        intent.putExtra("EVENT_TYPE", event.getType());

        startCreateOrEditEventActivity.launch(intent);
    }

    @Override
    public void onDeleteClick(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete '" + event.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteEventFromApi(event);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // <<< REPLACED: Delete event via API >>>
    private void deleteEventFromApi(Event event) {
        Call<Void> call = apiService.deleteEvent(event.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ListViewActivity.this, "Event deleted.", Toast.LENGTH_SHORT).show();
                    // Refresh the list
                    String filter = getIntent().getStringExtra("EVENT_FILTER");
                    loadEventsFromApi(filter != null ? filter : "ALL");
                } else {
                    Toast.makeText(ListViewActivity.this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ListViewActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
