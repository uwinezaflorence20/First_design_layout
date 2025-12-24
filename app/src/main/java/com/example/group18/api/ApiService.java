package com.example.group18.api;

import com.example.group18.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Get all events
    @GET("events")
    Call<List<Event>> getAllEvents();

    // Create a new event
    @POST("events")
    Call<Event> createEvent(@Body Event event);

    // Update an event
    @PUT("events/{id}")
    Call<Event> updateEvent(@Path("id") long id, @Body Event event);

    // Delete an event
    @DELETE("events/{id}")
    Call<Void> deleteEvent(@Path("id") long id);
}
