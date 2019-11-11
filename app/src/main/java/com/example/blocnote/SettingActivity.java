package com.example.blocnote;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blocnote.model.UserClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    FirebaseUser fuser;
    Uri uriImageSelected;
    DatabaseReference reference,another,freference;
    ImageView img;
    EditText inputName;
    EditText inputEmail;
    TextView classe;
    Toolbar toolbar;
    AlertDialog dialog;
    FloatingActionButton floatButton;
    Button enreg;

    UserClass muser;
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth=FirebaseAuth.getInstance();
        fuser=mAuth.getCurrentUser();


        img=(ImageView)findViewById(R.id.imageView3);
        inputEmail=(EditText)findViewById(R.id.inputEmail);
        inputName=(EditText)findViewById(R.id.inputName);
        classe=(TextView)findViewById(R.id.classe);
        floatButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        enreg=(Button)findViewById(R.id.enregistrer);
        toolbar=findViewById(R.id.toolbar);

        floatButton.setOnClickListener(this);
        enreg.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Paramètres");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id)
        {
            case R.id.enregistrer:
                if (!muser.getNom().equals(inputName.getText().toString()))
                {
                    reference.child("nom").setValue(inputName.getText().toString());
                }
                if (!fuser.getEmail().equals(inputEmail.getText().toString()))
                {

                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(inputEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                       Toast.makeText(SettingActivity.this,"Email changé avec succès",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(SettingActivity.this,"Une erreur s'est produite veuillez reéssayer",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                break;
            case R.id.floatingActionButton:

                // create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Options");
                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_view_dialog, null);
                builder.setView(customLayout);
                 TextView supp=(TextView)customLayout.findViewById(R.id.supprimer);
                 TextView changer=(TextView)customLayout.findViewById(R.id.change) ;
                 supp.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         reference.child("imageUrl").setValue("default");
                         dialog.dismiss();
                     }
                 });
                 changer.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                       methodRequiresPermission();
                       dialog.dismiss();
                     }
                 });
                // create and show the alert dialog
                dialog = builder.create();
                dialog.show();
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    private void methodRequiresPermission() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, "Autorisez vous Mogo a acceder a votre camera ?", RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 6 - Calling the appropriate method after activity result
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .into(this.img);
                uploadPhotoInFirebase();

            } else {
                Toast.makeText(this, "Impossible de charger la photo veuillez reessayer", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void uploadPhotoInFirebase() {
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("profilePic").child(uuid);
        mImageRef.putFile(uriImageSelected)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful());
                        Uri downloadUrl = urlTask.getResult();
                        final String url=downloadUrl.toString();
                        reference.child("imageUrl").setValue(url);
                         freference= FirebaseDatabase.getInstance().getReference("Users");

                         freference.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 for (DataSnapshot snapshot:dataSnapshot.getChildren())
                                 {
                                     UserClass user=snapshot.getValue(UserClass.class);
                                    another =FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                another.child("urlphoto").setValue(url);

                                }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                         });
                    }
                });


    }


    @Override
    protected void onResume() {
        super.onResume();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("isonline").setValue("true");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                muser= dataSnapshot.getValue(UserClass.class);
                if (!muser.getImageUrl().equals("default"))
                {
                    Glide.with(getApplicationContext()).load(Uri.parse(muser.getImageUrl())).into(img);
                }
                else
                    img.setImageResource(R.drawable.user_logo);
                inputEmail.setText(fuser.getEmail());
                inputEmail.setSelection(fuser.getEmail().length());
                inputName.setText(muser.getNom());
                inputName.setSelection(muser.getNom().length());
                classe.setText(muser.getFiliere());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("isonline").setValue("false");
        inputEmail.setText("");
        inputEmail.setSelection(0);
        inputName.setText("");
        inputName.setSelection(0);
        classe.setText("");


    }


}
