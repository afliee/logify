package com.example.logify.models;

import com.example.logify.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Model {
    public static final String TAG = "Model";

    protected FirebaseAuth mAuth;
    protected DatabaseReference database;



    public Model() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public Model(DatabaseReference database) {
        mAuth = FirebaseAuth.getInstance();
        this.database = database;
    }
}
