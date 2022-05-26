package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedemo.R;
import com.example.firebasedemo.StartActivity;
import com.example.firebasedemo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainBuyerActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 0;

    Bitmap bmImg;

    TextView nameText;
    Button logOut;
    Button addPicture;
    ListView listView;

    ImageView profilePic;

    BottomNavigationView navig;

    String uid ;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_buyer);
        logOut = findViewById(R.id.logoutbutton);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainBuyerActivity.this, StartActivity.class));
                finish();
            }
        });

        profilePic = findViewById(R.id.profilePic);

        navig = findViewById(R.id.buyerNavig);
        navig.setSelectedItemId(R.id.profil);
        navig.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.produits:
                        startActivity(new Intent(getApplicationContext(), BuyerProductsActivity.class));
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

        addPicture = findViewById(R.id.addPictureButton);
        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        nameText = findViewById(R.id.profilText);

        listView = findViewById(R.id.listview);
        ArrayList<String> listElts = new ArrayList<String>();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.itemText, listElts);
        listView.setAdapter(adapter);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                nameText.setText(user.getName());
                //set the image here too
                if(user.isHasProfilePic()) {
                    getBitMapFromURL(user.getImageUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBitMapFromURL(String url) {
        AsyncTaskDownloadImage asyncTaskDownloadImage = new AsyncTaskDownloadImage();
        asyncTaskDownloadImage.execute(url);
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void uploadImage(Uri imageURI) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading image...");
        pd.show();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profilePics")
                                .child(System.currentTimeMillis()+"."+getFileExtension(imageURI));
        ref.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(this.getClass().getSimpleName(), "image uploaded : "+ imageURI.toString());
                    pd.dismiss();
                    reference.child("hasProfilePic").setValue(true);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child("imageUrl").setValue(uri.toString());
                        }
                    });
                    Toast.makeText(MainBuyerActivity.this, "upload successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainBuyerActivity.this, "upload failed", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }
        });

    }

    private String getFileExtension(Uri imageURI) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageURI));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK){
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }


    private class AsyncTaskDownloadImage extends AsyncTask<String, String, Bitmap> {

        ProgressDialog p;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            bmImg=null;
            try {
                URL ImageUrl = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bmImg = BitmapFactory.decodeStream(is, null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(profilePic!=null) {
                profilePic.setImageBitmap(bitmap);
            }
        }
    }
}