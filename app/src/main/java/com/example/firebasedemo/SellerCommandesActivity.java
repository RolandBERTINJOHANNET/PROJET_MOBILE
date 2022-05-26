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
import android.view.MenuItem;
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

public class SellerCommandesActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeLayout;

    BottomNavigationView navig;

    UserAdapter userAdapter;

    private String uid;
    private DatabaseReference dataBaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_commandes);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dataBaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        Toast.makeText(this, "Activité non implémentée", Toast.LENGTH_SHORT).show();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        userAdapter = new UserAdapter(this);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        navig = findViewById(R.id.buyerNavig);
        navig.setSelectedItemId(R.id.commandes);
        navig.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profil:
                        startActivity(new Intent(getApplicationContext(), MainSellerActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.boutiques:
                        startActivity(new Intent(getApplicationContext(), SellerBoutiquesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    default:
                        return true;
                }
            }
        });
        /*updateRecyclerView();

        swipeLayout = findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerView();
            }
        });

    }


    private void updateRecyclerView(){
        dataBaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for(DataSnapshot child : task.getResult().getChildren()) {
                        User user = child.getValue(User.class);

                        if(user.isSeller()){
                            String name = user.getName();
                            Bitmap bm = null;
                            if (user.isHasProfilePic()) {
                                SellerCommandesActivity.AsyncTaskDownloadImage asyncTaskDownloadImage
                                        = new SellerCommandesActivity.AsyncTaskDownloadImage();
                                asyncTaskDownloadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,user.getImageUrl(),name);
                            }else{
                                userAdapter.addItem(name, null);
                            }
                        }
                    }
                }
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private class AsyncTaskDownloadImage extends AsyncTask<String, String, ArrayList<Object>> {

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
                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bmImg = BitmapFactory.decodeStream(is, null, options);
                data.add((Object)bmImg);
                data.add((Object)name);
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
            userAdapter.addItem(name,btm);
        }
    */
    }
}