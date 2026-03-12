package com.example.talksy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button button;
    EditText email, password;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();

        button = findViewById(R.id.button);
        email = findViewById(R.id.editTextLogEmail);
        password = findViewById(R.id.editTextLogPassword);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String pass = password.getText().toString();

                if((TextUtils.isEmpty(Email))){
                    Toast.makeText(Login.this,"Enter The Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(Login.this,"Enter The Password", Toast.LENGTH_SHORT).show();

                } else if (!Email.matches(emailPattern)) {
                    email.setError("Give Proper Email Address");
                } else if (password.length()<6) {
                    password.setError("More then six characters");
                    Toast.makeText(Login.this,"Password Needs To Be Longer Than six Characters", Toast.LENGTH_SHORT).show();
                }else{
                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                try{
                                    Intent intent = new Intent(Login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } catch (Exception e) {
                                    Toast.makeText(Login.this,e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }else{
                                Toast.makeText(Login.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }



            }
        });
    }
}