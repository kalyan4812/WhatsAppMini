package com.saikalyandaroju.whatsappclone.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.saikalyandaroju.whatsappclone.R;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    TextView verifyTv, counter, waiting;
    EditText otp;
    Button verification, resend;
    String verificationId;
    String number;
    CountDownTimer countDownTimer;
    PhoneAuthProvider phoneAuthProvider;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        verifyTv = findViewById(R.id.verifyTv);
        waiting = findViewById(R.id.waitingTv);
        otp = findViewById(R.id.sentcodeEt);
        counter = findViewById(R.id.counterTv);
        verification = findViewById(R.id.verificationBtn);
        verification.setEnabled(false);
        resend = findViewById(R.id.resendBtn);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);


        verifyTv.append("Verify " + getIntent().getStringExtra("number"));
        number = getIntent().getStringExtra("number");
        waiting.append(getIntent().getStringExtra("number") + " " + "Wrong Number ?");
        setHighLightedText(waiting, "Wrong Number ?");
        showTimer(60000);
        sendotp(number);
        resend.setVisibility(View.GONE);
        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.show();
                String uotp = otp.getText().toString();
                if (otp != null && otp.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.dismiss();
                                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                                startActivity(i);
                                finish();
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                            } else {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to Verify", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = s.toString();
                if (code.length() == 6) {
                    verification.setEnabled(true);
                    resend.setEnabled(false);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimer(60000);
                resend.setEnabled(false);
                otp.setText("");
                sendotp(number);
            }
        });


    }

    private void sendotp(final String phone_s) {
        progressBar.show();

        phoneAuthProvider = PhoneAuthProvider.getInstance();


        mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationId = s;
              //  verification.setEnabled(true);
                progressBar.dismiss();
                Log.i("info", "codesent");


            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    otp.setText(code);
                //    verification.setEnabled(true);
                    progressBar.dismiss();
                    Log.i("info", "pasted");

                }

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                resend.setVisibility(View.VISIBLE);
                resend.setEnabled(true);
                progressBar.dismiss();
                Log.i("info", e.getMessage());

            }
        };
        phoneAuthProvider.verifyPhoneNumber(phone_s, 60, TimeUnit.SECONDS, OtpActivity.this, mcallback);

    }

    private void showTimer(int i) {
        resend.setEnabled(false);
        countDownTimer = new CountDownTimer(i, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter.setVisibility(View.VISIBLE);
                counter.setText("Seconds Remaining :" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                resend.setVisibility(View.VISIBLE);
                resend.setEnabled(true);
                counter.setVisibility(View.GONE);
                verification.setEnabled(false);
                progressBar.dismiss();


            }
        };
        countDownTimer.start();
    }

    public void setHighLightedText(TextView tv, String textToHighlight) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight, 0);
        Spannable wordToSpan = new SpannableString(tv.getText());


        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                // you can change or add more span as per your need
                wordToSpan.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        // finish();
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                }, ofe, ofe + textToHighlight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // set size
                // wordToSpan.setSpan(new ForegroundColorSpan(Color.RED), ofe, ofe + textToHighlight.length(), 0);// set color
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                tv.setMovementMethod(LinkMovementMethod.getInstance()); // important for span to be clickable.
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
