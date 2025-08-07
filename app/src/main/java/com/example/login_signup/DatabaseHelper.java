package com.example.login_signup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_USERS = "users";

    // Table Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_MOBILE = "mobile";

    // Create Table Query
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_PASSWORD + " TEXT NOT NULL," +
                    COLUMN_MOBILE + " TEXT NOT NULL" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        Log.d(TAG, "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    // Add new user
    public boolean addUser(String email, String password, String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_MOBILE, mobile);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        Log.d(TAG, "User insertion result: " + result);
        return result != -1;
    }

    // Check if user credentials are valid (for login)
    public boolean checkUser(String email, String password, String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ? AND " + COLUMN_MOBILE + " = ?";
        String[] selectionArgs = {email, password, mobile};

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();

        Log.d(TAG, "User validation for " + email + ": " + isValid);
        return isValid;
    }

    // Check if email already exists (prevent duplicate registration)
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        Log.d(TAG, "Email exists check for " + email + ": " + exists);
        return exists;
    }

    // Get user details by email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            String userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE));

            user = new User(userId, userEmail, password, mobile);
        }

        cursor.close();
        db.close();
        return user;
    }

    // Get all users (for debugging/migration purposes)
    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);

        Log.d(TAG, "Total users in database: " + cursor.getCount());
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE));
            Log.d(TAG, "User: ID=" + id + ", Email=" + email + ", Mobile=" + mobile);
        }

        cursor.close();
        db.close();
    }
}
