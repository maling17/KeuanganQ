package com.example.keuanganq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {


    DatabaseReference reference;
    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);


        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            //ambil data dari firebase
                            String passwordFromFirebase = dataSnapshot.child("password").getValue().toString();

                            //validasi password jika berhasil lanjut ke home
                            if (password.equals(passwordFromFirebase)) {

                                //simpan ke local storage dulu
                                SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(username_key, etUsername.getText().toString());
                                editor.apply();

                                Intent Login = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(Login);
                            } else {
                                Toast.makeText(getApplicationContext(), "Password Salah", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Data tidak ada", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(Register);
            }
        });


    }
}
