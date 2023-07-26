package com.project.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavouriteShayariActivity extends AppCompatActivity {
    private TextView loginText, zeroShayariText;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseUser curUser;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ProgressDialog mProgress;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_shayari);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        loginText = findViewById(R.id.fav_text_login);
        loginBtn = findViewById(R.id.fav_button_login);
        toolbar = findViewById(R.id.favourite_shayari_app_bar);
        zeroShayariText = findViewById(R.id.fav_shayari_text);

        recyclerView=findViewById(R.id.fav_recycler_view);
        recyclerView.setHasFixedSize(true);

        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading data...");
        mProgress.setMessage("please wait");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favourite Quotes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavouriteShayariActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (curUser == null) {
            loginText.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            mProgress.dismiss();
        } else {

            String uid = curUser.getUid();
            mRef = FirebaseDatabase.getInstance().getReference().child(uid).child("favourite_shayari");
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        zeroShayariText.setVisibility(View.VISIBLE);
                        mProgress.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Model>()
                    .setQuery(mRef, Model.class)
                    .build();


            FirebaseRecyclerAdapter<Model, NotesViewHolder> firebaseRecyclerAdapter
                    = new FirebaseRecyclerAdapter<Model, NotesViewHolder>(options) {

                @Override
                protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull Model model) {
                    final String postKey = getRef(position).getKey();

                    holder.setTopic(model.getTopic());
                    holder.setShayari(model.getShayari());
                }

                @NonNull
                @Override
                public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    mProgress.dismiss();

                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_recycler_layout, parent, false);
                    NotesViewHolder viewHolder = new NotesViewHolder(view);
                    return viewHolder;
                }
            };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        }

    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public NotesViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setTopic(String t) {
            TextView topic = mView.findViewById(R.id.fav_rec_topic);
            topic.setText(t);
        }

        public void setShayari(String s) {
            TextView shayari = mView.findViewById(R.id.fav_rec_shayari_text);
            shayari.setText(s);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FavouriteShayariActivity.this, MainActivity.class));
        finish();
    }
}