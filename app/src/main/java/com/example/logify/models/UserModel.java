package com.example.logify.models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//this class is used to store data of user reference to firebase
public class UserModel {
    private static final String TAG = "UserModel";
    private static final String USER_COLLECTION = "users";
    private DatabaseReference database;

    public UserModel(DatabaseReference database) {
        this.database = database;
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, "onDataChange: " + snapshot.toString());
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
        DatabaseReference toRef = database.child(USER_COLLECTION).push();

        HashMap<String, Object> userValues = (HashMap<String, Object>) user.toMap();
        toRef.setValue(userValues)
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
                });;

    }

    public void addUserWithGoogle(String uuid, String username, String email, Uri avatar) {
        User user = new User(uuid, username, email, avatar);
        DatabaseReference toRef = database.child(USER_COLLECTION).push();

        HashMap<String, Object> userValues = (HashMap<String, Object>) user.toMap();
        toRef.setValue(userValues)
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
}
