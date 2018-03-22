package com.example.rahul.test_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login_Activity extends AppCompatActivity {

    private EditText loginEmailtext;
    private EditText loginPasstext;
    private Button loginbtn;
    private Button loginRegbtn;
    private ProgressBar login_Progress;
    private FirebaseAuth mAuth;

    private ProgressBar loginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        mAuth= FirebaseAuth.getInstance();
        loginEmailtext=(EditText) findViewById(R.id.reg_email);
        loginPasstext=(EditText) findViewById(R.id.reg_password);
        loginbtn=(Button) findViewById(R.id.login_button);
        loginRegbtn=(Button) findViewById(R.id.login_account_new);
        login_Progress=(ProgressBar) findViewById(R.id.loginProgress);

        loginRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent regIntent = new Intent(Login_Activity.this,Register_Activity.class);
                startActivity(regIntent);

            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String loginEmail = loginEmailtext.getText().toString();
                String loginPass= loginPasstext.getText().toString();



                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass))
                {

                    login_Progress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                sendtoMain();

                            }
                            else
                            {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(Login_Activity.this, "Error:"+ errorMessage, Toast.LENGTH_SHORT).show();
                            }

                            login_Progress.setVisibility(View.INVISIBLE);

                        }
                    });
                }



            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser= mAuth.getCurrentUser();

        if(currentUser != null){

            sendtoMain();
        }
    }

    private void sendtoMain() {

        Intent mainIntent = new Intent(Login_Activity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}
