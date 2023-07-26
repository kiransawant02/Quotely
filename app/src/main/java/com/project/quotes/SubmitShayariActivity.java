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
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SubmitShayariActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference subRef;
    private ProgressDialog mProgress;
    private EditText category,shayariText;
    private Button submitShayariBtn;
    private FirebaseUser curUser;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_shayari);

        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        subRef=mDatabase.getReference().child("user_quotes");
        curUser=mAuth.getCurrentUser();

        //toolbar
        toolbar=findViewById(R.id.submit_shayari_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Submit Quotes");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //hooks
        category=findViewById(R.id.submit_shayri_category);
        shayariText=findViewById(R.id.submit_shayari_text);
        submitShayariBtn=findViewById(R.id.submit_shayari_button);
        scrollView=findViewById(R.id.submit_scrollview);

        //Progress Dialog
        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Please Wait");
        mProgress.setMessage("Uploading your Quote to database!");
        mProgress.setCanceledOnTouchOutside(false);

        submitShayariBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyInputs();
            }
        });
    }

    private void verifyInputs() {
        String cat=category.getText().toString().trim();
        String shayari=shayariText.getText().toString().trim();

        if (cat.length()<4){
            category.setError("Not a valid category name!");
            category.requestFocus();
            return;
        }

        if (shayari.length()<10){
            shayariText.setError("Shayari is too short!");
            shayariText.requestFocus();
            return;
        }

        mProgress.show();
        if (curUser==null){
            mProgress.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            builder.setTitle("To share your shayari with us you need login to the app!");
            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(SubmitShayariActivity.this,LoginActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            long time=System.currentTimeMillis();
            String curTime=Long.toString(time);

            HashMap<String,String> map=new HashMap<>();
            map.put("category",cat);
            map.put("text",shayari);

            String uid=curUser.getUid();
            subRef.child(uid).child(curTime).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgress.dismiss();
                    if (task.isSuccessful()){
                        Toast.makeText(SubmitShayariActivity.this, "Shayari uploaded successfully!", Toast.LENGTH_SHORT).show();
                        category.setText("");
                        shayariText.setText("");
                        category.requestFocus();

                    }else{
                        Toast.makeText(SubmitShayariActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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