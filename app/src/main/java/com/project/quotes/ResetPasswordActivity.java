package com.project.quotes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailField;
    private FirebaseAuth mAuth;
    private Button resetButton;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth=FirebaseAuth.getInstance();
        emailField=findViewById(R.id.reset_email);
        resetButton=findViewById(R.id.reset_button);
        mProgress=new ProgressDialog(this);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email=emailField.getText().toString().trim();

        if (email.isEmpty()){
            emailField.setError("Email id required!");
            emailField.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError("Not a valid email address!");
            emailField.requestFocus();
            return;
        }
        mProgress.setTitle("Please wait..");
        mProgress.setMessage("Sending email to reset your password");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(ResetPasswordActivity.this,"Sent, Check your Email",Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this,R.style.AlertDialogTheme);
                    builder.setTitle("Mail sent");
                    builder.setMessage("Mail not received? send email again!");
                    builder.setPositiveButton("Send Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetPassword();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.setNeutralButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                    mProgress.dismiss();
                    Toast.makeText(ResetPasswordActivity.this,"There is no user with this email, Please SignUP",Toast.LENGTH_LONG).show();
                }else{
                    mProgress.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}