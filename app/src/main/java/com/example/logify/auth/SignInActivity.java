package com.example.logify.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.logify.MainActivity;
import com.example.logify.R;
import com.example.logify.entities.User;
import com.example.logify.models.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText edtPhoneNumber, edtPassword;
    Button btnLogin, btnLoginWithGG;
    CheckBox cbRememberMe;
    TextView tvForgot, tvRegisterHref;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    DatabaseReference database;
    Dialog dialog;
    UserModel userModel;

    private static final int RC_SIGN_IN = 123;
    private static String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();

//        config google sign in

//        add event for button login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });


//        add event for button login with google
        btnLoginWithGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginWithGG();
            }
        });

//        forwarding to forgot password activity
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        forwarding to register activity when click on register href
        tvRegisterHref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void init() {
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginWithGG = findViewById(R.id.btnLoginWithGG);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvForgot = findViewById(R.id.tvForgot);
        tvRegisterHref = findViewById(R.id.tvRegisterHref);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        userModel = new UserModel(database);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        dialog = new Dialog(this);

//        fill input if user is have login
    }

    private void handleLogin() {
        String phoneNumber = edtPhoneNumber.getText().toString();
        String password = edtPassword.getText().toString();

//        request forcus and fill when phone number emty
        if (phoneNumber.isEmpty()) {
            edtPhoneNumber.setError("Phone number is required");
            edtPhoneNumber.requestFocus();
            return;
        }
//        request forcus and fill when password emty
        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return;
        }
//        require password at least 6 characters
        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return;
        }

//        if all is ok, then login
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleLoginWithGG() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        handle result of login with google intent
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "onActivityResult: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
//                get userId
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "firebaseAuthWithGoogle: success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        Log.e(TAG, "firebaseAuthWithGoogle: user " + user.getDisplayName());
                        userModel.addUserWithGoogle(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl());
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "firebaseAuthWithGoogle: failed");
                    }
                })
                .addOnFailureListener(e -> {

                    Log.d(TAG, "firebaseAuthWithGoogle: " + e.getMessage());
                });
    }
}

