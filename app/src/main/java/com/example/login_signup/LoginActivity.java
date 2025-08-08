package com.example.login_signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
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

public class LoginActivity extends AppCompatActivity {

    private TextView textStatus;
    private TextInputLayout layoutEmail, layoutPassword, layoutMobile;
    private TextInputEditText editEmail, editPassword, editMobile;
    private MaterialButton btnLogin;
    private TextView txtRegister, txtForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure admin account exists
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.createAdminUserIfNotExists();

        // Auto-login if session exists
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        if (session.getBoolean("is_logged_in", false)) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Set up UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginScroll), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        textStatus    = findViewById(R.id.textLoginStatus);
        layoutEmail   = findViewById(R.id.layoutEmail);
        layoutPassword= findViewById(R.id.layoutPassword);
        layoutMobile  = findViewById(R.id.layoutMobile);
        editEmail     = findViewById(R.id.editTextEmail);
        editPassword  = findViewById(R.id.editTextPassword);
        editMobile    = findViewById(R.id.editTextMobile);
        btnLogin      = findViewById(R.id.buttonLogin);
        txtRegister   = findViewById(R.id.textRegister);
        txtForgot     = findViewById(R.id.textForgot);

        editMobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editMobile.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAndLogin();
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> validateAndLogin());
        txtRegister.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );
        txtForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );
    }

    private void validateAndLogin() {
        // Reset errors
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutMobile.setError(null);
        textStatus.setVisibility(TextView.GONE);

        String email    = editEmail.getText()   != null ? editEmail.getText().toString().trim()   : "";
        String password = editPassword.getText()!= null ? editPassword.getText().toString().trim(): "";
        String mobile   = editMobile.getText()  != null ? editMobile.getText().toString().trim()  : "";

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            layoutEmail.setError("Email is required");
            editEmail.requestFocus(); return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Enter a valid email");
            editEmail.requestFocus(); return;
        }
        if (TextUtils.isEmpty(password)) {
            layoutPassword.setError("Password is required");
            editPassword.requestFocus(); return;
        } else if (password.length() < 8) {
            layoutPassword.setError("Password must be at least 8 characters");
            editPassword.requestFocus(); return;
        }
        if (TextUtils.isEmpty(mobile)) {
            layoutMobile.setError("Mobile number required");
            editMobile.requestFocus(); return;
        } else if (!mobile.matches("[1-9]\\d{9}")) {
            layoutMobile.setError("Enter a valid 10-digit mobile number");
            editMobile.requestFocus(); return;
        }

        // Authenticate
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean isValid = dbHelper.checkUser(email, password, mobile);
        if (!isValid) {
            if (!dbHelper.checkEmailExists(email)) {
                layoutEmail.setError("Email not registered");
                editEmail.requestFocus();
            } else {
                layoutPassword.setError("Incorrect password or mobile number");
                editPassword.requestFocus();
            }
            return;
        }

        // Save session
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        session.edit()
                .putBoolean("is_logged_in", true)
                .putString("logged_in_email", email)
                .apply();

        // Role-based redirect
        User user = dbHelper.getUserByEmail(email);
        Intent intent;
        if (user != null && user.isAdmin()) {
            intent = new Intent(this, AdminActivity.class);
        } else {
            intent = new Intent(this, ProfileActivity.class);
        }
        startActivity(intent);
        finish();
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
    }
}
