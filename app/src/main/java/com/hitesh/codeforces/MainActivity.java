package com.hitesh.codeforces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView i1, i2, i3;
    TextView t1, t2, t3;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        i1 = (ImageView) findViewById(R.id.contest);
        i2 = (ImageView) findViewById(R.id.person);
        i3 = (ImageView) findViewById(R.id.question);
        t1 = (TextView) findViewById(R.id.contest_t);
        t2 = (TextView) findViewById(R.id.person_t);
        t3 = (TextView) findViewById(R.id.question_t);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpNavigationDrawer();
        restoreData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setImageListeners();
            }
        }).start();
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
                    case R.id.reminder:
                        getReminders();
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

    private void getReminders() {
        Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void shareApp() {
        Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void rateUs() {
        Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void contactMe() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/mitrukahitesh"));
        startActivity(intent);
    }

    private void reportBug() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hitesh9031@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "BUG: Codeforces Contests");
        startActivity(intent);
    }

    public void setImageListeners() {
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i1.setImageResource(R.drawable.contest_red);
                t1.setVisibility(View.VISIBLE);
                i2.setImageResource(R.drawable.person);
                i3.setImageResource(R.drawable.question);
                t2.setVisibility(View.GONE);
                t3.setVisibility(View.GONE);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment5, contestFragment).commit();
            }
        });
        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i1.setImageResource(R.drawable.contest);
                i2.setImageResource(R.drawable.person_red);
                t2.setVisibility(View.VISIBLE);
                t3.setVisibility(View.GONE);
                t1.setVisibility(View.GONE);
                i3.setImageResource(R.drawable.question);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment5, userFragment).commit();
            }
        });
        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i1.setImageResource(R.drawable.contest);
                i2.setImageResource(R.drawable.person);
                i3.setImageResource(R.drawable.question_red);
                t3.setVisibility(View.VISIBLE);
                t1.setVisibility(View.GONE);
                t2.setVisibility(View.GONE);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment5, questionFragment).commit();
            }
        });
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
        }
    }
}