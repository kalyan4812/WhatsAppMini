package com.saikalyandaroju.whatsappclone.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.saikalyandaroju.whatsappclone.R;
import com.saikalyandaroju.whatsappclone.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate();
            }
        },3000);

    }

    private void navigate() {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        else {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
}
