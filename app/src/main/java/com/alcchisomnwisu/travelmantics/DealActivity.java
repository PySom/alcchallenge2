package com.alcchisomnwisu.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    public static final String TRAVELDEAL_PARCE = "com.alcchisomnwisu.travelmantics.TRAVELDEAL_PARCE";
    public static final int UPLOAD_IMAGE = 42;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtTitle;
    EditText txtPrice;
    EditText txtDescription;
    Button btnUpload;
    ImageView img;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deal_main);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        initializeFields();
        populateExistingValues();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPLOAD_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            final StorageReference storageRef = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
            UploadTask uploadTask = storageRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        deal.setImageUrl(downloadUri.toString());
                        deal.setImageName(storageRef.getPath());
                        Log.d("name", storageRef.getPath());
                        showImage(downloadUri.toString());
                    } else {
                        Toast.makeText(DealActivity.this, "We could not upload the image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void populateExistingValues() {
        Intent intent = getIntent();
        deal = intent.getParcelableExtra(TRAVELDEAL_PARCE);
        if(deal != null){
            populateFields(deal);
        }
        else{
            deal = new TravelDeal();
        }
    }

    private void populateFields(TravelDeal deal) {
        txtPrice.setText(deal.getPrice());
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        showImage(deal.getImageUrl());
        txtTitle.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        validateAdmin(menu);
        return true;
    }

    private void validateAdmin(Menu menu) {
        menu.findItem(R.id.save_menu).setVisible(FirebaseUtil.isAdmin);
        menu.findItem(R.id.delete_menu).setVisible(FirebaseUtil.isAdmin);
        enableEditText(FirebaseUtil.isAdmin);
    }

    private void enableEditText(Boolean enable) {
        txtDescription.setEnabled(enable);
        txtPrice.setEnabled(enable);
        txtTitle.setEnabled(enable);
        btnUpload.setEnabled(enable);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                if(deal.getId() == null){
                    saveDeal();
                }
                else{
                    editDeal();
                    closeKeyboard();

                }
                backToList();
                return true;
            case R.id.delete_menu:
                if(deal != null){
                    deleteDeal();
                    backToList();
                }
                else{
                    Toast.makeText(this, "Save the deal first", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backToList() {
        startActivity(new Intent(this, ListActivity.class));
    }


    private void deleteDeal() {
        String imgName = deal.getImageName();
        if(imgName != null && !imgName.isEmpty()){
            StorageReference picRef = FirebaseUtil.mFirebaseStorage.getReference().child(imgName);
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Implement my success later
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //shaw admin the error
                }
            });
        }
        mDatabaseReference.child(deal.getId()).removeValue();
        Toast.makeText(this, "deleted successfully", Toast.LENGTH_SHORT).show();
    }

    private void editDeal() {
        TravelDeal td = getTravelDeal();
        td.setId(deal.getId());
        mDatabaseReference.child(deal.getId()).setValue(td);
        Toast.makeText(this, "edited successfully", Toast.LENGTH_SHORT).show();
    }

    private void clean() {
        txtPrice.setText("");
        txtTitle.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        TravelDeal td = getTravelDeal();
        if(td == null){
            Toast.makeText(this, "all fields must be set", Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.push().setValue(td);
        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
        clean();
    }

    private TravelDeal getTravelDeal() {
        String title = txtTitle.getText().toString();
        String price = txtPrice.getText().toString();
        String description = txtDescription.getText().toString();
        if(title.trim().isEmpty() && description.trim().isEmpty() && price.trim().isEmpty()){
            return null;
        }
        return new TravelDeal(title, description, price, deal.getImageUrl(), deal.getImageName());
    }


    public void initializeFields(){
        txtDescription = findViewById(R.id.txt_description);
        txtPrice = findViewById(R.id.txt_price);
        txtTitle = findViewById(R.id.txt_title);
        btnUpload = findViewById(R.id.upload_btn);
        img = findViewById(R.id.image_deal);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "insert picture"), UPLOAD_IMAGE);
            }
        });
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    public void showImage(String url){
        if(url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(img);
        }
    }

}
