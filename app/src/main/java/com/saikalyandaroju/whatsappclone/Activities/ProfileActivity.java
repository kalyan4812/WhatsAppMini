package com.saikalyandaroju.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.NestedScrollView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.saikalyandaroju.whatsappclone.Models.User;
import com.saikalyandaroju.whatsappclone.R;
import com.saikalyandaroju.whatsappclone.auth.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    EditText displayNameEt, nameEt, mobileEt;
    CircleImageView userPicIv;


    String imgPath;
    Bitmap bitmap;
    AppCompatImageView pickImage;
    Button save;
    NestedScrollView nestedScrollView;
    Button logout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Uri imageuri;
    boolean namechanged = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initPrefrences();
        setUpIds();
        bindData();

        displayNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                displayNameEt.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                namechanged = true;
                displayNameEt.setCursorVisible(true);
                save.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logout.setVisibility(View.GONE);
                save(view);


            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setVisibility(View.VISIBLE);
                openPicker(v);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();

            }
        });

    }

    private void setUpIds() {
        displayNameEt = findViewById(R.id.display_name);
        nameEt = findViewById(R.id.ed_name);
        nameEt.setEnabled(false);
        mobileEt = findViewById(R.id.ed_phone);
        mobileEt.setEnabled(false);
        userPicIv = findViewById(R.id.userImgView);
        pickImage = findViewById(R.id.openCamera);
        logout = findViewById(R.id.logout);
        save = findViewById(R.id.save);
        nestedScrollView = findViewById(R.id.scrollView);
    }

    private void save(View view) {
        setUpProgressDialog();
        displayNameEt.setCursorVisible(false);
        save.setText("Please wait...");
        editor.putString("display_name", displayNameEt.getText().toString()).apply();
        editor.putString("profile_pic", imgPath).apply();
        if (imageuri != null) {
            uploadImage(imageuri);
        }
         if(namechanged) {
             updateNameInFirebase(displayNameEt.getText().toString());
         }

        progressDialog.dismiss();
        save.setVisibility(View.GONE);
        logout.setVisibility(View.VISIBLE);


    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void updateNameInFirebase(String name) {
        if(namechanged)
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid().toString()).update("name", name);

    }

    private void initPrefrences() {
        sharedPreferences = getApplicationContext().getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void bindData() {
        mobileEt.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        nameEt.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        displayNameEt.setText(sharedPreferences.getString("display_name", ""));

        if (!sharedPreferences.getString("profile_pic", "").equals("")) {
            String image = sharedPreferences.getString("profile_pic", "");
            Log.i("check", image);

           /* BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeFile(image, options);
            userPicIv.setImageBitmap(bitmap);
            userPicIv.setScaleType(ImageView.ScaleType.CENTER_CROP);*/
            Picasso.get().load(image).placeholder(R.drawable.ic_user_pic).error(R.drawable.ic_user_pic).into(userPicIv);

        } else {
            Log.i("check", "empty image");
        }


    }

    public void openPicker(View v) {
        ImagePicker.Companion.with(this)
                .cropSquare()            //Crop image(Optional), Check Customization for more option
                .compress(1024)      //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                //.saveDir(new File(Environment.getExternalStorageDirectory(), "CricFrik"))
                .galleryMimeTypes(
                        new String[]{
                                "image/png",
                                "image/jpg",
                                "image/jpeg"
                        }
                )
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imgPath = data.getData().getPath();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeFile(imgPath, options);
                userPicIv.setImageBitmap(bitmap);
                userPicIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageuri = data.getData();


            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Unable to pick the image.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Unable to pick the image.", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImage(Uri imageURi) {
        if (imageURi != null) {
            final StorageReference mstorageReference = com.google.firebase.storage.FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageURi));
            StorageTask storageTask = mstorageReference.putFile(imageURi);
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
                                editor.putString("profile_pic", task.getResult().toString()).apply();
                                updateImageInFirestore(task.getResult().toString());

                            }
                        }
                    });
                }
            });

        }

    }

    private void updateImageInFirestore(String imgurl) {


        User user = new User();
        user.setImageUrl(imgurl);
        user.setThumbImage(imgurl);


        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid().toString()).update("thumbImage",imgurl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Profile Dp Updated succesfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to update..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}