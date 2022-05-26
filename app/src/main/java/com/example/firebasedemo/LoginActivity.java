package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button confirm;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(email.getText().toString())||TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(LoginActivity.this, "veuillez remplir les champs", Toast.LENGTH_SHORT).show();
                }
                else{
                    loginUser(email.getText().toString(),password.getText().toString());
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Vous êtes connecté", Toast.LENGTH_SHORT).show();
                    String uid = task.getResult().getUser().getUid();
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()) {
                                User user = task.getResult().getValue(User.class);
                                if (user.isSeller()) {
                                    startActivity(new Intent(LoginActivity.this, MainSellerActivity.class));
                                }
                                if (!user.isSeller()) {
                                    startActivity(new Intent(LoginActivity.this, MainBuyerActivity.class));
                                }
                                setResult(1);
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "échec de la connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}