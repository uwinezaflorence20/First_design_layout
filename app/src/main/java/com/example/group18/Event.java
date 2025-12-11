package com.example.group18;

public class Event {
    private final long id;
    private final String title;
    private final String date;
    private final String location;
    private final String imageUri;
    private final String category; // Field for category
    private final String type;     // Field for type

    // --- THIS IS THE CORRECT CONSTRUCTOR THAT ACCEPTS 7 ARGUMENTS ---
    public Event(long id, String title, String date, String location, String imageUri, String category, String type) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
        this.category = category; // Initialize category
        this.type = type;         // Initialize type
    }

    // Getters for all fields
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getImageUri() { return imageUri; }
    public String getCategory() { return category; } // Getter for category
    public String getType() { return type; }         // Getter for type
}
