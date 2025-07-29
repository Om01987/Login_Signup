package com.example.login_signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

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

        SharedPreferences usersPrefs = getSharedPreferences("users", MODE_PRIVATE);
        String usersJson = usersPrefs.getString("users_data", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, User>>(){}.getType();
        Map<String, User> users = gson.fromJson(usersJson, type);

        User currentUser = users.get(loggedEmail);

        if (currentUser != null) {
            textUserEmail.setText("Email: " + currentUser.email);
            textUserMobile.setText("Mobile: " + currentUser.mobile);
            textUserId.setText("User ID: " + currentUser.id);
        } else {
            textUserEmail.setText("User details not found.");
        }

        btnLogout.setOnClickListener(v -> {
            session.edit().clear().apply(); // clear session
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}
