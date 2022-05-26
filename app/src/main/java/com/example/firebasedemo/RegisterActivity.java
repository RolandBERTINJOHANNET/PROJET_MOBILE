package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    EditText name;
    Button confirm;

    CheckedTextView sellerCheck;
    CheckedTextView buyerCheck;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        sellerCheck = findViewById(R.id.vendeurCheck);
        sellerCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sellerCheck.isChecked()){
                    sellerCheck.setChecked(false);
                    buyerCheck.setChecked(true);
                }
                else if(!sellerCheck.isChecked()){
                    sellerCheck.setChecked(true);
                    buyerCheck.setChecked(false);
                }
            }
        });
        buyerCheck = findViewById(R.id.clientCheck);
        buyerCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buyerCheck.isChecked()){
                    buyerCheck.setChecked(false);
                    sellerCheck.setChecked(true);
                }
                else if(!buyerCheck.isChecked()){
                    buyerCheck.setChecked(true);
                    sellerCheck.setChecked(false);
                }
            }
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(email.getText().toString())||
                   TextUtils.isEmpty(password.getText().toString())||
                   TextUtils.isEmpty(name.getText().toString())||
                   incorrectCheckboxes()){
                    Toast.makeText(RegisterActivity.this, "veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(name.getText().toString(), email.getText().toString(),password.getText().toString(), isSeller());
                }
            }
        });
    }

    private boolean incorrectCheckboxes() {
        return !(buyerCheck.isChecked()||sellerCheck.isChecked());
    }

    private boolean isSeller() {
        return sellerCheck.isChecked();
    }

    private void registerUser(String name, String email, String password, boolean is_seller) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Vous êtes inscrit. Veuillez vous connecter.", Toast.LENGTH_LONG).show();
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("email",email);
                    map.put("Password",password);
                    map.put("Name",name);
                    map.put("hasProfilePic",false);
                    map.put("imageUrl", "");
                    map.put("seller",is_seller);
                    map.put("hasBoutique",false);
                    FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).setValue(map);
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this, "échec de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}