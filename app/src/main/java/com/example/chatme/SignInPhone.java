package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chatme.Models.Users;
import com.example.chatme.databinding.ActivitySignInPhoneBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignInPhone extends AppCompatActivity {
    ActivitySignInPhoneBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog loadingBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        loadingBar= new ProgressDialog(this);
        auth=FirebaseAuth.getInstance();
        binding.sendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber=binding.phoneNumber.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    binding.phoneNumber.setError("please enter phone number");
                }
                else{
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait while we are verifying your phone number");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(auth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(SignInPhone.this)                 // Activity (for callback binding)
                                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.phoneNumber.setVisibility(View.INVISIBLE);
                binding.sendVerificationCode.setVisibility(View.INVISIBLE);

                String verificationCode = binding.verificationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    binding.verificationCode.setError("Please enter the code");
                }
                else {
                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Please wait while we are verifying the code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(SignInPhone.this, "Invalid Phone Number, Please enter correct phone number with your country code", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();
                Toast.makeText(SignInPhone.this, "Code has been sent to your phone number", Toast.LENGTH_SHORT).show();
                binding.verificationCode.setVisibility(View.VISIBLE);
                binding.verify.setVisibility(View.VISIBLE);
                binding.phoneNumber.setVisibility(View.INVISIBLE);
                binding.sendVerificationCode.setVisibility(View.INVISIBLE);
            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(SignInPhone.this, "Sign in succesfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(SignInPhone.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(SignInPhone.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }
}