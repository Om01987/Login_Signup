package com.example.login_signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView textUserEmail, textUserMobile, textUserId;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textUserEmail = findViewById(R.id.textUserEmail);
        textUserMobile = findViewById(R.id.textUserMobile);
        textUserId = findViewById(R.id.textUserId);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        String loggedEmail = session.getString("logged_in_email", null);

        if (loggedEmail == null) {
            // No user logged in, redirect to login
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // ===== SQLite Data Retrieval =====
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        User currentUser = dbHelper.getUserByEmail(loggedEmail);

        if (currentUser != null) {
            textUserEmail.setText("Email: " + currentUser.email);
            textUserMobile.setText("Mobile: " + currentUser.mobile);
            textUserId.setText("User ID: " + currentUser.id);
        } else {
            textUserEmail.setText("User details not found.");
            Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show();

            // This shouldn't happen, but if it does, redirect to login
            session.edit().clear().apply(); // Clear corrupted session
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        btnLogout.setOnClickListener(v -> {
            session.edit().clear().apply(); // Clear session
            Toast.makeText(ProfileActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}
