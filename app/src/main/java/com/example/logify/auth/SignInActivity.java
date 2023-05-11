package com.example.logify.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logify.MainActivity;
import com.example.logify.R;
import com.example.logify.constants.App;
import com.example.logify.entities.User;
import com.example.logify.models.PlaylistModel;
import com.example.logify.models.UserModel;
import com.example.logify.utils.PasswordUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText edtPhoneNumber, edtPassword;
    Button btnLogin, btnLoginWithGG;
    CheckBox cbRememberMe;
    TextView tvForgot, tvRegisterHref;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    PhoneAuthOptions options;

    DatabaseReference database;
    Dialog dialog;
    UserModel userModel;
    String uuid = "";
    private String code;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final int RC_SIGN_IN = 123;
    private static String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        getDataIntent();

        String passwordtemp = "1111111";
        try {
            String hashed = PasswordUtils.hashPassword(passwordtemp);
            Log.e(TAG, "onCreate: password hashed : " + hashed);
            Log.e(TAG, "onCreate: password verified: " + PasswordUtils.verifyPassword(passwordtemp, hashed) );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


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
//                clear stack activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                finish();
            }
        });

//        forwarding to register activity when click on register href
        tvRegisterHref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
//                clear stack activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                finish();
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

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        uuid = sharedPreferences.getString("uuid", "");
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            code = intent.getStringExtra("code");
        }
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

        if (!TextUtils.isEmpty(uuid)) {
            Log.e(TAG, "handleLogin: login with uuid " + uuid);
            userModel.login(uuid, phoneNumber, password, new UserModel.LoginCallBacks() {
                @Override
                public void onCompleted(User user) {
                    if (user != null) {
                        Log.e(TAG, "onCompleted: Login success " + user.getPhoneNumber());
                        if (cbRememberMe.isChecked()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uuid", user.getUuid());
                            editor.apply();
                        }
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure() {

                }
            });
        } else {
            Log.e(TAG, "handleLogin: login without uuid ");
            userModel.login(phoneNumber, password, new UserModel.LoginCallBacks() {
                @Override
                public void onCompleted(User user) {
                    if (user != null) {
                        Log.e(TAG, "onCompleted: Login success " + user.getPhoneNumber());
                        if (cbRememberMe.isChecked()) {
                            Log.e(TAG, "onCompleted: check is remember me + userid: " + user.getUuid());
                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uuid", user.getUuid());
                            editor.apply();
                        }

                        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                getDataIntent();
//                                Log.e(TAG, "onVerificationCompleted: verify completed: code -> " + code);
//                                if (code != null) {
//                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneNumber, code);
//                                    signInPhoneAuthWithCredential(credential);
//                                }
//                                signInPhoneAuthWithCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);

                                Intent intent = new Intent(SignInActivity.this, OTPVerifyActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("password", password);
                                intent.putExtra("verificationId", s);
                                intent.putExtra("actionOption", OTPVerifyActivity.RE_LOGIN);
                                startActivity(intent);
                                finish();
                            }
                        };
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+84" + phoneNumber,
                                60L,
                                TimeUnit.SECONDS,
                                SignInActivity.this,
                                mCallbacks
                        );

//                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
                    } else {
                        
                        Log.e(TAG, "onCompleted: loggin failed");
                        Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "onFailure: failed to login without uuid");
                    Toast.makeText(SignInActivity.this, "Data Invalid!!!", Toast.LENGTH_SHORT).show();
                    edtPhoneNumber.requestFocus();
                }
            });
        }
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

    private void signInPhoneAuthWithCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInPhoneAuthWithCredential: success");
                        FirebaseUser user = task.getResult().getUser();
                        if (cbRememberMe.isChecked()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(App.SHARED_PREFERENCES_USER, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(App.SHARED_PREFERENCES_UUID, user.getUid());
                            editor.apply();
                        }
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "signInPhoneAuthWithCredential: failed");
                    }
                });
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

                        if (cbRememberMe.isChecked()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uuid", user.getUid());
                            editor.apply();
                        }

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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    public void updataUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }
}

