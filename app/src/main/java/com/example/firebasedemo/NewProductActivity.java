package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NewProductActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 0;

    TextView title;
    EditText productName;
    EditText productPrice;
    String imageUrl;
    ImageView imageView;
    boolean hasUrl;
    Button confirm;
    Button addImage;

    CheckedTextView meublePick;
    CheckedTextView artPick;
    CheckedTextView autrePick;

    private String uid;
    private DatabaseReference boutiqueReference;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        autrePick = findViewById(R.id.checkedBottom);
        meublePick = findViewById(R.id.checkedMiddle);
        artPick = findViewById(R.id.checkedTop);

        title = findViewById(R.id.NewProductTextView);
        title.setText(getIntent().getStringExtra("title"));

        productName = findViewById(R.id.editProductName);
        productPrice = findViewById(R.id.editProductPrice);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boutiqueReference = FirebaseDatabase.getInstance().getReference().child("boutiques").child(uid);
        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        imageView = findViewById(R.id.product1Image);

        addImage = findViewById(R.id.product1addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        //retrieve info if we're just modifying existing product
        if(title.getText().toString().equals("Modifier le produit")) {
            boutiqueReference.child("products").child(getIntent().getStringExtra("name")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()) {
                        Produit product = task.getResult().getValue(Produit.class);
                        if (product.getType().equals("meuble")) {
                            meublePick.setChecked(true);
                        } else if (product.getType().equals("art")) {
                            artPick.setChecked(true);
                        } else autrePick.setChecked(true);
                        productName.setText(product.getName());
                        //récupérer l'image
                        if (product.getImageUrl() != null) {
                            getBitMapFromURL(product.getImageUrl());
                        }
                    }
                    else Toast.makeText(NewProductActivity.this, "Impossible de récupérer les informations du produit", Toast.LENGTH_SHORT).show();
                }
            });
        }

        confirm = findViewById(R.id.confirmTemplate1);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("hasUrl",hasUrl);
                map.put("imageUrl",imageUrl);
                map.put("type", meublePick.isChecked()?"meuble": artPick.isChecked()?"art":"autre");
                map.put("price", productPrice.getText().toString()+"€");
                map.put("name", productName.getText().toString());
                boutiqueReference.child("products").child(productName.getText().toString()).setValue(map);
                finish();
            }
        });

        artPick.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                artPick.setChecked(true);
                meublePick.setChecked(false);
                autrePick.setChecked(false);
            }
        });
        meublePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artPick.setChecked(false);
                meublePick.setChecked(true);
                autrePick.setChecked(false);
            }
        });

        autrePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artPick.setChecked(false);
                meublePick.setChecked(false);
                autrePick.setChecked(true);
            }
        });


    }

    private void getBitMapFromURL(String url) {
        NewProductActivity.AsyncTaskDownloadImage asyncTaskDownloadImage = new NewProductActivity.AsyncTaskDownloadImage();
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
                    pd.dismiss();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            hasUrl=true;
                            imageUrl = uri.toString();
                            Toast.makeText(NewProductActivity.this, imageUrl, Toast.LENGTH_SHORT).show();

                            getBitMapFromURL(uri.toString());
                        }
                    });
                    Toast.makeText(NewProductActivity.this, "upload successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NewProductActivity.this, "upload failed", Toast.LENGTH_LONG).show();
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
            Bitmap bmImg=null;
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
            if(imageView!=null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}