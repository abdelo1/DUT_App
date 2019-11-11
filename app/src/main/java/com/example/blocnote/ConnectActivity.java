package com.example.blocnote;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressBar progressBar;
    private Button btn;
    private EditText user_email,user_password;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private TextView mdpoublie;
    private TextView pasde;
    private TextView inscription;
    private ImageView profil;
   private String urlProfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        mAuth=FirebaseAuth.getInstance();
        progressBar=(ProgressBar) findViewById(R.id.progress);

        inscription=(TextView) findViewById(R.id.pasdecompte);
        user_email=(EditText)findViewById(R.id.email);
        user_password=(EditText)findViewById(R.id.mdp);
        btn=(Button) findViewById(R.id.connexion);
        mdpoublie=(TextView) findViewById(R.id.mdpOublie);
        pasde=(TextView)findViewById(R.id.pasdecompte);
        pasde.setOnClickListener(this);
        btn.setOnClickListener(this);
        mdpoublie.setOnClickListener(this);
        inscription.setOnClickListener(this);



    }

    private void signIn( String email, String password) {
        final ProgressDialog progress=new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Connexion..");
        progress.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Intent intent = new Intent(ConnectActivity.this, ProfileActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            finish();
                        }

                        else

                        {

                            // If sign in fails, display a message to the user.
                            Toast.makeText(ConnectActivity.this, "Connexion impossible veuillez reessayer ",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }


                });

    }


    private boolean validateForm() {
        boolean valid = true;

        email =user_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            user_email.setError("Obligatoire");
            valid = false;
        } else {
            user_email.setError(null);
        }



        password = user_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            user_password.setError("Obligatoire");
            valid = false;
        } else {
            user_password.setError(null);
        }


        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connexion:
                if (validateForm()){
                    signIn(email,password);
                }
                break;
            case R.id.pasdecompte:
                startActivity(new Intent(this,RegisterActivity.class));
                finish();
                break;
            case  R.id.mdpOublie:
                email =user_email.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    user_email.setError("Entrez votre email pour cette operation");

                } else {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConnectActivity.this,"Veuillez consulter votre boite email enfin de redefinir votre mot de passe",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    mAuth.setLanguageCode("fr");
                }
                break;
        }
    }

    }

