package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.firebasedemo.R;
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

public class BuyerProductsActivity extends AppCompatActivity {

    BottomNavigationView navig;
    private ProductsAdapter productsAdapter;

    private String uid;
    private DatabaseReference boutiqueReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_products);


        Toast.makeText(this, "activité partiellement implémentée", Toast.LENGTH_LONG).show();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boutiqueReference = FirebaseDatabase.getInstance().getReference().child("boutiques");

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        productsAdapter = new ProductsAdapter(this);
        recyclerView.setAdapter(productsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        navig = findViewById(R.id.buyerNavig);
        navig.setSelectedItemId(R.id.produits);
        navig.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profil:
                        startActivity(new Intent(getApplicationContext(), MainBuyerActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.commerçants:
                        startActivity(new Intent(getApplicationContext(), BuyerCommerceActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

//all for updating recyclerView

    private void updateRecyclerView(){
        boutiqueReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for(DataSnapshot child : task.getResult().getChildren()) {
                        Produit product = child.getValue(Produit.class);
                        String name = product.getName();
                        String price = product.getPrice();
                        Log.d(this.getClass().getSimpleName(),"price = "+price);
                        Bitmap bm = null;
                        if (product.getHasUrl()) {
                            BuyerProductsActivity.AsyncTaskDownloadImageToAdapter asyncTaskDownloadImage
                                    = new BuyerProductsActivity.AsyncTaskDownloadImageToAdapter();
                            asyncTaskDownloadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,product.getImageUrl(),name,price);
                        }else{
                            productsAdapter.addItem(name, price, null);
                        }
                    }
                }
            }
        });
    }

    private class AsyncTaskDownloadImageToAdapter extends AsyncTask<String, String, ArrayList<Object>> {

        ProgressDialog p;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Object> doInBackground(String... strings) {
            Bitmap bmImg=null;
            ArrayList<Object> data = new ArrayList<>();
            try {
                URL ImageUrl = new URL(strings[0]);
                String name = new String(strings[1]);
                String price = new String(strings[2]);
                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bmImg = BitmapFactory.decodeStream(is, null, options);
                data.add((Object)bmImg);
                data.add((Object)name);
                data.add((Object)price);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(ArrayList<Object> data) {
            super.onPostExecute(data);
            Bitmap btm = (Bitmap)data.get(0);
            String name = (String)data.get(1);
            String price = (String)data.get(2);
            productsAdapter.addItem(name,price,btm);
        }
    }

}
