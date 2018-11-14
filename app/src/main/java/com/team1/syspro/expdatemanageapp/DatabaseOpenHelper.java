package com.team1.syspro.expdatemanageapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


//SQLite database のHelperの継承
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "list.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "listdb";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_TITLE = "product";
    private static final String COLUMN_NAME_SUBTITLE = "exp_date";

    private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER_PRIMARY_KEY," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_SUBTITLE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + TABLE_NAME;

    DatabaseOpenHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("my-debug","onCreate(SQLiteDatabase db)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
