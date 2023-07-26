package com.project.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText feedback;
    private Button sendBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseUser curUser;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mAuth=FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference().child("feedback");

        mProgress=new ProgressDialog(this);
        mProgress.setMessage("sending feedback..");
        mProgress.setCanceledOnTouchOutside(false);

        curUser=mAuth.getCurrentUser();


        toolbar=findViewById(R.id.feedback_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        feedback=findViewById(R.id.feedback_text);
        sendBtn=findViewById(R.id.feedback_send_button);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyInputs();
            }
        });
    }

    private void verifyInputs() {
        String feed=feedback.getText().toString().trim();
        if (feed.length()<4){
            feedback.setError("Not a valid feedback length!");
            feedback.requestFocus();
            return;
        }
        mProgress.show();

        if (curUser==null){
            mProgress.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            builder.setTitle("Please login in the app to send feedback to us!");
            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(FeedbackActivity.this,LoginActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            HashMap<String,String> map=new HashMap<>();
            map.put("name",curUser.getDisplayName());
            map.put("feedback_text",feed);
            map.put("email",curUser.getEmail());

            long time=System.currentTimeMillis();
            String curTime=Long.toString(time);

            mRef.child(curTime).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgress.dismiss();
                    if (task.isSuccessful()){
                        Toast.makeText(FeedbackActivity.this, "feedback sent successfully!", Toast.LENGTH_LONG).show();
                        feedback.setText("");
                        feedback.requestFocus();

                    }else{
                        Toast.makeText(FeedbackActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}