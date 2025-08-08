package com.example.login_signup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2; // Updated from 1 to 2

    // Table Name
    private static final String TABLE_USERS = "users";

    // Existing Table Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_MOBILE = "mobile";

    // New Table Columns for Version 2
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_ROLE = "role";

    // Create Table Query (for fresh installs)
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_PASSWORD + " TEXT NOT NULL," +
                    COLUMN_MOBILE + " TEXT NOT NULL," +
                    COLUMN_FIRST_NAME + " TEXT DEFAULT ''," +
                    COLUMN_LAST_NAME + " TEXT DEFAULT ''," +
                    COLUMN_ADDRESS + " TEXT DEFAULT ''," +
                    COLUMN_ROLE + " TEXT DEFAULT 'user'" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        Log.d(TAG, "Database created successfully with all columns");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {
            // Add new columns without dropping existing data
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_FIRST_NAME + " TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_LAST_NAME + " TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_ADDRESS + " TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_ROLE + " TEXT DEFAULT 'user'");
                Log.d(TAG, "Successfully added new columns for version 2");
            } catch (Exception e) {
                Log.e(TAG, "Error upgrading database: " + e.getMessage());
            }
        }
        // Handle future upgrades here (version 3, 4, etc.)
    }

    // EXISTING METHODS (Updated for backward compatibility)

    // Add new user (original method - backward compatible)
    public boolean addUser(String email, String password, String mobile) {
        return addUser(email, password, mobile, "", "", "", "user");
    }

    // Add new user with all fields
    public boolean addUser(String email, String password, String mobile,
                           String firstName, String lastName, String address, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_FIRST_NAME, firstName != null ? firstName : "");
        values.put(COLUMN_LAST_NAME, lastName != null ? lastName : "");
        values.put(COLUMN_ADDRESS, address != null ? address : "");
        values.put(COLUMN_ROLE, role != null ? role : "user");

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

    // Get user details by email (Updated for new fields)
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

            // Get new fields (with null checks for existing users)
            String firstName = getColumnValue(cursor, COLUMN_FIRST_NAME, "");
            String lastName = getColumnValue(cursor, COLUMN_LAST_NAME, "");
            String address = getColumnValue(cursor, COLUMN_ADDRESS, "");
            String role = getColumnValue(cursor, COLUMN_ROLE, "user");

            user = new User(userId, userEmail, password, mobile, firstName, lastName, address, role);
        }

        cursor.close();
        db.close();
        return user;
    }

    // Helper method to safely get column values
    private String getColumnValue(Cursor cursor, String columnName, String defaultValue) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex != -1) {
                String value = cursor.getString(columnIndex);
                return value != null ? value : defaultValue;
            }
        } catch (Exception e) {
            Log.w(TAG, "Column " + columnName + " not found, using default value");
        }
        return defaultValue;
    }

    // NEW METHODS FOR VERSION 2

    // Update user profile information
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, user.firstName);
        values.put(COLUMN_LAST_NAME, user.lastName);
        values.put(COLUMN_ADDRESS, user.address);
        values.put(COLUMN_MOBILE, user.mobile);
        // Note: Not updating email and password for security

        String whereClause = COLUMN_EMAIL + " = ?";
        String[] whereArgs = { user.email };

        int rows = db.update(TABLE_USERS, values, whereClause, whereArgs);
        db.close();

        Log.d(TAG, "User update result for " + user.email + ": " + rows + " rows affected");
        return rows > 0;
    }

    // Check if admin user exists
    public boolean isAdminExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_ROLE + " = ?";
        String[] selectionArgs = {"admin"};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Create admin user if not exists
    public void createAdminUserIfNotExists() {
        if (!isAdminExists()) {
            boolean result = addUser("admin@app.com", "Admin@123", "9999999999",
                    "Admin", "User", "App Headquarters", "admin");
            Log.d(TAG, "Admin user creation result: " + result);
        }
    }

    // Get all users (Updated for admin functionality)
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COLUMN_EMAIL + " COLLATE NOCASE");

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE));
            String firstName = getColumnValue(cursor, COLUMN_FIRST_NAME, "");
            String lastName = getColumnValue(cursor, COLUMN_LAST_NAME, "");
            String address = getColumnValue(cursor, COLUMN_ADDRESS, "");
            String role = getColumnValue(cursor, COLUMN_ROLE, "user");

            userList.add(new User(id, email, password, mobile, firstName, lastName, address, role));
        }

        cursor.close();
        db.close();
        return userList;
    }

    // Delete user by email (for admin)
    public boolean deleteUserByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_USERS, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
        Log.d(TAG, "User deletion result for " + email + ": " + rows + " rows affected");
        return rows > 0;
    }

    // Get all users (for debugging/migration purposes) - Updated
    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);

        Log.d(TAG, "Total users in database: " + cursor.getCount());
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE));
            String firstName = getColumnValue(cursor, COLUMN_FIRST_NAME, "");
            String lastName = getColumnValue(cursor, COLUMN_LAST_NAME, "");
            String role = getColumnValue(cursor, COLUMN_ROLE, "user");
            Log.d(TAG, "User: ID=" + id + ", Email=" + email + ", Mobile=" + mobile +
                    ", Name=" + firstName + " " + lastName + ", Role=" + role);
        }

        cursor.close();
        db.close();
    }
}
