package com.example.group18.db;

import android.provider.BaseColumns;

public final class EventContract {
    private EventContract() {}

    // Table for the people or groups organizing the events
    public static class OrganizerEntry implements BaseColumns {
        public static final String TABLE_NAME = "organizers";
        public static final String COLUMN_NAME_ORGANIZER_NAME = "name"; // "Google", "Local Cafe"
        public static final String COLUMN_NAME_CONTACT_EMAIL = "email";
    }

    // Table for the events themselves
    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_TITLE = "title"; // "Tech Conference"
        public static final String COLUMN_NAME_LOCATION = "location"; // "Moscone Center"
        public static final String COLUMN_NAME_DATE = "date"; // "2025-12-25"
        public static final String COLUMN_NAME_IMAGE_URI = "image_uri";
        public static final String COLUMN_NAME_CATEGORY = "category"; // "Tech", "Music"
        public static final String COLUMN_NAME_TYPE = "type"; // "Public" or "Private"

        // Foreign key to link to the organizers table
        public static final String COLUMN_NAME_ORGANIZER_ID = "organizer_id";
    }
}
