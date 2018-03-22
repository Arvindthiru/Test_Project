package com.example.rahul.test_project;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.*;

public class Setup_Activity extends AppCompatActivity {

    private CircleImageView setupimage;
    private boolean ischanged = false;
    private Uri mainImageURI = null;
    private String userid;
    private EditText setupname;
    private Button setupbtn;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar setupprogress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_);

        android.support.v7.widget.Toolbar setuptoolbar= findViewById(R.id.setup_toolbar);
        setSupportActionBar(setuptoolbar);
        getSupportActionBar().setTitle("Account setup");



        firebaseAuth = FirebaseAuth.getInstance();

        userid = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore= FirebaseFirestore.getInstance();

        setupimage = findViewById(R.id.setup_image);
        setupname = findViewById(R.id.setup_name);
        setupbtn=findViewById(R.id.setup_button);
        setupprogress= findViewById(R.id.setup_progress);

        setupprogress.setVisibility(View.VISIBLE);
        setupbtn.setEnabled(false);

        firebaseFirestore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists())
                    {
                        Toast.makeText(Setup_Activity.this,"Data exists", Toast.LENGTH_LONG).show();

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("Image");

                        mainImageURI= Uri.parse(image);

                        setupname.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.download);
                        Glide.with(Setup_Activity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupimage);
                    }
                    else
                    {
                        Toast.makeText(Setup_Activity.this,"Data doesn't exists", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    String error = task.getException().getMessage();
                    Toast.makeText(Setup_Activity.this,"FireStore Retrieve error:" + error, Toast.LENGTH_LONG).show();
                }

                setupprogress.setVisibility(View.INVISIBLE);
                setupbtn.setEnabled(true);

            }
        });


        setupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = setupname.getText().toString();
                if (!TextUtils.isEmpty(username) && mainImageURI != null) {
                setupprogress.setVisibility(View.VISIBLE);

                if(ischanged)
                {
                    userid = firebaseAuth.getCurrentUser().getUid();
                    StorageReference image_path = storageReference.child("profile_iamges").child(userid + ".jpg");
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                storeFirestore(task, username);


                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(Setup_Activity.this, "Error : " + error, Toast.LENGTH_LONG).show();
                                setupprogress.setVisibility(View.INVISIBLE);
                            }


                        }
                    });


                }
                else
                {
                    storeFirestore(null,username);
                }
            }


            }
        });

        setupimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(Setup_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(Setup_Activity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Setup_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else{
                        BringImagePicker();
                    }
                }
                else
                {
                    BringImagePicker();
                }

            }
        });

    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String username) {

        Uri download_uri;

        if(task !=null)
        {
            download_uri = task.getResult().getDownloadUrl();
        }
        else
        {
            download_uri = mainImageURI;
        }


        //Toast.makeText(Setup_Activity.this,"Image is Uploaded",Toast.LENGTH_LONG).show();
        Map<String, String> userMap =new HashMap<>();
        userMap.put("name",username);
        userMap.put("Image", download_uri.toString());
        firebaseFirestore.collection("Users").document(userid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(Setup_Activity.this,"The user Settings are Updated", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(Setup_Activity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }
                else
                {
                    String error = task.getException().getMessage();
                    Toast.makeText(Setup_Activity.this,"FireStore error:" + error, Toast.LENGTH_LONG).show();

                }

                setupprogress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void BringImagePicker() {

        Toast.makeText(Setup_Activity.this,"You already Have Permission",Toast.LENGTH_LONG).show();
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(Setup_Activity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupimage.setImageURI(mainImageURI);

                ischanged=true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
