package com.example.firebasedemo;

import java.net.URL;
import java.util.ArrayList;

public class Boutique {

    ArrayList<Produit> produits;

    boolean hasUrl;
    String imageUrl;
    String color;
    String name;

    Boutique(){}

    Boutique(boolean hasUrl, String imageUrl, String color, String name){
        this.hasUrl=hasUrl;
        this.imageUrl=imageUrl;
        this.color=color;
        this.name=name;
    }


    public boolean isHasUrl() {
        return hasUrl;
    }

    public void setHasUrl(boolean hasUrl) {
        this.hasUrl = hasUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    void addToArrayList(Produit produit){
        this.produits.add(produit);
    }

}
