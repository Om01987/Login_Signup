package com.example.login_signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<User> users = dbHelper.getAllUsers();

        RecyclerView recycler = findViewById(R.id.recyclerAdminUsers);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new AdminAdapter(users, dbHelper, this));

        // Handle admin logout button
        MaterialButton btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnAdminLogout.setOnClickListener(v -> {
            SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
            session.edit().clear().apply();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
