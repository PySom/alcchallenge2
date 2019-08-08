package com.alcchisomnwisu.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    private static final int RC_SIGN_IN = 123;
    private static final String DEALS_FOLDER = "deals_images";
    public static FirebaseDatabase mFirebaseDatabase;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    private static ListActivity caller;
    public static Boolean isAdmin;
    public static ArrayList<TravelDeal> mDeals;

    private FirebaseUtil(){}
    public static void openFireBaseUtils(String ref, final ListActivity callerActivity){
        if(firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            caller = callerActivity;
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    // Choose authentication providers
                    if(firebaseAuth.getCurrentUser() == null){
                        signIn();
                    }
                    else{
                        String uid = firebaseAuth.getUid();
                        Log.d("uid", uid);
                        checkAdmin(uid);

                    }
                    Toast.makeText(caller.getBaseContext(), "Welcome back", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();

        }
        mDeals = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }


    public static void connectStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child(DEALS_FOLDER);
    }

    private static void checkAdmin(String uid) {
        isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators")
                .child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

}
