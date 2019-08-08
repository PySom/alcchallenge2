package com.alcchisomnwisu.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


    }

    private void showDeals() {
        FirebaseUtil.openFireBaseUtils("traveldeals", this);
        rcView = findViewById(R.id.rc_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,
                false);
        TravelDealAdapter adapter = new TravelDealAdapter();
        rcView.setLayoutManager(layoutManager);
        rcView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_item_menu, menu);
        MenuItem item = menu.findItem(R.id.item_list_insert);
        Boolean canInsert = FirebaseUtil.isAdmin;
        if(canInsert != null){
            if(canInsert){
                item.setVisible(true);
            }
            else{
                item.setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_list_insert:
                showInsert();
                return true;
            case R.id.user_logout:
                logout();
                return true;
             default:
                 return super.onOptionsItemSelected(item);
        }

    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUtil.attachListener();
                    }
                });
        FirebaseUtil.detachListener();

    }

    private void showInsert() {
        Intent intent = new Intent(this, DealActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDeals();
        FirebaseUtil.attachListener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
