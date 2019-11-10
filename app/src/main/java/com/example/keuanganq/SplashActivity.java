package com.example.keuanganq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getUsernameLocal();
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");

        if (username_key_new.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //setelah loading maka akan langsung berpindah ke home activity
                    Intent login = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();

                }
            }, 3000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //setelah loading maka akan langsung berpindah ke home activity
                    Intent home = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(home);
                    finish();

                }
            }, 3000);

        }
    }

}
