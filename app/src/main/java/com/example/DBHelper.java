package com.example.a433assn4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "media.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Photos table
        db.execSQL(
                "CREATE TABLE photos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "path TEXT, " +
                        "tags TEXT, " +
                        "datetime TEXT)"
        );

        // Sketches table
        db.execSQL(
                "CREATE TABLE sketches (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "path TEXT, " +
                        "tags TEXT, " +
                        "datetime TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS photos");
        db.execSQL("DROP TABLE IF EXISTS sketches");
        onCreate(db);
    }

    // Insert photo
    public void insertPhoto(String path, String tags, String datetime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("path", path);
        cv.put("tags", tags);
        cv.put("datetime", datetime);
        db.insert("photos", null, cv);
    }

    // Insert sketch
    public void insertSketch(String path, String tags, String datetime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("path", path);
        cv.put("tags", tags);
        cv.put("datetime", datetime);
        db.insert("sketches", null, cv);
    }

    // Get all photos
    public Cursor getAllPhotos() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM photos", null);
    }

    // Get all sketches
    public Cursor getAllSketches() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM sketches", null);
    }

    // Search photos by comma-separated tag list
    public Cursor searchPhotos(String[] tags) {
        SQLiteDatabase db = getReadableDatabase();

        if (tags == null || tags.length == 0) {
            return getAllPhotos();
        }

        StringBuilder query = new StringBuilder("SELECT * FROM photos WHERE ");
        String[] args = new String[tags.length];

        for (int i = 0; i < tags.length; i++) {
            query.append("tags LIKE ?");
            if (i < tags.length - 1) query.append(" OR ");
            args[i] = "%" + tags[i].trim() + "%";
        }

        return db.rawQuery(query.toString(), args);
    }

    // Search sketches by tags
    public Cursor searchSketches(String[] tags) {
        SQLiteDatabase db = getReadableDatabase();

        if (tags == null || tags.length == 0) {
            return getAllSketches();
        }

        StringBuilder query = new StringBuilder("SELECT * FROM sketches WHERE ");
        String[] args = new String[tags.length];

        for (int i = 0; i < tags.length; i++) {
            query.append("tags LIKE ?");
            if (i < tags.length - 1) query.append(" OR ");
            args[i] = "%" + tags[i].trim() + "%";
        }

        return db.rawQuery(query.toString(), args);
    }
}