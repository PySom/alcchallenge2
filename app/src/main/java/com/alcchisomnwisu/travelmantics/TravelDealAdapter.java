package com.alcchisomnwisu.travelmantics;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TravelDealAdapter extends RecyclerView.Adapter<TravelDealAdapter.TravelDealViewHolder> {
    private ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mListener;
    private static final int thumbnailSize = 160;
    private Context context;
    public TravelDealAdapter(){
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        deals = FirebaseUtil.mDeals;
        mListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deals", td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size() -1);
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
        mDatabaseReference.addChildEventListener(mListener);
    }
    @NonNull
    @Override
    public TravelDealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new TravelDealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelDealViewHolder holder, int position) {
        TravelDeal td = deals.get(position);
        holder.bind(td);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class TravelDealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtPrice;
        public ImageView img;
        public TravelDealViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.rvTitle);
            txtDescription = itemView.findViewById(R.id.rvDescription);
            txtPrice = itemView.findViewById(R.id.rvPrice);
            img = itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal td){
            txtPrice.setText(td.getPrice());
            txtTitle.setText(td.getTitle());
            txtDescription.setText(td.getDescription());
            showImage(td.getImageUrl());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            TravelDeal selectedDeal = deals.get(position);
            editDeal(selectedDeal);
        }

        private void editDeal(TravelDeal sd) {
            Intent intent = new Intent(context, DealActivity.class);
            intent.putExtra(DealActivity.TRAVELDEAL_PARCE, sd);

            context.startActivity(intent);
        }

        private void showImage(String url){
            if(url != null && !url.isEmpty()){

                Picasso.with(context)
                        .load(url)
                        .resize(thumbnailSize, thumbnailSize)
                        .centerCrop()
                        .into(img);
            }
        }
    }
}
