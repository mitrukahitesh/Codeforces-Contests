package com.hitesh.codeforces;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class AlarmSQLiteHelper extends SQLiteOpenHelper {
    public static final String NAME = "ALARMSET";
    public static final String CONTEST_ID = "contest_id";
    public static final String TABLE = "ALARMS";
    public static final Integer VERSION = 1;

    public AlarmSQLiteHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE ALARMS(_id INTEGER PRIMARY KEY AUTOINCREMENT, contest_id INTEGER)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
