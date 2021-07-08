package com.saikalyandaroju.whatsappclone.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class MyApplication extends Application {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static Boolean online=false;

    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new GoogleEmojiProvider());
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        registerActivityLifecycleCallbacks(new AppLifeCyccleTracker());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.i("token", "not succesful");
                    return;
                }
                Log.i("token", task.getResult().getToken());
                sharedPreferences = getApplicationContext().getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("deviceToken", task.getResult().getToken()).apply();
                sendToServer(task.getResult().getToken().toString());

            }
        });

    }


    private void sendToServer(String s) {
        if (FirebaseAuth.getInstance().getUid() != null) {
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).update("deviceToken", s)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("check", "success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("check", "failed");
                }
            });
        }
    }

    class AppLifeCyccleTracker implements Application.ActivityLifecycleCallbacks {
        private int num=0;
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if(num==0){
                if(FirebaseAuth.getInstance().getUid()!=null) {
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).
                            update("onlineStatus", "Online");
                }
                online=true;
            }
            num++;

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            num--;
            if(num==0){
                if(FirebaseAuth.getInstance().getUid()!=null) {
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).
                            update("onlineStatus", "");
                }
                online=false;
            }

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }
}
