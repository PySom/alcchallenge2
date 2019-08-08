package com.alcchisomnwisu.travelmantics;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelDeal implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    private String imageName;

    public TravelDeal(String title, String description, String price, String imageUrl, String imageName) {
        //this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
        this.setImageName(imageName);
    }

    public TravelDeal(Parcel parcel){
        id = parcel.readString();
        title = parcel.readString();
        description = parcel.readString();
        price = parcel.readString();
        imageUrl = parcel.readString();
        imageName = parcel.readString();
    }
    public TravelDeal(){}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(imageUrl);
        parcel.writeString(imageName);
    }

    public static final Creator CREATOR = new Creator<TravelDeal>(){

        @Override
        public TravelDeal createFromParcel(Parcel parcel) {
            return new TravelDeal(parcel);
        }

        @Override
        public TravelDeal[] newArray(int i) {
            return new TravelDeal[i];
        }
    };
}
