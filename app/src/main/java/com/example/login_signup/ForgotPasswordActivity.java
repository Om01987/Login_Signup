package com.example.login_signup;

import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutEmail;
    private TextInputEditText editTextEmailFP;
    private MaterialButton btnSend;
    private TextView txtBackToLogin, textConfirmation, textSendAgain;
    private ProgressBar progressBar;

    // Remember last email for resend
    private String lastEmailSent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        editTextEmailFP = findViewById(R.id.editTextEmailFP);
        btnSend = findViewById(R.id.buttonSendReset);
        textConfirmation = findViewById(R.id.textConfirmation);
        textSendAgain = findViewById(R.id.textSendAgain);
        txtBackToLogin = findViewById(R.id.textBackToLogin);
        progressBar = findViewById(R.id.progressBar);

        btnSend.setOnClickListener(v -> handleSendReset());
        textSendAgain.setOnClickListener(v -> handleResend());
        txtBackToLogin.setOnClickListener(v -> finish());
    }

    private void handleSendReset() {
        String emailStr = editTextEmailFP.getText() != null ? editTextEmailFP.getText().toString().trim() : "";

        // Input validation
        if (emailStr.isEmpty()) {
            textInputLayoutEmail.setError("Email is required");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            textInputLayoutEmail.setError("Enter a valid email address");
            return;
        } else {
            textInputLayoutEmail.setError(null);
        }

        // Simulate sending: show progress, then show sent message and clear field
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);
        editTextEmailFP.setEnabled(false);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnSend.setEnabled(true);
            editTextEmailFP.setEnabled(true);

            lastEmailSent = emailStr;
            editTextEmailFP.setText("");

            // Show confirmation and send-again link
            textConfirmation.setText("Reset link sent to " + lastEmailSent);
            textConfirmation.setVisibility(View.VISIBLE);
            textSendAgain.setVisibility(View.VISIBLE);

            Toast.makeText(ForgotPasswordActivity.this,
                    "Reset link sent to " + lastEmailSent,
                    Toast.LENGTH_SHORT).show();
        }, 1200);
    }

    private void handleResend() {
        if (lastEmailSent.isEmpty()) return;

        // Show progress and simulate resend
        progressBar.setVisibility(View.VISIBLE);
        textSendAgain.setEnabled(false);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            textSendAgain.setEnabled(true);

            textConfirmation.setText("Reset link resent to " + lastEmailSent);
            Toast.makeText(ForgotPasswordActivity.this,
                    "Reset link resent to " + lastEmailSent,
                    Toast.LENGTH_SHORT).show();
        }, 1000);
    }
}
