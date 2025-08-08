package com.example.login_signup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    }
}
