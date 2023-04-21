package com.example.logify.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.logify.R;
import com.example.logify.models.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private static final String COUNTRY_CODE = "+84";
    TextInputEditText edtPhoneNumber, edtNewPassword, edtNewPasswordCon;
    Button btnSubmit, btnCancel;

    FirebaseAuth auth;
    UserModel userModel;

    String phoneNumber = "", newPassword = "", newPasswordCon = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });

    }

    private void init() {
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtNewPasswordCon = findViewById(R.id.edtNewPasswordCon);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        auth = FirebaseAuth.getInstance();
        userModel = new UserModel();
    }

    private void handleForgotPassword() {
        String phoneNumber = edtPhoneNumber.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String newPasswordCon = edtNewPasswordCon.getText().toString();

        if (phoneNumber.isEmpty()) {
            edtPhoneNumber.setError("Phone number is required");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            edtNewPassword.setError("New password is required");
            edtNewPassword.requestFocus();
            return;
        }

        if (newPasswordCon.isEmpty()) {
            edtNewPasswordCon.setError("Confirm new password is required");
            edtNewPasswordCon.requestFocus();
            return;
        }

        if (!newPassword.equals(newPasswordCon)) {
            edtNewPasswordCon.setError("New password and confirm new password must be the same");
            edtNewPasswordCon.requestFocus();
            return;
        }

        userModel.checkUserIsExist(phoneNumber, new UserModel.CheckUserExistCallBacks() {
            @Override
            public void onExist() {
                Log.w(TAG, "onExist: execute forgot Password");
                userModel.forgotPassword(phoneNumber, newPassword, ForgotPasswordActivity.this);
            }

            @Override
            public void onNotFound() {
                edtPhoneNumber.setError("Phone number is not exist");
                edtPhoneNumber.requestFocus();
            }
        });
    }
}