package com.hitesh.codeforces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.ContentProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.loader.ResourcesLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.hitesh.codeforces.contest.Result;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FragmentManager manager;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ArrayList<Result> results;
    ContestFragment contestFragment;
    UserFragment userFragment;
    QuestionFragment questionFragment;
    public final static String SHARED_PREF = "sharedPref";
    public final static String CONTEST = "contest";
    public final static String USER = "user";
    public final static String QUESTION = "question";
    public static final String DOWNLOAD_LINK = "https://mitrukahitesh.github.io/apkdownload/Codeforces%20Contest.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        manager = getSupportFragmentManager();
        setUpNavigationDrawer();
        navigationView.setCheckedItem(R.id.contests);
        restoreData();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment5, contestFragment).commit();
    }

    private void setUpNavigationDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.contests:
                        manager.beginTransaction().addToBackStack(null).replace(R.id.fragment5, contestFragment).commit();
                        break;
                    case R.id.questions:
                        manager.beginTransaction().addToBackStack(null).replace(R.id.fragment5, questionFragment).commit();
                        break;
                    case R.id.user:
                        manager.beginTransaction().addToBackStack(null).replace(R.id.fragment5, userFragment).commit();
                        break;
                    case R.id.share:
                        shareApp();
                        break;
                    case R.id.rate:
                        rateUs();
                        break;
                    case R.id.developer:
                        contactMe();
                        break;
                    case R.id.report:
                        reportBug();
                        break;
                }
                return true;
            }
        });
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Download the Codeforces Contest app from the link below:\n\n" + DOWNLOAD_LINK);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    private void rateUs() {
        Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void contactMe() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/mitrukahitesh"));
        startActivity(Intent.createChooser(intent, "Select Browser"));
    }

    private void reportBug() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hitesh9031@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "BUG: Codeforces Contests");
        startActivity(Intent.createChooser(intent, "Select email app"));
    }

    @Override
    protected void onStop() {
        String s1 = contestFragment.getLastResponse();
        String s2 = userFragment.getLastResponse();
        String s3 = questionFragment.getLastResponse();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONTEST, s1);
        editor.putString(USER, s2);
        editor.putString(QUESTION, s3);
        editor.commit();
        super.onStop();
    }

    public void restoreData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        contestFragment = new ContestFragment(sharedPreferences.getString(CONTEST, null));
        userFragment = new UserFragment(sharedPreferences.getString(USER, null));
        questionFragment = new QuestionFragment(sharedPreferences.getString(QUESTION, null));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if(contestFragment != null) {
                if(contestFragment.isVisible()) {
                    navigationView.setCheckedItem(R.id.contests);
                    return;
                }
            }
            if(questionFragment != null) {
                if(questionFragment.isVisible()) {
                    navigationView.setCheckedItem(R.id.questions);
                    return;
                }
            }
            if(userFragment != null) {
                if(userFragment.isVisible()) {
                    navigationView.setCheckedItem(R.id.user);
                }
            }
        }
    }
}