package com.example.keuanganq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    DatabaseReference reference;
    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username_register);
        etEmail = findViewById(R.id.et_email_register);
        etPassword = findViewById(R.id.et_password_register);
        Button btnMasuk = findViewById(R.id.btn_masuk_register);
        Button btnLogin=findViewById(R.id.btn_login_register);


        //pindah ke halaman register
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(login);
            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //simpan ke local storage dulu
                SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(username_key, etUsername.getText().toString());
                editor.apply();

                //menyimpan ke database
                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(etUsername.getText().toString());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("username").setValue(etUsername.getText().toString());
                        dataSnapshot.getRef().child("email").setValue(etEmail.getText().toString());
                        dataSnapshot.getRef().child("password").setValue(etPassword.getText().toString());
                        dataSnapshot.getRef().child("uang_skrg").setValue("0");

                        Intent home = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(home);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });

    }
}
