package com.project.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout emailField,passField;
    private Button loginBtn;
    private TextView signupLink,forgotPassLink;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        mProgress=new ProgressDialog(this);

        emailField=findViewById(R.id.login_email);
        passField=findViewById(R.id.login_password);
        loginBtn=findViewById(R.id.login_button);
        signupLink=findViewById(R.id.login_signup_link);
        forgotPassLink=findViewById(R.id.login_forgot_pass_link);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (!isNetworkAvailable(getApplicationContext())){
                    Toast.makeText(LoginActivity.this, "Internet not available", Toast.LENGTH_SHORT).show();
                }else{
                    verifyDetails();
                }
            }
        });

        forgotPassLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
                finish();
            }
        });
    }

    private void verifyDetails() {
        String email=emailField.getEditText().getText().toString().trim();
        String pass=passField.getEditText().getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError("Not a valid email address!");
            emailField.requestFocus();
            return;
        }else{
            emailField .clearFocus();
            emailField.setErrorEnabled(false);
        }

        if (pass.isEmpty()){
            passField.setError("Please enter password to login");
            passField.requestFocus();
            return;
        }else{
            passField.clearFocus();
            passField.setErrorEnabled(false);
        }

        mProgress.setTitle("Logging In");
        mProgress.setMessage("please wait...");

        loginUser(email,pass);
    }

    private void loginUser(String email, String pass) {
        mProgress.show();

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    goToMainActivity();
                }else{
                    if (task.getException() instanceof NetworkErrorException){
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "No existing account found, please register!", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
                    }else{
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        finish();
    }

    private void goToMainActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra("login_type","email");
        startActivity(intent);
        finish();
    }
}