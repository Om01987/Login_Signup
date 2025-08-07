package com.example.login_signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.content.SharedPreferences;
import android.widget.Toast;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.UUID;
//import java.lang.reflect.Type;


public class SignupActivity extends AppCompatActivity {

    private TextView textStatus;
    private TextInputLayout layoutEmail, layoutPassword, layoutConfirmPassword, layoutMobile;
    private TextInputEditText editEmail, editPassword, editConfirmPassword, editMobile;
    private MaterialButton btnSignup;
    private TextView textGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signupScroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textStatus = findViewById(R.id.textSignupStatus);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        layoutMobile = findViewById(R.id.layoutMobile);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editMobile = findViewById(R.id.editTextMobile);
        btnSignup = findViewById(R.id.buttonSignup);
        textGoToLogin = findViewById(R.id.textGoToLogin);

        // Defensive: enforce phone number length
        editMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        editMobile.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAndRegister();
                return true;
            }
            return false;
        });

        btnSignup.setOnClickListener(v -> validateAndRegister());
        textGoToLogin.setOnClickListener(v -> finish());
    }

    // ----------- Password Validation Requirements -----------
    private boolean isLongEnough(String pwd) {
        return pwd != null && pwd.length() >= 10;
    }
    private boolean hasUppercase(String pwd) {
        return pwd != null && pwd.matches(".*[A-Z].*");
    }
    private boolean hasLowercase(String pwd) {
        return pwd != null && pwd.matches(".*[a-z].*");
    }
    private boolean hasDigit(String pwd) {
        return pwd != null && pwd.matches(".*\\d.*");
    }
    private boolean hasSpecialChar(String pwd) {
        return pwd != null && pwd.matches(".*[^a-zA-Z0-9].*");
    }
/*
    private void validateAndRegister() {
        // Reset errors and status
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);
        layoutMobile.setError(null);
        textStatus.setVisibility(TextView.GONE);

        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String password = editPassword.getText() != null ? editPassword.getText().toString().trim() : "";
        String confirmPassword = editConfirmPassword.getText() != null ? editConfirmPassword.getText().toString().trim() : "";
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

        // Advanced password validation
        StringBuilder errorBuilder = new StringBuilder();
        if (!isLongEnough(password)) {
            errorBuilder.append("A password must have at least ten characters.\n");
        }
        if (!hasUppercase(password)) {
            errorBuilder.append("A password must include at least one capital letter.\n");
        }
        if (!hasLowercase(password)) {
            errorBuilder.append("A password must include at least one small letter.\n");
        }
        if (!hasDigit(password)) {
            errorBuilder.append("A password must include at least one digit.\n");
        }
        if (!hasSpecialChar(password)) {
            errorBuilder.append("A password must include at least one special character.\n");
        }
        if (errorBuilder.length() > 0) {
            layoutPassword.setError(errorBuilder.toString().trim());
            editPassword.requestFocus();
            return;
        } else {
            layoutPassword.setError(null);
        }

        // Confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            layoutConfirmPassword.setError("Please confirm password");
            editConfirmPassword.requestFocus();
            return;
        } else if (!confirmPassword.equals(password)) {
            layoutConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus();
            return;
        } else {
            layoutConfirmPassword.setError(null);
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
        } else {
            layoutMobile.setError(null);
        }

        // Success: registered
//        textStatus.setText("Registered as " + email);
//        textStatus.setVisibility(TextView.VISIBLE);

        // SAVE REGISTERED USER - for single user only

//        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
//        android.content.SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("email", email);
//        editor.putString("password", password);
//        editor.putString("mobile", mobile);
//        editor.apply();

        // Prepare SharedPreferences for multi-user storage
        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        Gson gson = new Gson();
        String usersJson = prefs.getString("users_data", "{}");
        Type type = new TypeToken<Map<String, User>>(){}.getType();
        Map<String, User> users = gson.fromJson(usersJson, type);

        if (users == null) users = new HashMap<>();

        // Prevent duplicate registration
        if (users.containsKey(email)) {
            layoutEmail.setError("This email is already registered.");
            editEmail.requestFocus();
            return;
        }

        // Add new user with random id
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, email, password, mobile);
        users.put(email, user);

        // Save updated user map to SharedPreferences
        prefs.edit().putString("users_data", gson.toJson(users)).apply();


        // Save login session info (auto login after signup)
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        session.edit()
                .putBoolean("is_logged_in", true)
                .putString("logged_in_email", email)
                .apply();

        // Redirect to ProfileActivity
        Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();

//        // Show success as before
//        textStatus.setText("Registered as " + email);
//        textStatus.setVisibility(TextView.VISIBLE);



        // Clear fields for next registration
//        editEmail.setText("");
//        editPassword.setText("");
//        editConfirmPassword.setText("");
//        editMobile.setText("");
//        editEmail.requestFocus();
    }

 */

    private void validateAndRegister() {
        // Reset errors and status
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);
        layoutMobile.setError(null);
        textStatus.setVisibility(TextView.GONE);

        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String password = editPassword.getText() != null ? editPassword.getText().toString().trim() : "";
        String confirmPassword = editConfirmPassword.getText() != null ? editConfirmPassword.getText().toString().trim() : "";
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

        // Advanced password validation
        StringBuilder errorBuilder = new StringBuilder();
        if (!isLongEnough(password)) {
            errorBuilder.append("A password must have at least ten characters.\n");
        }
        if (!hasUppercase(password)) {
            errorBuilder.append("A password must include at least one capital letter.\n");
        }
        if (!hasLowercase(password)) {
            errorBuilder.append("A password must include at least one small letter.\n");
        }
        if (!hasDigit(password)) {
            errorBuilder.append("A password must include at least one digit.\n");
        }
        if (!hasSpecialChar(password)) {
            errorBuilder.append("A password must include at least one special character.\n");
        }
        if (errorBuilder.length() > 0) {
            layoutPassword.setError(errorBuilder.toString().trim());
            editPassword.requestFocus();
            return;
        } else {
            layoutPassword.setError(null);
        }

        // Confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            layoutConfirmPassword.setError("Please confirm password");
            editConfirmPassword.requestFocus();
            return;
        } else if (!confirmPassword.equals(password)) {
            layoutConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus();
            return;
        } else {
            layoutConfirmPassword.setError(null);
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
        } else {
            layoutMobile.setError(null);
        }

        // ===== SQLite Integration =====
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Check if email already exists
        if (dbHelper.checkEmailExists(email)) {
            layoutEmail.setError("This email is already registered.");
            editEmail.requestFocus();
            return;
        }

        // Add user to SQLite database
        boolean isInserted = dbHelper.addUser(email, password, mobile);

        if (isInserted) {
            // Save login session info (auto login after signup)
            SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
            session.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("logged_in_email", email)
                    .apply();

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

            // Redirect to ProfileActivity
            Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Registration failed
            textStatus.setText("Registration failed. Please try again.");
            textStatus.setVisibility(TextView.VISIBLE);
        }
    }

}
