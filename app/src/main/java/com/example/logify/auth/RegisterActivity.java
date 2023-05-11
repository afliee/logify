package com.example.logify.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logify.R;
import com.example.logify.models.UserModel;
import com.example.logify.utils.Crypto;
import com.example.logify.utils.PasswordUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static final String COUNTRY_CODE = "+84";
    TextInputEditText edtUsername, edtPhoneNumber, edtPassword;
    Button btnRegister, btnCancel;
    TextView tvLoginHref;

    FirebaseAuth mAuth;

    PhoneAuthProvider provider;
    PhoneAuthOptions options;
    UserModel userModel;

    String verificationId = "";
    //    String recentToken = "";
    PhoneAuthProvider.ForceResendingToken recentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        tvLoginHref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });
    }

    private void handleRegister() {
        String username = edtUsername.getText().toString();
        String phoneNumber = edtPhoneNumber.getText().toString();
        String password = edtPassword.getText().toString();

        if (username.isEmpty()) {
            edtUsername.setError("Username is required");
            edtUsername.setFocusable(true);
            return;
        }

        if (phoneNumber.isEmpty()) {
            edtPhoneNumber.setError("Phone number is required");
            edtPhoneNumber.setFocusable(true);
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.setFocusable(true);
            return;
        }

        userModel.checkUserIsExist(phoneNumber, new UserModel.CheckUserExistCallBacks() {
            @Override
            public void onExist() {
                edtPhoneNumber.setError("Phone number is already exist");
                edtPhoneNumber.setFocusable(true);
            }

            @Override
            public void onNotFound() {
                options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(COUNTRY_CODE + phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(RegisterActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(options);
            }
        });
    }

    private void init() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        tvLoginHref = findViewById(R.id.tvLoginHref);

        mAuth = FirebaseAuth.getInstance();
        userModel = new UserModel();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: verification completed");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: failed to verify " + e.getMessage());
                btnRegister.setEnabled(false);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent: code sent" + s);
                Intent otpIntent = new Intent(RegisterActivity.this, OTPVerifyActivity.class);
                otpIntent.putExtra("verificationId", s);
                otpIntent.putExtra("username", edtUsername.getText().toString().trim());
                otpIntent.putExtra("phoneNumber", edtPhoneNumber.getText().toString().trim());
                String password = edtPassword.getText().toString().trim();
                try {
                    String passwordEncrypted = PasswordUtils.hashPassword(password);
                    otpIntent.putExtra("password", passwordEncrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Password invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
//                otpIntent.putExtra("password", edtPassword.getText().toString().trim());
                otpIntent.putExtra("recentToken", forceResendingToken);
                otpIntent.putExtra("actionOption", OTPVerifyActivity.REGISTRATION);
                verificationId = s;
                startActivity(otpIntent);
                finish();
            }
        };
    }
}