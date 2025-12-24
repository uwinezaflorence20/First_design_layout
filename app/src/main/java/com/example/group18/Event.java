package com.example.group18;

import com.google.gson.annotations.SerializedName;

public class Event {
    
    private final long id;

    @SerializedName("eventName")
    private final String title;

    @SerializedName("eventDate")
    private final String date;

    @SerializedName("eventTime")
    private final String time;

    private final String description; // Matches backend name

    private final String location;    // Matches backend name

    @SerializedName("imageUrl")
    private final String imageUri;

    // Backend 'eventType' seems to correspond to your "Tech/Music" dropdown (Android 'category')
    @SerializedName("eventType")
    private final String category;

    // Backend 'category' seems to correspond to your "Public/Private" radio button (Android 'type')
    @SerializedName("category")
    private final String type;

    public Event(long id, String title, String date, String time, String description, String location, String imageUri, String category, String type) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
        this.location = location;
        this.imageUri = imageUri;
        this.category = category;
        this.type = type;
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getImageUri() { return imageUri; }
    public String getCategory() { return category; }
    public String getType() { return type; }
}
