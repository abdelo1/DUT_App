package com.example.blocnote;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;

   private Button inscription;
    private Button connexion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         inscription=(Button)findViewById(R.id.btn_inscription);
         connexion = (Button)findViewById(R.id.btn_connexion);
        // App.init();
        FirebaseApp.initializeApp(this);
        inscription.setOnClickListener(register);
        connexion.setOnClickListener(connect);


    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
       user=FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null)
        {

            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            finish();
        }

    }
  OnClickListener register=new OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this,RegisterActivity.class));


        }
    };
    OnClickListener connect=new OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this,ConnectActivity.class));


        }
    };
}
