package com.example.logify.models;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.logify.auth.ForgotPasswordActivity;
import com.example.logify.auth.OTPVerifyActivity;
import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Artist;
import com.example.logify.entities.User;
import com.example.logify.utils.Crypto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//this class is used to store data of user reference to firebase
public class UserModel extends Model{
    private static final String TAG = "UserModel";
    private static final String USER_COLLECTION = "users";
    private static final String COUNTRY_CODE = "+84";
//    private DatabaseReference database;

    private PhoneAuthOptions options;
//    private FirebaseAuth mAuth;
    private String vetificationId = "";
    private PlaylistModel playlistModel = new PlaylistModel();

    public interface UserCallBacks {
        void onCallback(User user);
    }

    public interface onAddConfigListener {
        void onCompleted();
        void onFailure();
    }

    public interface onGetConfigListener {
        void onCompleted(List<Map<String, Object>> config);
        void onFailure();
    }
    public interface LoginCallBacks {
        void onCompleted(User user);
        void onFailure();
    }

    public interface CheckUserExistCallBacks {
        void onExist();
        void onNotFound();
    }

    public interface OnGetArtistFavoriteListener {
        void onCompleted(ArrayList<Artist> artist);
        void onFailure();
    }

    public UserModel() {
        super();
    }
    public UserModel(DatabaseReference database) {
        super(database);
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


    public void addUserWithPhone(String uuid, String username, String phoneNumber, String password, String playlistId) {
        User user = new User(uuid, username, phoneNumber, password, playlistId);
        database.child(USER_COLLECTION).child(uuid).setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: write data successfull ");
                        playlistModel.add(uuid, "Your favorite songs", "This is your favorite songs", "", uuid, LocalTime.now().toString());
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
        User user = new User(uuid, username, email, avatar, uuid);
        database.child(USER_COLLECTION).child(uuid).setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: write data successfull ");
                        playlistModel.add(uuid, "Your favorite songs", "This is your favorite songs", "", uuid, LocalTime.now().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: write failed ");
                    }
                });
    }

    public String getCurrentUser() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    public void updateUser(String uuid, String username, String avatar) {
        if (username != null) {
            database.child(USER_COLLECTION).child(uuid).child("username").setValue(username);
        }

        if (avatar != null) {
            database.child(USER_COLLECTION).child(uuid).child("avatar").setValue(avatar);
        }
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
                    try {
                        String passwordDecrypted = Crypto.decrypt(user.getPassword());
                        if (user.getPhoneNumber().equals(phoneNumber) && password.equals(passwordDecrypted)) {
                            loginCallBacks.onCompleted(user);
                        } else {
                            loginCallBacks.onFailure();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        loginCallBacks.onFailure();
                    }
//                    if (user.getPhoneNumber().equals(phoneNumber) && user.getPassword().equals(password)) {
//                        loginCallBacks.onCompleted(user);
//                    } else {
//                        loginCallBacks.onFailure();
//                    }
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
                    if (user != null) {
                        try {
                            String passwordDecrypted = Crypto.decrypt(user.getPassword());
                            if (user.getPhoneNumber().equals(phoneNumber) && password.equals(passwordDecrypted)) {
                                loginCallBacks.onCompleted(user);
                                isFound = true;
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loginCallBacks.onFailure();
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
                            callBacks.onExist();
                            return;
//                            break;
                        }
                    }
                }
                callBacks.onNotFound();
//                if (isFound) {
//                    callBacks.onExist();
//                } else {
//                    callBacks.onNotFound();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void forgotPassword(String phoneNumber, String newPassword, ForgotPasswordActivity forgotPasswordActivity) {
        options = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(COUNTRY_CODE + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)

                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.e(TAG, "onVerificationCompleted: verify completed" );
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        Intent intent = new Intent(forgotPasswordActivity, OTPVerifyActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("newPassword", newPassword);
                        intent.putExtra("verificationId", s);
                        intent.putExtra("actionOption", OTPVerifyActivity.FORGOT_PASSWORD);
                        forgotPasswordActivity.startActivity(intent);
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void updatePassword(String uuid, String newPassword) {
        database.child(USER_COLLECTION).child(uuid).child("password").setValue(newPassword);
    }

    public void updateConfig(String userId,String key, List<Map<String, Object>> values, onAddConfigListener listener) {
        Query query = database.child(Schema.USERS).child(userId).child(App.CONFIGURATION).child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    database.child(Schema.USERS).child(userId).child(App.CONFIGURATION).child(key).removeValue();
                    database.child(Schema.USERS).child(userId).child(App.CONFIGURATION).child(key).setValue(values);
                }else {
                    database.child(Schema.USERS).child(userId).child(App.CONFIGURATION).child(key).setValue(values);
                }
                listener.onCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error occur " + error.toString());
                listener.onFailure();
            }
        });
    }

    public void getConfig(String userId, String key, onGetConfigListener listener) {
        if (userId == null || key == null) {
            listener.onFailure();
            return;
        }
        Query query = database.child(Schema.USERS).child(userId).child(App.CONFIGURATION).child(key);
        if (query != null) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Object values = snapshot.getValue();
                        List<Map<String, Object>> list = new ArrayList<>();
                        if (values instanceof List) {
                            for (Object object : (List) values) {
                                if (object instanceof Map) {
                                    list.add((Map<String, Object>) object);
                                }
                            }
                        }
                        listener.onCompleted(list);
                    } else {
                        listener.onCompleted(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: error occur " + error.toString());
                    listener.onFailure();
                }
            });
        } else {
            listener.onCompleted(null);
        }
    }

    public void getArtistIdsFavorite(String userId, OnGetArtistFavoriteListener listener) {
        Query query = database.child(Schema.USERS).child(userId).child(App.CONFIGURATION).child(Schema.FAVORITE_ARTISTS);
        if (query != null) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Object values = snapshot.getValue();
                        if (values instanceof List) {
                            JSONArray artists = new JSONArray((List) values);
                            if (artists != null) {
                                ArrayList<Artist> artistArrayList = new ArrayList<>();
                                for (int i = 0; i < artists.length(); i++) {
                                    JSONObject artist = artists.optJSONObject(i);
                                    try {
                                        String id = artist.getString(Schema.ArtistType.ID);
                                        String name = artist.getString(Schema.ArtistType.NAME);
                                        String image = artist.getString(Schema.ArtistType.IMAGE);
                                        String playlistId = artist.getString(Schema.ArtistType.PLAYLIST_ID);

                                        Artist artist1 = new Artist(id, name, image, playlistId);
                                        artistArrayList.add(artist1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                listener.onCompleted(artistArrayList);
                            } else {
                                listener.onCompleted(null);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: error occur " + error.toString());
                    listener.onFailure();
                }
            });
        } else {
            listener.onCompleted(null);
        }
    }
}
