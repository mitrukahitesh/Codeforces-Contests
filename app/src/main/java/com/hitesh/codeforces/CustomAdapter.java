package com.hitesh.codeforces;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.net.Proxy.Type.HTTP;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomVH> {

    Activity activity;
    Context context;
    ArrayList<Result> results;
    LinearLayout layout;

    public CustomAdapter(Context context, ArrayList<Result> results) {
        this.context = context;
        this.results = results;
        activity = (Activity) context;
    }

    @NonNull
    @Override
    public CustomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.customview, parent, false);
        return new CustomVH(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CustomVH holder, final int position) {
        holder.name.setText(results.get(position).getName());
        holder.type.setText("Type: " + results.get(position).getType());
        holder.duration.setText("Duration: " + results.get(position).getDurationSeconds() / 3600 + " hours");
        if (results.get(position).getPhase().equals("CODING")) {
            if ((Calendar.getInstance().getTimeInMillis() < (long)((long)results.get(position).getStartTimeSeconds() +
                    (long)results.get(position).getDurationSeconds()) * 1000))
                holder.ongoing.setVisibility(View.VISIBLE);
            else{
                holder.ongoing.setVisibility(View.VISIBLE);
                holder.ongoing.setText("FINISHED");
            }
        }
        TimeZone zone = TimeZone.getDefault();
        Date date = new Date((long) results.get(position).getStartTimeSeconds() * (long) 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(zone);
        String formattedDate = sdf.format(date);
        holder.start.setText("Start: " + formattedDate);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class CustomVH extends RecyclerView.ViewHolder {
        TextView name, start, duration, type, ongoing;
        ImageView notify, info, share;

        public CustomVH(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            start = (TextView) itemView.findViewById(R.id.start);
            duration = (TextView) itemView.findViewById(R.id.duration);
            ongoing = (TextView) itemView.findViewById(R.id.ongoing);
            type = (TextView) itemView.findViewById(R.id.type);
            info = (ImageView) itemView.findViewById(R.id.info);
            notify = (ImageView) itemView.findViewById(R.id.notify);
            share = (ImageView) itemView.findViewById(R.id.share);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String url = "https://www.codeforces.com/contests/" + results.get(position).getId();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                }
            });
            notify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE, results.get(position).getName());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, (long) results.get(position).getStartTimeSeconds() * 1000);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, (long) results.get(position).getStartTimeSeconds() * 1000
                            + results.get(position).getDurationSeconds() * 1000);
                    intent.putExtra(CalendarContract.Events.HAS_ALARM, true);
                    int permission_read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
                    int permission_write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR);
                    if (permission_read == PackageManager.PERMISSION_DENIED)
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 0);
                    if (permission_write == PackageManager.PERMISSION_DENIED)
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
                    if (intent.resolveActivity(context.getPackageManager()) != null)
                        context.startActivity(intent);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Check this upcoming CodeForces contest\n\n" +
                            "https://www.codeforces.com/contests/" + results.get(position).getId());
                    context.startActivity(intent);
                }
            });
        }
    }

}
