package com.example.logify.models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.auth.OTPVerifyActivity;
import com.example.logify.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

//this class is used to store data of user reference to firebase
public class UserModel {
    private static final String TAG = "UserModel";
    private static final String USER_COLLECTION = "users";
    private DatabaseReference database;

    private PhoneAuthOptions options;
    private FirebaseAuth mAuth;
    private String vetificationId = "";



    public interface UserCallBacks {
        void onCallback(User user);
    }

    public interface LoginCallBacks {
        void onCompleted(User user);
        void onFailure();
    }

    public interface CheckUserExistCallBacks {
        void onExist(boolean isExist);
        void onNotFound();
    }

    public UserModel() {
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }
    public UserModel(DatabaseReference database) {
        this.database = database;
        mAuth = FirebaseAuth.getInstance();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, "onDataChange: " + snapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.toString());
            }
        });
    }

    public DatabaseReference getDatabase() {
        return database;
    }

    public void addUserWithPhone(String uuid, String username, String phoneNumber, String password) {
        User user = new User(uuid, username, phoneNumber, password);
        database.child(USER_COLLECTION).child(uuid).setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: write data successfull ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: write failed ");
                    }
                });

    }

    public void addUserWithGoogle(String uuid, String username, String email, Uri avatar) {
        User user = new User(uuid, username, email, avatar);
        database.child(USER_COLLECTION).child(uuid).setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: write data successfull ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: write failed ");
                    }
                });
    }

    public void updateUser(String uuid, String username, String avatar) {
        database.child(USER_COLLECTION).child(uuid).child("username").setValue(username);
        database.child(USER_COLLECTION).child(uuid).child("avatar").setValue(avatar);
    }

    public void getUser(String uuid, UserCallBacks callBacks) {
        Query query = database.child(USER_COLLECTION).child(uuid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                callBacks.onCallback(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.toString());
            }
        });
    }

    public void login(String uuid, String phoneNumber, String password, LoginCallBacks loginCallBacks) {
        Query query = database.child(USER_COLLECTION).child(uuid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getPhoneNumber().equals(phoneNumber) && user.getPassword().equals(password)) {
                        loginCallBacks.onCompleted(user);
                    } else {
                        loginCallBacks.onFailure();
                    }
                } else {
                    loginCallBacks.onFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.toString());
            }
        });
    }


    public void login(String phoneNumber, String password, LoginCallBacks loginCallBacks) {
        Query query = database.child(USER_COLLECTION);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = null;
                boolean isFound = false;
//                retrieve data from user collection
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user = dataSnapshot.getValue(User.class);
                    Log.w(TAG, "onDataChange: user "  + user.toString());
                    if (user != null) {
                        if (user.getPhoneNumber().equals(phoneNumber) && user.getPassword().equals(password)) {
                            loginCallBacks.onCompleted(user);
                            isFound = true;
                            break;
                        }
                    }
                }
                if (!isFound) {
                    loginCallBacks.onFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.toString());
            }
        });
    }

    public void checkUserIsExist(String phoneNumber, CheckUserExistCallBacks callBacks) {
        Query query = database.child(USER_COLLECTION);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = null;
                boolean isFound = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getPhoneNumber().equals(phoneNumber)) {
                            isFound = true;
                            break;
                        }
                    }
                }

                if (isFound) {
                    callBacks.onExist(true);
                } else {
                    callBacks.onNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
