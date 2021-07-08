package com.saikalyandaroju.whatsappclone.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hbb20.CountryCodePicker;
import com.saikalyandaroju.whatsappclone.R;

public class LoginActivity extends AppCompatActivity {
    EditText number;
    Button next;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryCodePicker = findViewById(R.id.ccp);
        number = findViewById(R.id.phoneNumberEt);
        countryCodePicker.registerCarrierNumberEditText(number);
        next = findViewById(R.id.nextBtn);
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String num = s.toString();
                if (s.length() == 10) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
                Log.i("change", "changed" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryCodePicker.isValidFullNumber()) {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(LoginActivity.this).setMessage("Proceed for Verification of number ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getApplicationContext(), OtpActivity.class);
                                    i.putExtra("number", countryCodePicker.getFullNumberWithPlus());
                                    startActivity(i);
                                    finish();

                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setCancelable(false);
                    alertDialogBuilder.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter a Valid Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
