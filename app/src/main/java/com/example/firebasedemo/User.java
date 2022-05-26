package com.example.firebasedemo;

import android.net.Uri;

public class User {
    private String Name;
    private String Password;
    private String email;
    private boolean hasProfilePic;
    private String imageUrl;
    private boolean seller;
    private boolean hasBoutique;


    public User(){}

    public User(String Name, String Password, String email, boolean hasProfilePic,String imageUrl, boolean seller, boolean hasBoutique){
        this.Name = Name;
        this.Password =Password;
        this.email=email;
        this.hasProfilePic=hasProfilePic;
        this.imageUrl=imageUrl;
        this.seller = seller;
        this.hasBoutique=hasBoutique;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHasProfilePic() {
        return hasProfilePic;
    }

    public void setHasProfilePic(boolean hasProfilePic) {
        this.hasProfilePic = hasProfilePic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSeller() {
        return seller;
    }

    public void setSeller(boolean seller) {
        this.seller = seller;
    }

    public boolean isHasBoutique() {
        return hasBoutique;
    }

    public void setHasBoutique(boolean hasBoutique) {
        this.hasBoutique = hasBoutique;
    }
}
