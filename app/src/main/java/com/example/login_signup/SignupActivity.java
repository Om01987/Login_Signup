package com.example.login_signup;

import android.content.Intent;
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

public class SignupActivity extends AppCompatActivity {

    private TextView textStatus;
    private TextInputLayout layoutEmail,
            layoutPassword,
            layoutConfirmPassword,
            layoutMobile,
            layoutFirstName,
            layoutLastName,
            layoutAddress;
    private TextInputEditText editEmail,
            editPassword,
            editConfirmPassword,
            editMobile,
            editFirstName,
            editLastName,
            editAddress;
    private MaterialButton btnSignup;
    private TextView textGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signupScroll),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        textStatus           = findViewById(R.id.textSignupStatus);
        layoutEmail          = findViewById(R.id.layoutEmail);
        layoutPassword       = findViewById(R.id.layoutPassword);
        layoutConfirmPassword= findViewById(R.id.layoutConfirmPassword);
        layoutMobile         = findViewById(R.id.layoutMobile);
        layoutFirstName      = findViewById(R.id.layoutFirstName);
        layoutLastName       = findViewById(R.id.layoutLastName);
        layoutAddress        = findViewById(R.id.layoutAddress);

        editEmail            = findViewById(R.id.editTextEmail);
        editPassword         = findViewById(R.id.editTextPassword);
        editConfirmPassword  = findViewById(R.id.editTextConfirmPassword);
        editMobile           = findViewById(R.id.editTextMobile);
        editFirstName        = findViewById(R.id.editTextFirstName);
        editLastName         = findViewById(R.id.editTextLastName);
        editAddress          = findViewById(R.id.editTextAddress);

        btnSignup            = findViewById(R.id.buttonSignup);
        textGoToLogin        = findViewById(R.id.textGoToLogin);

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

    // Password validation helpers
    private boolean isLongEnough(String pwd)      { return pwd != null && pwd.length() >= 10; }
    private boolean hasUppercase(String pwd)      { return pwd != null && pwd.matches(".*[A-Z].*"); }
    private boolean hasLowercase(String pwd)      { return pwd != null && pwd.matches(".*[a-z].*"); }
    private boolean hasDigit(String pwd)          { return pwd != null && pwd.matches(".*\\d.*"); }
    private boolean hasSpecialChar(String pwd)    { return pwd != null && pwd.matches(".*[^a-zA-Z0-9].*"); }

    private void validateAndRegister() {
        // Reset errors
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);
        layoutMobile.setError(null);
        layoutFirstName.setError(null);
        layoutLastName.setError(null);
        layoutAddress.setError(null);
        textStatus.setVisibility(TextView.GONE);

        String email       = editEmail.getText()==null?"":editEmail.getText().toString().trim();
        String password    = editPassword.getText()==null?"":editPassword.getText().toString().trim();
        String confirmPwd  = editConfirmPassword.getText()==null?"":editConfirmPassword.getText().toString().trim();
        String mobile      = editMobile.getText()==null?"":editMobile.getText().toString().trim();
        String firstName   = editFirstName.getText()==null?"":editFirstName.getText().toString().trim();
        String lastName    = editLastName.getText()==null?"":editLastName.getText().toString().trim();
        String address     = editAddress.getText()==null?"":editAddress.getText().toString().trim();

        // Email
        if (TextUtils.isEmpty(email)) {
            layoutEmail.setError("Email is required");
            editEmail.requestFocus(); return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Enter a valid email");
            editEmail.requestFocus(); return;
        }

        // Password
        StringBuilder err = new StringBuilder();
        if (!isLongEnough(password))  err.append("At least 10 characters.\n");
        if (!hasUppercase(password))  err.append("At least one uppercase letter.\n");
        if (!hasLowercase(password))  err.append("At least one lowercase letter.\n");
        if (!hasDigit(password))      err.append("At least one digit.\n");
        if (!hasSpecialChar(password))err.append("At least one special character.\n");
        if (err.length()>0) {
            layoutPassword.setError(err.toString().trim());
            editPassword.requestFocus(); return;
        }

        // Confirm password
        if (TextUtils.isEmpty(confirmPwd)) {
            layoutConfirmPassword.setError("Please confirm password");
            editConfirmPassword.requestFocus(); return;
        } else if (!confirmPwd.equals(password)) {
            layoutConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus(); return;
        }

        // Mobile
        if (TextUtils.isEmpty(mobile)) {
            layoutMobile.setError("Mobile number required");
            editMobile.requestFocus(); return;
        } else if (!mobile.matches("[1-9]\\d{9}")) {
            layoutMobile.setError("Enter a valid 10-digit mobile number not starting with 0");
            editMobile.requestFocus(); return;
        }

        // Optional fields - no validation required, but trim spaces
        // firstName, lastName, address may remain blank

        // SQLite integration
        DatabaseHelper db = new DatabaseHelper(this);
        if (db.checkEmailExists(email)) {
            layoutEmail.setError("This email is already registered.");
            editEmail.requestFocus(); return;
        }

        // Insert user with new fields and default role "user"
        boolean ok = db.addUser(
                email, password, mobile,
                firstName, lastName, address,
                "user"
        );

        if (ok) {
            // Auto-login
            getSharedPreferences("session", MODE_PRIVATE)
                    .edit()
                    .putBoolean("is_logged_in", true)
                    .putString("logged_in_email", email)
                    .apply();

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        } else {
            textStatus.setText("Registration failed. Please try again.");
            textStatus.setVisibility(TextView.VISIBLE);
        }
    }
}
