package com.example.sih_pothole.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sih_pothole.MainActivity;
import com.example.sih_pothole.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneET;
    private Button signinBT;
    private String number="";

    @Override
    protected void onStart() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent i=new Intent(LoginActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LinearLayout linsig=findViewById(R.id.linsig);

        linsig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this, SignUpDetailsActivity.class);
               // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });


        signinBT=findViewById(R.id.signinBT);
        phoneET=findViewById(R.id.phoneET);


        signinBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        number=phoneET.getText().toString().trim();

        if(number.length()==10) {
            DatabaseReference df= FirebaseDatabase.getInstance().getReference().child("profiles").child(number);

            df.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

if(dataSnapshot.exists())
{
    Intent i = new Intent(LoginActivity.this, OTPActivity.class);
    i.putExtra("number", number);
    startActivity(i);
}
else {
    Toast.makeText(LoginActivity.this, "Create a Account to login", Toast.LENGTH_SHORT).show();
}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }else {
            Toast.makeText(LoginActivity.this, "enter a valid mobile number", Toast.LENGTH_SHORT).show();
        }

            }
        });


    }
}
