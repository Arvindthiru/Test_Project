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

public class Register_Activity extends AppCompatActivity {

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass;
    private Button reg_btn;
    private Button reg_already_btn;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        mAuth= FirebaseAuth.getInstance();
        reg_email_field= (EditText) findViewById(R.id.reg_email);
        reg_pass_field=(EditText) findViewById(R.id.reg_password);
        reg_confirm_pass=(EditText)findViewById(R.id.reg_confirm_password);
        reg_btn = (Button) findViewById(R.id.reg_button);
        reg_already_btn=(Button) findViewById(R.id.reg_account_already);
        reg_progress=(ProgressBar)findViewById(R.id.Registration_Progress);

        reg_already_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = reg_email_field.getText().toString();
                String password = reg_pass_field.getText().toString();
                String confirm_password = reg_confirm_pass.getText().toString();


                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_password)){


                    if(password.equals(confirm_password)){
                        reg_progress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Intent setupIntent = new Intent(Register_Activity.this, Setup_Activity.class);
                                    startActivity(setupIntent);
                                    finish();
                                }

                                else
                                {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(Register_Activity.this,"Error: "+ errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else

                    {
                        Toast.makeText(Register_Activity.this,"Confirm password and password doesn't match", Toast.LENGTH_LONG).show();
                    }

                    reg_progress.setVisibility(View.INVISIBLE);

                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser !=null)
        {
            sendToMain();
        }
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(Register_Activity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
