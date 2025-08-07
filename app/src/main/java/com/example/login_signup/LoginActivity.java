package com.example.login_signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.content.SharedPreferences;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.util.Map;
//import java.lang.reflect.Type;


public class LoginActivity extends AppCompatActivity {

    private TextView textStatus;
    private TextInputLayout layoutEmail, layoutPassword, layoutMobile;
    private TextInputEditText editEmail, editPassword, editMobile;
    private MaterialButton btnLogin;
    private TextView txtRegister, txtForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        if (session.getBoolean("is_logged_in", false)) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
            return;  // prevent continuing to set content view
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginScroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textStatus = findViewById(R.id.textLoginStatus);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutMobile = findViewById(R.id.layoutMobile);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editMobile = findViewById(R.id.editTextMobile);
        btnLogin = findViewById(R.id.buttonLogin);
        txtRegister = findViewById(R.id.textRegister);
        txtForgot = findViewById(R.id.textForgot);

        // Set max length: Defensive coding (already in XML, but ensures)
        editMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        // Keyboard behavior (allows login on "done" on mobile)
        editMobile.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAndLogin();
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> validateAndLogin());
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
        txtForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
/*
    private void validateAndLogin() {
        // Reset errors and status
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutMobile.setError(null);
        textStatus.setVisibility(View.GONE);

        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String password = editPassword.getText() != null ? editPassword.getText().toString().trim() : "";
        String mobile = editMobile.getText() != null ? editMobile.getText().toString().trim() : "";

        // Email validation
        if (TextUtils.isEmpty(email)) {
            layoutEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Enter a valid email");
            editEmail.requestFocus();
            return;
        }

        // Password validation: At least 8 chars, can add more if needed
        if (TextUtils.isEmpty(password)) {
            layoutPassword.setError("Password is required");
            editPassword.requestFocus();
            return;
        } else if (password.length() < 8) {
            layoutPassword.setError("Password must be at least 8 characters");
            editPassword.requestFocus();
            return;
        }

        // Mobile validation: exactly 10 digits, doesn't start with 0, all are digits
        if (TextUtils.isEmpty(mobile)) {
            layoutMobile.setError("Mobile number required");
            editMobile.requestFocus();
            return;
        } else if (mobile.length() != 10) {
            layoutMobile.setError("Enter a 10-digit mobile number");
            editMobile.requestFocus();
            return;
        } else if (mobile.startsWith("0")) {
            layoutMobile.setError("Mobile number cannot start with 0");
            editMobile.requestFocus();
            return;
        } else if (!mobile.matches("\\d{10}")) {
            layoutMobile.setError("Only digits allowed");
            editMobile.requestFocus();
            return;
        }
// old single user check
        // --- CHECK REGISTERED USER ---
//        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
//        String regEmail = prefs.getString("email", "");
//        String regPassword = prefs.getString("password", "");
//        String regMobile = prefs.getString("mobile", "");
//
//        // If user not registered
//        if (TextUtils.isEmpty(regEmail)) {
//            layoutEmail.setError("No user registered. Please sign up first.");
//            editEmail.requestFocus();
//            return;
//        }
//
//        // If not matching credentials
//        if (!email.equals(regEmail)) {
//            layoutEmail.setError("Email not registered");
//            editEmail.requestFocus();
//            return;
//        }
//        if (!password.equals(regPassword)) {
//            layoutPassword.setError("Incorrect password");
//            editPassword.requestFocus();
//            return;
//        }
//        if (!mobile.equals(regMobile)) {
//            layoutMobile.setError("Incorrect mobile number");
//            editMobile.requestFocus();
//            return;
//        }

        // Multi-user login support
        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        Gson gson = new Gson();
        String usersJson = prefs.getString("users_data", "{}");
        Type type = new TypeToken<Map<String, User>>(){}.getType();
        Map<String, User> users = gson.fromJson(usersJson, type);

        if (users == null || !users.containsKey(email)) {
            layoutEmail.setError("Email not registered");
            editEmail.requestFocus();
            return;
        }
        User user = users.get(email);

        if (!user.password.equals(password)) {
            layoutPassword.setError("Incorrect password");
            editPassword.requestFocus();
            return;
        }
        if (!user.mobile.equals(mobile)) {
            layoutMobile.setError("Incorrect mobile number");
            editMobile.requestFocus();
            return;
        }

// Success logic (show status, navigate, etc.)
//        textStatus.setText("Logged in as " + email);
//        textStatus.setVisibility(View.VISIBLE);
//        editEmail.setText("");
//        editPassword.setText("");
//        editMobile.setText("");
//        editEmail.requestFocus();

        // Save login session information
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        session.edit()
                .putBoolean("is_logged_in", true)
                .putString("logged_in_email", email)  // or user.id
                .apply();

        // Redirect to ProfileActivity
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();

//        // "Login successful" status
//        textStatus.setText("Logged in as " + email);
//        textStatus.setVisibility(View.VISIBLE);
//
//        // Clear fields for next login
//        editEmail.setText("");
//        editPassword.setText("");
//        editMobile.setText("");
//
//        // return keyboard focus to email
//        editEmail.requestFocus();

         //  show a toast
         Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
    }

 */
private void validateAndLogin() {
    // Reset errors and status
    layoutEmail.setError(null);
    layoutPassword.setError(null);
    layoutMobile.setError(null);
    textStatus.setVisibility(View.GONE);

    String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
    String password = editPassword.getText() != null ? editPassword.getText().toString().trim() : "";
    String mobile = editMobile.getText() != null ? editMobile.getText().toString().trim() : "";

    // Email validation
    if (TextUtils.isEmpty(email)) {
        layoutEmail.setError("Email is required");
        editEmail.requestFocus();
        return;
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        layoutEmail.setError("Enter a valid email");
        editEmail.requestFocus();
        return;
    }

    // Password validation
    if (TextUtils.isEmpty(password)) {
        layoutPassword.setError("Password is required");
        editPassword.requestFocus();
        return;
    } else if (password.length() < 8) {
        layoutPassword.setError("Password must be at least 8 characters");
        editPassword.requestFocus();
        return;
    }

    // Mobile validation
    if (TextUtils.isEmpty(mobile)) {
        layoutMobile.setError("Mobile number required");
        editMobile.requestFocus();
        return;
    } else if (mobile.length() != 10) {
        layoutMobile.setError("Enter a 10-digit mobile number");
        editMobile.requestFocus();
        return;
    } else if (mobile.startsWith("0")) {
        layoutMobile.setError("Mobile number cannot start with 0");
        editMobile.requestFocus();
        return;
    } else if (!mobile.matches("\\d{10}")) {
        layoutMobile.setError("Only digits allowed");
        editMobile.requestFocus();
        return;
    }

    // ===== SQLite Authentication =====
    DatabaseHelper dbHelper = new DatabaseHelper(this);

    // Check if user credentials are valid
    boolean isValidUser = dbHelper.checkUser(email, password, mobile);

    if (isValidUser) {
        // Save login session information
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        session.edit()
                .putBoolean("is_logged_in", true)
                .putString("logged_in_email", email)
                .apply();

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        // Redirect to ProfileActivity
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    } else {
        // Check specific error for better UX
        if (!dbHelper.checkEmailExists(email)) {
            layoutEmail.setError("Email not registered");
            editEmail.requestFocus();
        } else {
            // Email exists but password or mobile is wrong
            layoutPassword.setError("Incorrect password or mobile number");
            editPassword.requestFocus();
        }
    }
}

}
