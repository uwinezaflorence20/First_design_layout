package com.example.group18;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Event event);
        void onDeleteClick(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList, OnItemClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, date, time, description, location;
        TextView category, type; // <<< Find new TextViews
        Button editButton, deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardEventImageView);
            title = itemView.findViewById(R.id.cardEventTitle);
            date = itemView.findViewById(R.id.cardEventDate);
            time = itemView.findViewById(R.id.cardEventTime);
            description = itemView.findViewById(R.id.cardEventDescription);
            location = itemView.findViewById(R.id.cardEventLocation);
            category = itemView.findViewById(R.id.cardEventCategory); // <<< Initialize
            type = itemView.findViewById(R.id.cardEventType);       // <<< Initialize
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final Event event, final OnItemClickListener listener) {
            title.setText(event.getTitle());
            date.setText(event.getDate());
            time.setText(event.getTime());
            description.setText(event.getDescription());
            location.setText(event.getLocation());
            category.setText(event.getCategory().toUpperCase()); // <<< Set Category
            type.setText(event.getType());                     // <<< Set Type

            Glide.with(itemView.getContext())
                    .load(Uri.parse(event.getImageUri()))
                    .placeholder(R.drawable.image_placeholder_background)
                    .error(R.drawable.image_placeholder_background)
                    .into(imageView);

            editButton.setOnClickListener(v -> listener.onEditClick(event));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(event));
        }
    }
}
