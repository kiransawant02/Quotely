package com.project.quotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;

public class ConnectUsActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_us);

        toolbar=findViewById(R.id.connect_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Connect with us");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                //.addGroup("Developer Profile")
                .setImage(R.drawable.connect)
                .setDescription("Let's Connect")
                .addEmail("sawantk389" + "@gmail.com")
                .addFacebook("kiransawantt")
                .addTwitter("Kiran_Sawantt")
                .addInstagram("kiran_s02")
                .create();

        setContentView(aboutPage);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ConnectUsActivity.this,MainActivity.class));
        finish();
    }
}