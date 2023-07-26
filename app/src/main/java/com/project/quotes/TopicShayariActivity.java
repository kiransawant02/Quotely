package com.project.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class TopicShayariActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private FirebaseAuth mAuth;
    private InterstitialAd mInterstitialAd;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private StringBuffer topicNameText;
    private String topic, curShayari;
    private int curIndex = 0;
    private boolean isCurShayariFav = false;
    private String uniqueText = "";
    private TextView shayariText, watermark;
    private ImageView shayariImage;
    private Button prevButton, nextButton, moreButton, favButton, saveButton, copyButton, shareButton;
    private Toolbar toolbar;
    private ProgressDialog mProgress;
    private RelativeLayout relativeLayout;
    private Animation leftAnim, rightAnim;
    private ArrayList<String> attitude, love, dosti, zindagi, funny, sad, good_morning, good_night, birthday,sigma,
            new_year, suggestions ;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_shayari);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mProgress = new ProgressDialog(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        prevButton = findViewById(R.id.topic_shayari_prev_button);
        nextButton = findViewById(R.id.topic_shayari_next_button);
        favButton = findViewById(R.id.topic_shayari_fav_button);
        moreButton = findViewById(R.id.topic_shayari_more_button);
        shayariImage = findViewById(R.id.topic_shayari_image);
        shayariText = findViewById(R.id.topic_shayari_text);
        saveButton = findViewById(R.id.savebtn);
        copyButton = findViewById(R.id.topic_shayari_copy_text);
        shareButton = findViewById(R.id.topic_shayari_share);
        relativeLayout = findViewById(R.id.topic_relative_layout);

        topicNameText = new StringBuffer(getIntent().getStringExtra("topic_name"));
        topic = new String(topicNameText);
        topicNameText.replace(0, 1, Character.toString(topicNameText.charAt(0)).toUpperCase());
        toolbar = findViewById(R.id.topic_shayari_app_bar);

        leftAnim = AnimationUtils.loadAnimation(this, R.anim.left_anim);
        rightAnim = AnimationUtils.loadAnimation(this, R.anim.right_anim);
        shayariImage.setAnimation(leftAnim);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(topicNameText + " Quotes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        shayariImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditActivity();
            }
        });


        suggestions = new ArrayList<>();
        suggestions.add("Emotional empathy is what motivates us to help others");
        suggestions.add("Make the decision, make another. Remake one past, you cannot.");
        suggestions.add("Life itself is the proper binge.");
        suggestions.add("Reading without reflecting is like eating without digesting.");

        attitude = new ArrayList<>();
        attitude.add("Positive thinking will let you do everything better than negative thinking.");
        attitude.add("The most important thing you’ll ever wear is your attitude.");
        attitude.add("A positive attitude may not solve all our problems but that is the only option we have if we want to get out of problems.");
        attitude.add("All your dreams can come true f you have the courage to purse them    ");
        attitude.add("If you don’t like something, change it. If you can’t change it, change your attitude.");
        attitude.add("A positive attitude is a person's passport to a better tomorrow.");
        attitude.add("The only disability in life is a bad attitude.");
        attitude.add("To be an overachiever you have to be an over-believer");
        System.out.println("Attitude " + attitude.size());

        love = new ArrayList<>();
        love.add("Love is not love until love’s vulnerable.");
        love.add("A purpose of human life, no matter who is controlling it, is to love whoever is around to be loved.");
        love.add("Romance is the glamour which turns the dust of everyday life into a golden haze.");
        love.add("One word frees us of all the weight and pain of life: that word is love.");
        System.out.println("Love " + love.size());

        dosti = new ArrayList<>();
        dosti.add("The most beautiful discovery true friends make is that they can grow separately without growing apart");
        dosti.add("Life is partly what we make it, and partly what it is made by the friends we choose.");
        System.out.println("Dosti " + dosti.size());


        zindagi = new ArrayList<>();
        zindagi.add("Life is a journey, not a destination.");
        zindagi.add("The purpose of our lives is to be happy");
        zindagi.add("The only way to do great work is to love what you do");
        System.out.println("Zindagi " + zindagi.size());

        funny = new ArrayList<>();
        funny.add("Behind every great man is a woman rolling her eyes.");
        funny.add("I finally realized that people are prisoners of their phones, which is why it's called a 'cell' phone.");
        funny.add("Why do they call it rush hour when nothing moves? ");
        System.out.println("Funny " + funny.size());

        sad = new ArrayList<>();
        sad.add("Tears are words that need to be written.");
        sad.add("We must embrace pain and burn it as fuel for our journey");
        sad.add("It's sad when someone you know becomes someone you knew.");
        sad.add("Sometimes the memories are worth the pain.");
        sad.add("The saddest thing about betrayal is that it never comes from your enemies.");
        System.out.println("Sad " + sad.size());

        good_morning = new ArrayList<>();
        good_morning.add("With the new day comes new strength and new thoughts.");
        good_morning.add("Our greatest glory is not in never falling, but in rising every time we fall.");
        good_morning.add("First thing every morning before you arise, say out loud, 'I believe,' three times.   ");
        good_morning.add("What you do today can improve all your tomorrows.");
        good_morning.add("");
        System.out.println("Good Morning " + good_morning.size());

        sigma = new ArrayList<>();
        sigma.add("With the new day comes new strength and new thoughts.");
        sigma.add("Our greatest glory is not in never falling, but in rising every time we fall.");
        sigma.add("First thing every morning before you arise, say out loud, 'I believe,' three times.   ");
        sigma.add("What you do today can improve all your tomorrows.");
        sigma.add("");
        System.out.println("Good Morning " + sigma.size());

        good_night = new ArrayList<>();
        good_night.add("Good night, sleep tight, don't let the bedbugs bite");
        good_night.add("Sleep is the golden chain that binds health and our bodies together");
        good_night.add("As the night falls, may you find comfort and solace in the peacefulness of the darkness. Good night");
        good_night.add("दGood night, sweet dreams, and may your tomorrow be even better than today" );
        System.out.println("Good Night " + good_night.size());

        birthday = new ArrayList<>();
        birthday.add("May your birthday be filled with sunshine, rainbows, and all the blessings life has to offer. Happy birthday!");
        birthday.add("Another year of amazing adventures and beautiful memories. Happy birthday, my dear friend");
        birthday.add("Age is merely the number of years the world has been enjoying you.");
        System.out.println("Birthday " + birthday.size());

        new_year = new ArrayList<>();
        new_year.add("Cheers to a new year and another chance for us to get it right");
        new_year.add("Every new beginning comes from some other beginning's end ");
        System.out.println("New Year " + new_year.size());

        ArrayList<String> shayariList = null;
        if (topic.equals("suggestions")) {
            shayariList = suggestions;
            shayariImage.setImageResource(R.drawable.bg3);
        } else if (topic.equals("attitude")) {
            shayariList = attitude;
            shayariImage.setImageResource(R.drawable.peakyb);
        } else if (topic.equals("love")) {
            shayariList = love;
            shayariImage.setImageResource(R.drawable.love);
        } else if (topic.equals("dosti")) {
            shayariList = dosti;
            shayariImage.setImageResource(R.drawable.friendsbg1);
        }else if (topic.equals("sigma")) {
            shayariList = sigma;
            shayariImage.setImageResource(R.drawable.gym2);
        } else if (topic.equals("zindagi")) {
            shayariList = zindagi;
            shayariImage.setImageResource(R.drawable.lifebg1);
        } else if (topic.equals("funny")) {
            shayariList = funny;
            shayariImage.setImageResource(R.drawable.funny);
        } else if (topic.equals("sad")) {
            shayariList = sad;
            shayariImage.setImageResource(R.drawable.alone);
        } else if (topic.equals("good_morning")) {
            shayariList = good_morning;
            shayariImage.setImageResource(R.drawable.morning);
        } else if (topic.equals("good_night")) {
            shayariList = good_night;
            shayariImage.setImageResource(R.drawable.night);
        } else if (topic.equals("birthday")) {
            shayariList = birthday;
            shayariImage.setImageResource(R.drawable.birthday);
        } else if (topic.equals("new_year")) {
            shayariList = new_year;
            shayariImage.setImageResource(R.drawable.newyr1);
        }

        final int[] index = {0};
        shayariText.setText(shayariList.get(index[0]));
        curShayari = shayariList.get(index[0]);
        curIndex = index[0];
        uniqueText = topic + "_" + curIndex;

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            mRef = mDatabase.getReference().child(uid).child("favourite_shayari").child(uniqueText);

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        isCurShayariFav = true;
                        int imgResource = R.drawable.ic_favorite_24;
                        favButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                        favButton.setCompoundDrawableTintList(getColorStateList(R.color.button_text_color));
                        favButton.setText("Remove Fav");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        final ArrayList<String> finalShayariList = shayariList;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButtonFunction();
                shayariImage.startAnimation(rightAnim);
                //shayariText.startAnimation(rightAnim);
                index[0]++;
                if (index[0] > finalShayariList.size() - 1) {
                    index[0] = 0;
                }
                shayariText.setText(finalShayariList.get(index[0]));
                curShayari = finalShayariList.get(index[0]);
                curIndex = index[0];
                uniqueText = topic + "_" + curIndex;


                if (mAuth.getCurrentUser() != null) {
                    String uid = mAuth.getCurrentUser().getUid();
                    mRef = mDatabase.getReference().child(uid).child("favourite_shayari").child(uniqueText);

                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                isCurShayariFav = true;
                                int imgResource = R.drawable.ic_favorite_24;
                                favButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                favButton.setCompoundDrawableTintList(getColorStateList(R.color.button_text_color));
                                favButton.setText("Remove fav");
                            } else {
                                isCurShayariFav = false;
                                int imgResource = R.drawable.ic_favorite_border_24;
                                favButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                favButton.setCompoundDrawableTintList(getColorStateList(R.color.button_text_color));
                                favButton.setText("Add to fav");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shayariImage.startAnimation(leftAnim);
                //shayariText.startAnimation(leftAnim);
                index[0]--;
                if (index[0] < 0) {
                    index[0] = finalShayariList.size() - 1;
                }
                shayariText.setText(finalShayariList.get(index[0]));
                curShayari = finalShayariList.get(index[0]);
                curIndex = index[0];
                uniqueText = topic + "_" + curIndex;

                if (mAuth.getCurrentUser() != null) {
                    String uid = mAuth.getCurrentUser().getUid();
                    mRef = mDatabase.getReference().child(uid).child("favourite_shayari").child(uniqueText);

                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                isCurShayariFav = true;
                                int imgResource = R.drawable.ic_favorite_24;
                                favButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                favButton.setCompoundDrawableTintList(getColorStateList(R.color.button_text_color));
                                favButton.setText("Remove fav");
                            } else {
                                isCurShayariFav = false;
                                int imgResource = R.drawable.ic_favorite_border_24;
                                favButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                favButton.setCompoundDrawableTintList(getColorStateList(R.color.button_text_color));
                                favButton.setText("Add to fav");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditActivity();
            }
        });
        shayariImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditActivity();
            }
        });


        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();

                if (mAuth.getCurrentUser() == null) {
                    mProgress.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(TopicShayariActivity.this, R.style.AlertDialogTheme);
                    builder.setTitle("To add Quotes to favorite you need login to the app!");
                    builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(TopicShayariActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    Toast.makeText(TopicShayariActivity.this, "Please login to add Quotes to favourites!", Toast.LENGTH_SHORT).show();
                } else {
                    String uid = mAuth.getCurrentUser().getUid();
                    mRef = mDatabase.getReference().child(uid).child("favourite_shayari");

                    if (isCurShayariFav) {
                        mRef.child(uniqueText).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mProgress.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(TopicShayariActivity.this, "Quote removed from Favourite!", Toast.LENGTH_SHORT).show();
                                    isCurShayariFav = false;
                                    int imgResource = R.drawable.ic_favorite_border_24;
                                    favButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                    favButton.setCompoundDrawableTintList(getColorStateList(R.color.button_text_color));
                                    favButton.setText("Add to fav");
                                } else {
                                    Toast.makeText(TopicShayariActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        HashMap<String, String> map = new HashMap<>();
                        map.put("topic", topic);
                        map.put("shayari", curShayari);
                        mRef = mRef.child(uniqueText);
                        mRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mProgress.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(TopicShayariActivity.this, "Quote added to favourite!", Toast.LENGTH_SHORT).show();
                                    isCurShayariFav = true;
                                    int imgResource = R.drawable.ic_favorite_24;
                                    favButton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                    favButton.setCompoundDrawableTintList(getColorStateList(R.color.button_text_color));
                                    favButton.setText("Remove fav");
                                } else {
                                    Toast.makeText(TopicShayariActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            goToEditActivity();
                        }
                    });
                } else {
                    goToEditActivity();
                }
            }
        });
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Shayari", curShayari);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TopicShayariActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TopicShayariActivity.this,R.style.AlertDialogTheme);
                builder.setNegativeButton("Save image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(TopicShayariActivity.this, "Image saved in gallery", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                continueImageSaving(view);
            }

            private void continueImageSaving(View view) {
                v=view;
                if (checkPermission()) {
                    Bitmap bitmap=Bitmap.createBitmap(relativeLayout.getWidth(),relativeLayout.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas=new Canvas(bitmap);
                    relativeLayout.draw(canvas);

                    Bitmap icon = bitmap;
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.TITLE, "Your Shayari");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    MediaStore.Images.Media.insertImage(getContentResolver(), icon, topic+" shayari", "yourDescription");
                    //getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    //Toast.makeText(EditActivity.this, "Image saved in gallery", Toast.LENGTH_LONG).show();

                } else {
                    requestPermission();
                }
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v = view;
                if (checkPermission()) {
                    share();

                } else {
                    requestPermission();
                }
            }
        });

    }


    private void nextButtonFunction() {
    }

    private void prevButtonFunction() {
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        //int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED /*&& result1 == PackageManager.PERMISSION_GRANTED */;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    //boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted /*&& cameraAccepted*/)
                        Snackbar.make(v, "Permission Granted, Now you can access internal storage.", Snackbar.LENGTH_LONG).show();
                    else {

                        Snackbar.make(v, "Permission Denied, You cannot access internal storage.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to storage permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(TopicShayariActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void share() {
        Bitmap bitmap = Bitmap.createBitmap(relativeLayout.getWidth(), relativeLayout.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        relativeLayout.draw(canvas);

        Bitmap icon = bitmap;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.TITLE, "Your Shayari");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        //MediaStore.Images.Media.insertImage(getContentResolver(), icon, topic+" shayari", "yourDescription");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image using:"));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void goToEditActivity() {
        Intent intent = new Intent(TopicShayariActivity.this, EditActivity.class);
        intent.putExtra("cur_shayari", curShayari);
        intent.putExtra("topic", topic);
        startActivity(intent);
        //Toast.makeText(TopicShayariActivity.this, "Jald aa raha :-)", Toast.LENGTH_SHORT).show();
    }
}