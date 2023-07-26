package com.project.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String loginType=null;
    SharedPreferences shrd;
    private FirebaseUser curUser;
    private TextView headerName, headerEmail, categoriesText;
    boolean doubleTap = false;
    private InterstitialAd mInterstitialAd, favIntAdd, subIntAdd;
    private View v;
    private Toolbar mToolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    private Animation sideAnim, bottomAnim;
    GridLayout gridLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        MobileAds.initialize(this);

        shrd=getSharedPreferences("login_type",MODE_PRIVATE);
        SharedPreferences.Editor editor=shrd.edit();
        loginType=getIntent().getStringExtra("login_type");

        if (loginType!=null && loginType.equals("guest")){
            editor.putString("login_type","guest");
        }else{
            editor.putString("login_type","email");
        }
        editor.apply();


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        favIntAdd = new InterstitialAd(this);
        favIntAdd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        favIntAdd.loadAd(new AdRequest.Builder().build());

        subIntAdd = new InterstitialAd(this);
        subIntAdd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        subIntAdd.loadAd(new AdRequest.Builder().build());

        //Hooks
        mToolbar = findViewById(R.id.main_app_bar);
        gridLayout = findViewById(R.id.main_grid_layout);
        categoriesText = findViewById(R.id.main_text_categories);
        navigationView = findViewById(R.id.main_navigation_view);

        //Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Quotely");

        drawerLayout = findViewById(R.id.main_drawer_layout);

        //NavigationView
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        headerName = navView.findViewById(R.id.nav_header_name);
        headerEmail = navView.findViewById(R.id.nav_header_email);

        //Animations
        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);
        gridLayout.setAnimation(sideAnim);
        categoriesText.setAnimation(bottomAnim);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelector(menuItem);
                return false;
            }
        });
        Menu nav_Menu = navigationView.getMenu();
        if (curUser != null) {
            headerName.setText(curUser.getDisplayName());
            headerEmail.setText(curUser.getEmail());
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
        } else {
            headerEmail.setText("");
            headerName.setText("Guest User");
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        }

    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                drawerLayout.closeDrawer(Gravity.LEFT);
                Toast.makeText(getApplicationContext(), "You are on HOME!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                drawerLayout.closeDrawer(Gravity.LEFT);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                builder.setTitle("Dou you really want to logout!");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        checkUserExistence();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;

            case R.id.nav_fav:
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, FavouriteShayariActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_submit_shayari:
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent subIntent = new Intent(MainActivity.this, SubmitShayariActivity.class);
                startActivity(subIntent);
                break;

            case R.id.nav_login:
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.nav_developer:
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent devIntent = new Intent(MainActivity.this, DeveloperActivity.class);
                startActivity(devIntent);
                break;
            case R.id.nav_feedback:
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent feedIntent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(feedIntent);
                break;
            case R.id.nav_connect:
                drawerLayout.closeDrawer(Gravity.LEFT);

                Intent conIntent = new Intent(MainActivity.this, ConnectUsActivity.class);
                startActivity(conIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleTap) {
                super.onBackPressed();
            } else {
                Snackbar backSnakbar = Snackbar.make(drawerLayout, "Press back twice to exit Quotely app", Snackbar.LENGTH_SHORT);
                backSnakbar.show();
                doubleTap = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleTap = false;
                    }
                }, 1000); //one second
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExistence();
        //Snackbar snackbar=Snackbar.make(drawerLayout,"Welcome "+curUser.getDisplayName(),Snackbar.LENGTH_LONG);
        //snackbar.show();
    }


    public void goToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkUserExistence() {
        curUser = mAuth.getCurrentUser();
        String type=shrd.getString("login_type","login");
        if (curUser == null && !type.equals("guest")) {
            goToHomeActivity();
        }
    }

    public void goToTopicMenu(final View view) {

            String name_id = view.getResources().getResourceEntryName(view.getId());
            Intent intent = new Intent(MainActivity.this, TopicShayariActivity.class);
            intent.putExtra("topic_name", name_id);
            startActivity(intent);
        }
    }
