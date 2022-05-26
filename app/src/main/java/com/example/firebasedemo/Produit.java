package com.example.firebasedemo;

import java.net.MalformedURLException;
import java.net.URL;

public class Produit {

    String price;
    Boolean hasUrl;
    String ImageUrl;
    String type;
    String name;

    Produit(){}

    public String getPrice() {
        return price;
    }


    public void setPrice(String prix) {
        this.price = prix;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Boolean getHasUrl() {
        return hasUrl;
    }

    public void setHasUrl(Boolean hasUrl) {
        this.hasUrl = hasUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
