package com.example.talksy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import android.net.Uri;

public class registration extends AppCompatActivity {

    TextView rgloginbut;
    EditText rgusername , rgEmail , rgPassword , rgrePassword;
    Button signupbutton ;
    CircleImageView rg_profilerg0;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    Uri imageURI ;
    String imageuri;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        rgloginbut = findViewById(R.id.loginbut);
        rgusername = findViewById(R.id.rgusername);
        rgEmail = findViewById(R.id.rgEmail);
        rgPassword = findViewById(R.id.rgPassword);
        rgrePassword = findViewById(R.id.rgrePassword);
        rg_profilerg0 = findViewById(R.id.profilerg0);
        signupbutton = findViewById(R.id.signupbutton);

        // Firebase initialize
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        rgloginbut.setOnClickListener(v -> {
            Intent intent = new Intent(registration.this, Login.class);
            startActivity(intent);
            finish();
        });

        signupbutton.setOnClickListener(v -> {

            String name = rgusername.getText().toString().trim();
            String email = rgEmail.getText().toString().trim();
            String Password = rgPassword.getText().toString().trim();
            String rePassword = rgrePassword.getText().toString().trim();
            String status = "Hey I'm Using This Application";

            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(Password) || TextUtils.isEmpty(rePassword)){

                Toast.makeText(registration.this,"Please Enter Valid Information",Toast.LENGTH_SHORT).show();

            }
            else if(!email.matches(emailPattern)){
                rgEmail.setError("Enter Valid Email");
            }
            else if(Password.length()<6){
                rgPassword.setError("Password Must Be 6 Characters");
            }
            else if(!Password.equals(rePassword)){
                rgrePassword.setError("Password Doesn't Match");
            }
            else{

                auth.createUserWithEmailAndPassword(email,Password)
                        .addOnCompleteListener(task -> {

                            if(task.isSuccessful()){

                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);

                                if(imageURI!=null){

                                    storageReference.putFile(imageURI)
                                            .addOnCompleteListener(task1 -> {

                                                if(task1.isSuccessful()){

                                                    storageReference.getDownloadUrl()
                                                            .addOnSuccessListener(uri -> {

                                                                imageuri = uri.toString();

                                                                Users user = new Users(id,name,email,Password,rePassword,imageuri,status);

                                                                reference.setValue(user)
                                                                        .addOnCompleteListener(task2 -> {

                                                                            if(task2.isSuccessful()){

                                                                                Intent intent = new Intent(registration.this, MainActivity.class);
                                                                                startActivity(intent);
                                                                                finish();

                                                                            }else{
                                                                                Toast.makeText(registration.this,"Error Creating User",Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        });

                                                            });

                                                }

                                            });

                                }
                                else{

                                    imageuri = "default";

                                    Users user = new Users(id,name,email,Password,rePassword,imageuri,status);

                                    reference.setValue(user).addOnCompleteListener(task12 -> {

                                        if(task12.isSuccessful()){

                                            Intent intent = new Intent(registration.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }else{
                                            Toast.makeText(registration.this,"Error Creating User",Toast.LENGTH_SHORT).show();
                                        }

                                    });

                                }

                            }
                            else{
                                Toast.makeText(registration.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                            }

                        });

            }

        });

        rg_profilerg0.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10 && resultCode==RESULT_OK){

            if(data!=null){

                imageURI = data.getData();
                rg_profilerg0.setImageURI(imageURI);

            }

        }

    }

}