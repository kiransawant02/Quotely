package com.project.quotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {
    private boolean doubleTap=false;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        constraintLayout=findViewById(R.id.home_constraint);
    }

    public void goToLoginActivity(View view) {
        Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void goToSignupActivity(View view) {
        Intent intent=new Intent(HomeActivity.this,RegistrationActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        if (doubleTap){
            super.onBackPressed();
        }
        else {
            Snackbar backSnakbar=Snackbar.make(constraintLayout,"Press back twice to exit Quotely app",Snackbar.LENGTH_SHORT);
            backSnakbar.show();
            doubleTap=true;
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap=false;
                }
            },1000); //one second
        }
    }

    public void goToMainActivity(View view) {
        Intent intent=new Intent(HomeActivity.this,MainActivity.class);
        intent.putExtra("login_type","guest");
        startActivity(intent);
        finish();
    }
}