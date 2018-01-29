package com.rucksack.dailywallpaper.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rucksack.dailywallpaper.BuildConfig;
import com.rucksack.dailywallpaper.data.provider.TasksContract;
import com.rucksack.dailywallpaper.util.Constants;

/**
 * @author liaoheng
 * @version 2018-01-16 15:44
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Constants.PROJECT_NAME + ".db", null, BuildConfig.VERSION_CODE);
    }

    private static final String DB_CREATE = "create table " + TasksContract.TaskEntry.TABLE_NAME +
            " (" + TasksContract.TaskEntry._ID + " integer primary key autoincrement, " +
            TasksContract.TaskEntry.COLUMN_TAG + " text not null, " +
            TasksContract.TaskEntry.COLUMN_DATE + " integer not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
