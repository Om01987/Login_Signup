package com.example.login_signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView textUserEmail,
            textUserMobile,
            textFirstName,
            textLastName,
            textAddress,
            textUserId;
    private MaterialButton btnEditProfile,
            btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textUserEmail  = findViewById(R.id.textUserEmail);
        textUserMobile = findViewById(R.id.textUserMobile);
        textFirstName  = findViewById(R.id.textFirstName);
        textLastName   = findViewById(R.id.textLastName);
        textAddress    = findViewById(R.id.textAddress);
        textUserId     = findViewById(R.id.textUserId);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout      = findViewById(R.id.btnLogout);

        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        String loggedEmail = session.getString("logged_in_email", null);
        if (loggedEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        User user = dbHelper.getUserByEmail(loggedEmail);

        if (user != null) {
            textUserEmail.setText("Email: " + user.email);
            textUserMobile.setText("Mobile: " + user.mobile);
            textFirstName.setText("First Name: " + user.firstName);
            textLastName.setText("Last Name: " + user.lastName);
            textAddress.setText("Address: " + user.address);
            textUserId.setText("User ID: " + user.id);
        } else {
            Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show();
            session.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        btnEditProfile.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
            i.putExtra("email", user.email);
            startActivity(i);
        });

        btnLogout.setOnClickListener(v -> {
            session.edit().clear().apply();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
