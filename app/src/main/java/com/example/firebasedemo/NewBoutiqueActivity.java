package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class NewBoutiqueActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 0;

    TextView title;
    EditText boutiqueName;
    String imageUrl;
    ImageView imageView;
    boolean hasUrl;
    Button confirm;
    Button addImage;

    CheckedTextView redPick;
    CheckedTextView greenPick;
    CheckedTextView bluePick;

    Button addProduct;

    private String uid;
    private DatabaseReference boutiqueReference;
    private DatabaseReference userReference;

    ProductsAdapter productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_boutique);

        bluePick = findViewById(R.id.checkedBottom);
        redPick = findViewById(R.id.checkedMiddle);
        greenPick = findViewById(R.id.checkedTop);

        title = findViewById(R.id.NewBoutiqueTextView);
        title.setText(getIntent().getStringExtra("title"));

        boutiqueName = findViewById(R.id.editBoutiqueName);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boutiqueReference = FirebaseDatabase.getInstance().getReference().child("boutiques").child(uid);
        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        imageView = findViewById(R.id.boutique1Image);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        productsAdapter = new ProductsAdapter(this);
        recyclerView.setAdapter(productsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addImage = findViewById(R.id.boutique1addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        //retrieve info if we're just modifying existing boutique
        if(title.getText().toString().equals("Modifier la boutique")) {
            boutiqueReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Boutique boutique = task.getResult().getValue(Boutique.class);
                    int colorResource = getResources().getColor(R.color.blue);
                    if(boutique.getColor().equals("red")){
                        colorResource=getResources().getColor(R.color.red);
                        redPick.setChecked(true);
                    }
                    else if(boutique.getColor().equals("green")){
                        colorResource=getResources().getColor(R.color.green);
                        greenPick.setChecked(true);
                    }else bluePick.setChecked(true);
                    findViewById(R.id.newboutiquelayout).setBackgroundColor(colorResource);
                    boutiqueName.setText(boutique.getName());
                    updateBackground(boutique.getColor());
                    //récupérer l'image
                    if(boutique.getImageUrl()!=null){
                        getBitMapFromURL(boutique.getImageUrl());
                    }
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
                map.put("color",redPick.isChecked()?"red":greenPick.isChecked()?"green":"blue");
                map.put("name",boutiqueName.getText().toString());
                boutiqueReference.setValue(map);
                userReference.child("hasBoutique").setValue(true);
                finish();
            }
        });

        //set the addProduct button
        addProduct = findViewById(R.id.addProductButton);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewBoutiqueActivity.this, NewProductActivity.class);
                intent.putExtra("title","Ajout d'un produit :");
                startActivity(intent);
            }
        });

        //define the products recyclerView


        greenPick.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                findViewById(R.id.newboutiquelayout).setBackgroundColor(getResources().getColor(R.color.green));
                greenPick.setChecked(true);
                redPick.setChecked(false);
                bluePick.setChecked(false);
            }
        });
        redPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.newboutiquelayout).setBackgroundColor(getResources().getColor(R.color.red));
                greenPick.setChecked(false);
                redPick.setChecked(true);
                bluePick.setChecked(false);
            }
        });

        bluePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.newboutiquelayout).setBackgroundColor(getResources().getColor(R.color.blue));
                greenPick.setChecked(false);
                redPick.setChecked(false);
                bluePick.setChecked(true);
            }
        });

        updateRecyclerView();

    }

    private void getBitMapFromURL(String url) {
        NewBoutiqueActivity.AsyncTaskDownloadImage asyncTaskDownloadImage = new NewBoutiqueActivity.AsyncTaskDownloadImage();
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
                            Toast.makeText(NewBoutiqueActivity.this, imageUrl, Toast.LENGTH_SHORT).show();

                            getBitMapFromURL(uri.toString());
                        }
                    });
                    Toast.makeText(NewBoutiqueActivity.this, "upload successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NewBoutiqueActivity.this, "upload failed", Toast.LENGTH_LONG).show();
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

    private void updateBackground(String color) {
        switch(color){
            case "red":
                findViewById(R.id.newboutiquelayout).setBackgroundColor(getResources().getColor(R.color.red));
                break;
            default:
                break;
        }
    }


    //all for updating recyclerView

    private void updateRecyclerView(){
        boutiqueReference.child("products").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                            NewBoutiqueActivity.AsyncTaskDownloadImageToAdapter asyncTaskDownloadImage
                                    = new NewBoutiqueActivity.AsyncTaskDownloadImageToAdapter();
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