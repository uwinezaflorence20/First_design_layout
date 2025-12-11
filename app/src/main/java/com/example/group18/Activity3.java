package com.example.group18;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.group18.db.EventContract;
import com.example.group18.db.EventDbHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Activity3 extends AppCompatActivity {

    // UI View variables
    private EditText editTextEventName, editTextLocation, editTextEventDate;
    private AutoCompleteTextView autoCompleteCategory;
    private RadioGroup radioGroupEventType;
    private ImageView eventImageView;
    private Button btnSave;

    // Database and Image URI variables
    private EventDbHelper dbHelper;
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

        dbHelper = new EventDbHelper(this);
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
        String imageUriString = intent.getStringExtra("EVENT_IMAGE_URI");
        String category = intent.getStringExtra("EVENT_CATEGORY");
        String type = intent.getStringExtra("EVENT_TYPE");

        editTextEventName.setText(title);
        editTextLocation.setText(location);
        editTextEventDate.setText(date);
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
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory);
        radioGroupEventType = findViewById(R.id.radioGroupEventType);
        btnSave = findViewById(R.id.btnSave);

        String[] categories = new String[]{"Tech", "Music", "Art", "Food", "Community", "Sports"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        autoCompleteCategory.setAdapter(adapter);

        eventImageView.setOnClickListener(v -> showImageSourceDialog());
        editTextEventDate.setOnClickListener(v -> showDatePickerDialog());
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

    // New method specifically for updating an existing event
    private void updateExistingEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();
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

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventContract.EventEntry.COLUMN_NAME_TITLE, eventName);
        values.put(EventContract.EventEntry.COLUMN_NAME_LOCATION, location);
        values.put(EventContract.EventEntry.COLUMN_NAME_DATE, eventDate);
        values.put(EventContract.EventEntry.COLUMN_NAME_CATEGORY, category);
        values.put(EventContract.EventEntry.COLUMN_NAME_TYPE, eventType);
        values.put(EventContract.EventEntry.COLUMN_NAME_IMAGE_URI, photoUriString);

        String selection = EventContract.EventEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(existingEventId)};

        int count = db.update(
                EventContract.EventEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Toast.makeText(this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error updating event.", Toast.LENGTH_SHORT).show();
        }
    }

    // The original save logic, now in its own method
    private void createNewEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();
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

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues organizerValues = new ContentValues();
        organizerValues.put(EventContract.OrganizerEntry.COLUMN_NAME_ORGANIZER_NAME, "My Events");
        long organizerId = db.insertWithOnConflict(EventContract.OrganizerEntry.TABLE_NAME, null, organizerValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (organizerId == -1) {
            // If conflict, find the ID of the existing organizer
            // (A more robust implementation would be needed for multiple organizers)
        }

        ContentValues eventValues = new ContentValues();
        eventValues.put(EventContract.EventEntry.COLUMN_NAME_TITLE, eventName);
        eventValues.put(EventContract.EventEntry.COLUMN_NAME_LOCATION, location);
        eventValues.put(EventContract.EventEntry.COLUMN_NAME_DATE, eventDate);
        eventValues.put(EventContract.EventEntry.COLUMN_NAME_CATEGORY, category);
        eventValues.put(EventContract.EventEntry.COLUMN_NAME_TYPE, eventType);
        eventValues.put(EventContract.EventEntry.COLUMN_NAME_IMAGE_URI, photoUriString);
        eventValues.put(EventContract.EventEntry.COLUMN_NAME_ORGANIZER_ID, organizerId);

        long newRowId = db.insert(EventContract.EventEntry.TABLE_NAME, null, eventValues);

        if (newRowId != -1) {
            Toast.makeText(this, "Event Saved Successfully!", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving event.", Toast.LENGTH_SHORT).show();
        }
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

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String selectedDate = (month + 1) + "/" + day + "/" + year;
                    editTextEventDate.setText(selectedDate);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
