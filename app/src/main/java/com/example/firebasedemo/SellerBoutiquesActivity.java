package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SellerBoutiquesActivity extends AppCompatActivity {


    Button addBoutique;



    private String uid;
    private DatabaseReference boutiqueReference;
    private DatabaseReference userReference;

    BottomNavigationView navig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_boutiques);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boutiqueReference = FirebaseDatabase.getInstance().getReference().child("boutiques").child(uid);
        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        addBoutique = findViewById(R.id.addBoutique);

        userReference.child("hasBoutique").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if((boolean) task.getResult().getValue()){
                    addBoutique.setText("Modifier la boutique");
                }
                else {
                    addBoutique.setText("Ajouter une boutique");
                }
            }
        });

        addBoutique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerBoutiquesActivity.this, NewBoutiqueActivity.class);
                userReference.child("hasBoutique").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if((boolean) task.getResult().getValue()){
                            intent.putExtra("title","Modifier la boutique");
                            startActivity(intent);
                        }
                        else {
                            intent.putExtra("title","Créer une nouvelle boutique");
                            startActivity(intent);
                        }
                    }
                });
            }
        });



        navig = findViewById(R.id.sellerNavig);
        navig.setSelectedItemId(R.id.boutiques);
        navig.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profil:
                        startActivity(new Intent(getApplicationContext(), MainSellerActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.commandes:
                        startActivity(new Intent(getApplicationContext(), SellerCommandesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

}