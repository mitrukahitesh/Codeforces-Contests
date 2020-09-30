package com.hitesh.codeforces;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
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

import com.hitesh.codeforces.contest.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ContestAdapter extends RecyclerView.Adapter<ContestAdapter.CustomVH> {

    Activity activity;
    Context context;
    ArrayList<Result> results;
    AlarmSQLiteHelper sqLiteHelper;
    static Map<Integer, Boolean> alarmsSet = new HashMap<>();

    public ContestAdapter(Context context, ArrayList<Result> results) {
        this.context = context;
        this.results = results;
        activity = (Activity) context;
        sqLiteHelper = new AlarmSQLiteHelper(context);
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT contest_id FROM ALARMS", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Integer columnID = cursor.getColumnIndex(AlarmSQLiteHelper.CONTEST_ID);
                do {
                    alarmsSet.put(cursor.getInt(columnID), true);
                } while (cursor.moveToNext());
            }
        }
    }

    @NonNull
    @Override
    public CustomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contest_holder, parent, false);
        return new CustomVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomVH holder, final int position) {
        Result current = results.get(position);
        holder.name.setText(current.getName());
        holder.type.setText("Type: " + current.getType());
        holder.duration.setText("Duration: " + getDuration(current.getDurationSeconds()));
        if (current.getPhase().equals("CODING")) {
            if (Calendar.getInstance().getTimeInMillis() <
                    (((long) current.getStartTimeSeconds() +
                            (long) current.getDurationSeconds()) * (long) 1000))
                holder.ongoing.setVisibility(View.VISIBLE);
            else {
                holder.ongoing.setVisibility(View.VISIBLE);
                holder.ongoing.setText("FINISHED");
            }
        } else {
            holder.ongoing.setVisibility(View.GONE);
        }
        TimeZone zone = TimeZone.getDefault();
        Date date = new Date((long) current.getStartTimeSeconds() * (long) 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(zone);
        String formattedDate = sdf.format(date);
        holder.start.setText("Start: " + formattedDate);
        if (alarmsSet.containsKey(current.getId())) {
            holder.notify.setImageResource(R.drawable.ic_baseline_alarm_on_24);
        } else {
            holder.notify.setImageResource(R.drawable.ic_baseline_add_alarm_24);
        }
    }

    private String getDuration(Integer durationSeconds) {
        String s;
        Integer minutes = durationSeconds / 60;
        Integer hours = minutes / 60;
        minutes = minutes % 60;
        s = hours + " hours ";
        if (minutes > 0) {
            s = s + minutes + " minutes";
        }
        return s;
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
            setClickListeners();
        }

        private void setClickListeners() {
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String url = "https://www.codeforces.com/contests/" + results.get(position).getId();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(Intent.createChooser(intent, "Select browser"));
                }
            });
            notify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Integer id = results.get(getAdapterPosition()).getId();
                    final String name = results.get(getAdapterPosition()).getName();
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
                    intent.putExtra("Id", id);
                    intent.putExtra("name", name);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (((long) results.get(getAdapterPosition()).getStartTimeSeconds() * (long) 1000) <=
                            (System.currentTimeMillis() + (60 * 60 * 1000) + 10000)) {
                        if (alarmNotSet(id))
                            Toast.makeText(context, "Less than 1 hr left", Toast.LENGTH_LONG).show();
                        else {
                            manager.cancel(pendingIntent);
                            notify.setImageResource(R.drawable.ic_baseline_add_alarm_24);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    removeAlarmFromDb(id);
                                }
                            }).start();
                        }
                        return;
                    }
                    if (alarmNotSet(id)) {
                        manager.setExact(AlarmManager.RTC_WAKEUP,
                                (long) results.get(getAdapterPosition()).getStartTimeSeconds() * (long) 1000 - (long) (60 * 60 * 1000),
                                pendingIntent);
                        notify.setImageResource(R.drawable.ic_baseline_alarm_on_24);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                addAlarmToDb(id);
                            }
                        }).start();
                    } else {
                        manager.cancel(pendingIntent);
                        notify.setImageResource(R.drawable.ic_baseline_add_alarm_24);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                removeAlarmFromDb(id);
                            }
                        }).start();
                    }
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
                    context.startActivity(Intent.createChooser(intent, "Share"));
                }
            });
        }

        private boolean alarmNotSet(Integer id) {
            SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
            String query = "SELECT contest_id FROM ALARMS WHERE contest_id = " + id;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst())
                return false;
            return true;
        }

        private void addAlarmToDb(Integer id) {
            SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("contest_id", id);
            db.insert(AlarmSQLiteHelper.TABLE, null, values);
            alarmsSet.put(id, true);
        }

        private void removeAlarmFromDb(Integer id) {
            SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
            db.delete(AlarmSQLiteHelper.TABLE, AlarmSQLiteHelper.CONTEST_ID + " = " + id, null);
            alarmsSet.remove(id);
        }
    }

}
