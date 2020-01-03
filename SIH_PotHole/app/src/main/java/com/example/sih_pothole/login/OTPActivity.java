package com.example.sih_pothole.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sih_pothole.MainActivity;
import com.example.sih_pothole.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {



    TextView resendotp;
    private EditText reenterphone,enterotp;
    private Button verifyotp;
    private String number="",mail="",name="";
    private FirebaseAuth auth;
    private  String verificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        number = getIntent().getExtras().getString("number");
        name = getIntent().getExtras().getString("name");
        mail = getIntent().getExtras().getString("mail");



        reenterphone = findViewById(R.id.reenterphone);
        resendotp = findViewById(R.id.resendotp);
        enterotp=findViewById(R.id.enterotp);
        auth=FirebaseAuth.getInstance();

        verifyotp = findViewById(R.id.sendotp);
        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode(enterotp.getText().toString());
            }
        });


        reenterphone.setText(number);

        sendVerificationCode(number);
        resendotp.setVisibility(View.VISIBLE);

        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendotp.setVisibility(View.GONE);
                sendVerificationCode(reenterphone.getText().toString());
                resendotp.setVisibility(View.VISIBLE);

            }
            //StartFirebaseLogin();


        });



    }


    private void sendVerificationCode(String number) {

//        String phoneNumber = number;
//        // Check phoneNumber.length()
//        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
//        try {
//            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "IN");
//            Toast.makeText(this, phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164), Toast.LENGTH_SHORT).show();
//            PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                    phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164), // Phone number to verify
//                    6, // Timeout duration
//                    TimeUnit.SECONDS, // Unit of timeout
//                    otp.this, // Activity (for callback binding)
//                    mCallbacks); // OnVerificationStateChangedCallbacks
//        } catch (NumberParseException e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            // Wrong format
//        }





        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            SignInWithCredential(phoneAuthCredential);
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                enterotp.setText(code);
                //String codenum=enterotp.getText().toString();

                verifyCode(code);


            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    private void SignInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            DatabaseReference df= FirebaseDatabase.getInstance().getReference().child("profiles").child(number);

                            df.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            if(TextUtils.isEmpty(name))
                            {
                                Intent i=new Intent(OTPActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();

                            }
                            else
                            {
                                HashMap<String,String> hashMap=new HashMap<>();
                                hashMap.put("name",name);
                                hashMap.put("number",number);
                                hashMap.put("image","");
                                hashMap.put("complaints","");
                                hashMap.put("email",mail);

                                FirebaseDatabase.getInstance().getReference().child("profiles").child(number).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Intent i=new Intent(OTPActivity.this, MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                });

                            }


                        } else {


                        }
                    }
                });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        SignInWithCredential(credential);
    }
    public boolean connectedToNetwork() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isConnected();
        }

        return false;

    }


    public void NoInternetAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You are not connected to the internet. ");
        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(connectedToNetwork()){
                    //volley();
                }else{ NoInternetAlertDialog(); }
            }
        });
        builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent openSettings = new Intent();
                openSettings.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openSettings);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
