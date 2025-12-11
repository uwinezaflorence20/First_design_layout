package com.example.group18.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.group18.db.EventContract;


public class EventDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CommunityEvents.db"; // New DB name

    private static final String SQL_CREATE_ORGANIZERS =
            "CREATE TABLE " + EventContract.OrganizerEntry.TABLE_NAME + " (" +
                    EventContract.OrganizerEntry._ID + " INTEGER PRIMARY KEY," +
                    EventContract.OrganizerEntry.COLUMN_NAME_ORGANIZER_NAME + " TEXT UNIQUE," +
                    EventContract.OrganizerEntry.COLUMN_NAME_CONTACT_EMAIL + " TEXT)";

    private static final String SQL_CREATE_EVENTS =
            "CREATE TABLE " + EventContract.EventEntry.TABLE_NAME + " (" +
                    EventContract.EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventContract.EventEntry.COLUMN_NAME_TITLE + " TEXT," +
                    EventContract.EventEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    EventContract.EventEntry.COLUMN_NAME_DATE + " TEXT," +
                    EventContract.EventEntry.COLUMN_NAME_IMAGE_URI + " TEXT," +
                    EventContract.EventEntry.COLUMN_NAME_CATEGORY + " TEXT," +
                    EventContract.EventEntry.COLUMN_NAME_TYPE + " TEXT," +
                    EventContract.EventEntry.COLUMN_NAME_ORGANIZER_ID + " INTEGER," +
                    "FOREIGN KEY(" + EventContract.EventEntry.COLUMN_NAME_ORGANIZER_ID + ") REFERENCES " +
                    EventContract.OrganizerEntry.TABLE_NAME + "(" + EventContract.OrganizerEntry._ID + "))";

    private static final String SQL_DELETE_TABLES =
            "DROP TABLE IF EXISTS " + EventContract.EventEntry.TABLE_NAME + ";" +
                    "DROP TABLE IF EXISTS " + EventContract.OrganizerEntry.TABLE_NAME;

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ORGANIZERS);
        db.execSQL(SQL_CREATE_EVENTS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLES);
        onCreate(db);
    }
}
