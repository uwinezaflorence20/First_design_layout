package com.example.group18;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.group18.api.ApiClient;
import com.example.group18.api.ApiService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity3 extends AppCompatActivity {

    // UI View variables
    private EditText editTextEventName, editTextLocation, editTextEventDate;
    private EditText editTextEventTime, editTextEventDescription;
    private AutoCompleteTextView autoCompleteCategory;
    private RadioGroup radioGroupEventType;
    private ImageView eventImageView;
    private Button btnSave;

    // API Service and Image URI variables
    private ApiService apiService;
    private Uri imageUri;

    // --- NEW: Variables to manage edit mode ---
    private boolean isEditMode = false;
    private long existingEventId = -1;

    // Launchers (no changes needed here)
    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            isSuccess -> {
                if (isSuccess) {
                    Glide.with(this).load(imageUri).into(eventImageView);
                }
            }
    );
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            imageUri = selectedImageUri;
                            Glide.with(this).load(imageUri).into(eventImageView);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to get permission for image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        // Initialize API Service
        apiService = ApiClient.getClient().create(ApiService.class);
        
        initializeViewsAndListeners();

        // Check if we are in CREATE or EDIT mode
        Intent intent = getIntent();
        if (intent != null && "EDIT".equals(intent.getStringExtra("EVENT_MODE"))) {
            isEditMode = true;
            existingEventId = intent.getLongExtra("EVENT_ID", -1);
            populateFormForEdit(intent);
        } else {
            isEditMode = false;
        }

        setupToolbar(); // Call after checking mode to set the correct title
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_activity3);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Set title based on mode
            if (isEditMode) {
                getSupportActionBar().setTitle("Edit Event");
            } else {
                getSupportActionBar().setTitle("Create Event");
            }
        }
    }

    // New method to pre-fill the form with existing data
    private void populateFormForEdit(Intent intent) {
        String title = intent.getStringExtra("EVENT_TITLE");
        String location = intent.getStringExtra("EVENT_LOCATION");
        String date = intent.getStringExtra("EVENT_DATE");
        String time = intent.getStringExtra("EVENT_TIME");
        String description = intent.getStringExtra("EVENT_DESCRIPTION");
        String imageUriString = intent.getStringExtra("EVENT_IMAGE_URI");
        String category = intent.getStringExtra("EVENT_CATEGORY");
        String type = intent.getStringExtra("EVENT_TYPE");

        editTextEventName.setText(title);
        editTextLocation.setText(location);
        editTextEventDate.setText(date);
        editTextEventTime.setText(time);
        editTextEventDescription.setText(description);
        autoCompleteCategory.setText(category, false);

        if (type != null) {
            if (type.equals("Public")) {
                radioGroupEventType.check(R.id.radio_public);
            } else if (type.equals("Private")) {
                radioGroupEventType.check(R.id.radio_private);
            }
        }

        if (imageUriString != null && !imageUriString.equals("no_image_uri")) {
            imageUri = Uri.parse(imageUriString);
            Glide.with(this).load(imageUri).into(eventImageView);
        }

        btnSave.setText("Update Event");
    }

    private void initializeViewsAndListeners() {
        eventImageView = findViewById(R.id.eventImageView);
        editTextEventName = findViewById(R.id.editTextEventName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextEventDate = findViewById(R.id.editTextEventDate);
        editTextEventTime = findViewById(R.id.editTextEventTime);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory);
        radioGroupEventType = findViewById(R.id.radioGroupEventType);
        btnSave = findViewById(R.id.btnSave);

        String[] categories = new String[]{"Tech", "Music", "Art", "Food", "Community", "Sports"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        autoCompleteCategory.setAdapter(adapter);

        eventImageView.setOnClickListener(v -> showImageSourceDialog());
        editTextEventDate.setOnClickListener(v -> showDatePickerDialog());
        editTextEventTime.setOnClickListener(v -> showTimePickerDialog());
        btnSave.setOnClickListener(v -> saveOrUpdateEvent());
    }

    // This method now decides whether to create a new event or update an existing one
    private void saveOrUpdateEvent() {
        if (isEditMode) {
            updateExistingEvent();
        } else {
            createNewEvent();
        }
    }

    // New method specifically for updating an existing event via API
    private void updateExistingEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();
        String eventTime = editTextEventTime.getText().toString().trim();
        String description = editTextEventDescription.getText().toString().trim();
        String category = autoCompleteCategory.getText().toString().trim();

        String eventType = "";
        int checkedRadioButtonId = radioGroupEventType.getCheckedRadioButtonId();
        if (checkedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(checkedRadioButtonId);
            eventType = selectedRadioButton.getText().toString();
        }

        String photoUriString = (imageUri != null) ? imageUri.toString() : "no_image_uri";

        if (eventName.isEmpty() || location.isEmpty() || eventDate.isEmpty() || category.isEmpty() || eventType.isEmpty() || existingEventId == -1) {
            Toast.makeText(this, "Please fill all fields, including event type.", Toast.LENGTH_LONG).show();
            return;
        }

        // <<< FIX: Construct date in YYYY-MM-DD for API >>>
        // Note: This relies on the date picker setting the date as YYYY-MM-DD
        Event event = new Event(existingEventId, eventName, eventDate, eventTime, description, location, photoUriString, category, eventType);
        
        Call<Event> call = apiService.updateEvent(existingEventId, event);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Activity3.this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(Activity3.this, "Error updating event on server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(Activity3.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // New method for creating an event via API
    private void createNewEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();
        String eventTime = editTextEventTime.getText().toString().trim();
        String description = editTextEventDescription.getText().toString().trim();
        String category = autoCompleteCategory.getText().toString().trim();

        String eventType = "";
        int checkedRadioButtonId = radioGroupEventType.getCheckedRadioButtonId();
        if (checkedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(checkedRadioButtonId);
            eventType = selectedRadioButton.getText().toString();
        }

        String photoUriString = (imageUri != null) ? imageUri.toString() : "no_image_uri";

        if (eventName.isEmpty() || location.isEmpty() || eventDate.isEmpty() || category.isEmpty() || eventType.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields, including event type.", Toast.LENGTH_LONG).show();
            return;
        }

        // We pass 0 for ID since the server will generate it
        Event newEvent = new Event(0, eventName, eventDate, eventTime, description, location, photoUriString, category, eventType);

        Call<Event> call = apiService.createEvent(newEvent);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Activity3.this, "Event Saved Successfully!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(Activity3.this, "Error saving event to server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(Activity3.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // Methods for image and date pickers
    private void showImageSourceDialog() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        new AlertDialog.Builder(this)
                .setTitle("Select Event Photo")
                .setItems(options, (dialog, item) -> {
                    if ("Take Photo".equals(options[item])) {
                        dispatchTakePictureIntent();
                    } else if ("Choose from Gallery".equals(options[item])) {
                        openGallery();
                    } else {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file.", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.example.group18.fileprovider", photoFile);
                takePictureLauncher.launch(imageUri);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // <<< UPDATED: Date picker format to YYYY-MM-DD for backend compatibility >>>
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    // Format: YYYY-MM-DD
                    // Note: month is 0-indexed, so we add 1
                    String selectedDate = String.format(Locale.US, "%d-%02d-%02d", year, month + 1, day);
                    editTextEventDate.setText(selectedDate);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // New Method for Time Picker
    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    editTextEventTime.setText(time);
                }, hour, minute, true).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
