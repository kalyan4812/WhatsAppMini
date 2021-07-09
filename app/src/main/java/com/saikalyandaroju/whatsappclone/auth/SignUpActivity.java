package com.saikalyandaroju.whatsappclone.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.saikalyandaroju.whatsappclone.Activities.MainActivity;
import com.saikalyandaroju.whatsappclone.Models.User;
import com.saikalyandaroju.whatsappclone.R;
import com.squareup.picasso.Picasso;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    ShapeableImageView shapeableImageView;
    Uri imageURi;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private StorageTask storageTask;
    String downloadurl;
    EditText name;
    Button next;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        shapeableImageView = findViewById(R.id.userImgView);
        name = findViewById(R.id.nameEt);
        next = findViewById(R.id.nextBtn);
        sharedPreferences = getApplicationContext().getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        storageReference = com.google.firebase.storage.FirebaseStorage.getInstance().getReference("uploads");
        firebaseFirestore = FirebaseFirestore.getInstance();
        shapeableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosephoto(v);
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sn = s.toString();
                if (sn.length() > 0) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = name.getText().toString();

                if (uname.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferences.edit().putString("username",uname).apply();
                   /*    downloadurl = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_24dp).toString();
                    }*/
                   User user = new User(uname, downloadurl, FirebaseAuth.getInstance().getUid(), "", "Hey there I am Using WhatsApp!",
                           sharedPreferences.getString("deviceToken", ""), downloadurl,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                    firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getUid().toString()).set(user).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                }
            }
        });
    }

    public void choosephoto(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageURi = data.getData();
            Log.i("data", imageURi.toString());
            Picasso.get().load(imageURi).fit().centerCrop().into(shapeableImageView);
            uploadImage(imageURi);
        }
    }

    private void uploadImage(Uri imageURi) {
        if (imageURi != null) {
            final StorageReference mstorageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageURi));
            storageTask = mstorageReference.putFile(imageURi);
            // Task<Uri> urlTask =
            storageTask.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return mstorageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadurl = task.getResult().toString();
                            }
                        }
                    });
                }
            });

        }

    }

    // to get type of file jpg/mp4/mp3 etc..

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
