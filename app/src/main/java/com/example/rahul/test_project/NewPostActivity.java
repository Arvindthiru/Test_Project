package com.example.rahul.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

public class NewPostActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar newposttoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newposttoolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newposttoolbar);
        getSupportActionBar().setTitle("Add New Post");

    }
}
