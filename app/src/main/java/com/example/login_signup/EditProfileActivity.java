package com.example.login_signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout layoutFirstName, layoutLastName, layoutAddress;
    private TextInputEditText editFirstName, editLastName, editAddress;
    private MaterialButton btnSave;

    private String email;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.editProfileScroll),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                }
        );

        layoutFirstName = findViewById(R.id.layoutFirstName);
        layoutLastName  = findViewById(R.id.layoutLastName);
        layoutAddress   = findViewById(R.id.layoutAddress);

        editFirstName   = findViewById(R.id.editFirstName);
        editLastName    = findViewById(R.id.editLastName);
        editAddress     = findViewById(R.id.editAddress);

        btnSave         = findViewById(R.id.btnSaveProfile);

        dbHelper = new DatabaseHelper(this);

        // Get email from intent or session
        email = getIntent().getStringExtra("email");
        if (TextUtils.isEmpty(email)) {
            SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
            email = session.getString("logged_in_email", null);
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing user data
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            editFirstName.setText(user.firstName);
            editLastName.setText(user.lastName);
            editAddress.setText(user.address);
        }

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String firstName = editFirstName.getText() != null
                ? editFirstName.getText().toString().trim() : "";
        String lastName = editLastName.getText() != null
                ? editLastName.getText().toString().trim() : "";
        String address = editAddress.getText() != null
                ? editAddress.getText().toString().trim() : "";

        // Optional: validate lengths or non-empty if required
        // For now, accept empty values

        // Fetch existing user object
        User user = dbHelper.getUserByEmail(email);
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update fields
        user.firstName = firstName;
        user.lastName  = lastName;
        user.address   = address;

        boolean updated = dbHelper.updateUser(user);
        if (updated) {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            // Return to profile screen and refresh
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}
