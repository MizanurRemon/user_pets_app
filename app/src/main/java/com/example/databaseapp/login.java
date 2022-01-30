package com.example.databaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {


   private Button btnRegister;


    private EditText Editemail, Editpassword;
    private Button login;
    private Button register;
    private Boolean correctInfo = false;
    private FirebaseDatabase mFirebaseDatabase;

    private ProgressBar progressBar;


    FirebaseAuth mAuth;


    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), register.class);
            startActivity(intent);
        }

    };


    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean info = registerUser();
      //      if (info == true) {
        //        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
         //       startActivity(intent);
         //   }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
        Editemail = (EditText) findViewById(R.id.LoginTextEmailAddress);
        Editpassword = (EditText) findViewById(R.id.LoginTextPassword);
        btnRegister = findViewById(R.id.login_act_register);

        login.setOnClickListener(loginListener);
        progressBar = (ProgressBar) findViewById(R.id.main_progressBar1);
        btnRegister.setOnClickListener(registerListener);

    }


    private boolean registerUser() {


        String email = Editemail.getText().toString().trim();
        String password = Editpassword.getText().toString().trim();


        if (email.isEmpty()) {
            Editemail.setError("Email is required");
            Editemail.requestFocus();

            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Editemail.setError("Please provide valid email");
            Editemail.requestFocus();

            return false;
        }
        else if (password.isEmpty()) {
            Editpassword.setError("Password is required");
            Editpassword.requestFocus();
            return false;

        } else if (password.length() < 6) {
            Editpassword.setError("Your password length should be above 6 characters");
            Editpassword.requestFocus();
            return false;
        }



      //  progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(login.this, MainActivity.class);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser mAuthCurrentUser = mAuth.getCurrentUser();
                    if (mAuthCurrentUser != null) {
                        Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_SHORT).show();
                      //  progressBar.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();
                    }

                  //  ep = 1;
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong password or email", Toast.LENGTH_SHORT).show();
                   // progressBar.setVisibility(View.GONE);
                    //ep = 0;
                }
            }
        });


            return true;
    }

    @Override
    protected void onStart() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.main_progressBar1);
        progressBar.setVisibility(View.VISIBLE);
        super.onStart();
        Log.e("TAG", "ONStart");
    }

    @Override
    protected void onResume(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.main_progressBar1);

        super.onResume();
        Log.e("TAG", "ONResume");
        progressBar.setVisibility(View.INVISIBLE);
    }

}





