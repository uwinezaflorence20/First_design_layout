package com.example.group18.db;

import android.provider.BaseColumns;

public final class EventContract {
    private EventContract() {}

    // Table for the people or groups organizing the events
    public static class OrganizerEntry implements BaseColumns {
        public static final String TABLE_NAME = "organizers";
        public static final String COLUMN_NAME_ORGANIZER_NAME = "name";
        public static final String COLUMN_NAME_CONTACT_EMAIL = "email";
    }

    // Table for the events themselves
    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_IMAGE_URI = "image_uri";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_TYPE = "type";

        public static final String COLUMN_NAME_ORGANIZER_ID = "organizer_id";
    }
}
