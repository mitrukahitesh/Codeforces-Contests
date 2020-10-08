package com.hitesh.codeforces;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hitesh.codeforces.user.SearchedUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.View.GONE;

public class UserFragment extends Fragment {

    Button b;
    EditText e;
    ImageView i;
    TextView handle, rank, maxrank, lastonline, fname, lname, email, country, rating, contribution,
            friends, organization, registeredon;
    LinearLayout layout;
    String lastResponse;
    public static final String URL = "https://codeforces.com/api/user.info?handles=";

    public UserFragment() {
        // Required empty public constructor
    }

    public UserFragment(String lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getLastResponse() {
        return lastResponse;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        b = (Button) view.findViewById(R.id.search);
        e = (EditText) view.findViewById(R.id.userhandle);
        i = (ImageView) view.findViewById(R.id.avatar);
        handle = (TextView) view.findViewById(R.id.handle);
        rank = (TextView) view.findViewById(R.id.rank);
        maxrank = (TextView) view.findViewById(R.id.maxrank);
        lastonline = (TextView) view.findViewById(R.id.lastonline);
        fname = (TextView) view.findViewById(R.id.fname);
        lname = (TextView) view.findViewById(R.id.lname);
        email = (TextView) view.findViewById(R.id.email);
        country = (TextView) view.findViewById(R.id.country);
        rating = (TextView) view.findViewById(R.id.rating);
        contribution = (TextView) view.findViewById(R.id.contribution);
        friends = (TextView) view.findViewById(R.id.friends);
        organization = (TextView) view.findViewById(R.id.organization);
        registeredon = (TextView) view.findViewById(R.id.registeredon);
        layout = (LinearLayout) view.findViewById(R.id.ll2);
        if (lastResponse != null)
            setLastUserData();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInternetAvailable()) {
                    Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                final LoadingDialog dialog = new LoadingDialog(getContext());
                dialog.startLoader();
                String input = e.getText().toString();
                StringRequest request = new StringRequest(URL + input, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.substring(1, 6).equals("status")) {
                            Toast.makeText(getContext(), "Server under maintenance", Toast.LENGTH_SHORT).show();
                            dialog.dismissLoader();
                            return;
                        }
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        SearchedUser user = gson.fromJson(response, SearchedUser.class);
                        if (user.getStatus().equals("OK")) {
                            layout.setVisibility(View.VISIBLE);
                            lastResponse = response;
                            dialog.dismissLoader();
                            setLastUserData();
                        } else {
                            dialog.dismissLoader();
                            Toast.makeText(getContext(), user.getComment(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismissLoader();
                        if (error.toString().endsWith("ClientError"))
                            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "Please check internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(request);
            }
        });
        return view;
    }

    public void setLastUserData() {
        layout.setVisibility(View.VISIBLE);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SearchedUser user = gson.fromJson(lastResponse, SearchedUser.class);
        Glide.with(getContext()).load("https:" + user.getUserDetail().get(0).getTitlePhoto()).into(i);
        handle.setText(user.getUserDetail().get(0).getHandle());
        if (user.getUserDetail().get(0).getRank() == null)
            rank.setVisibility(GONE);
        else {
            rank.setVisibility(View.VISIBLE);
            rank.setText(user.getUserDetail().get(0).getRank());
        }
        if (user.getUserDetail().get(0).getMaxRank() == null)
            maxrank.setVisibility(GONE);
        else {
            maxrank.setVisibility(View.VISIBLE);
            maxrank.setText("(max: " + user.getUserDetail().get(0).getMaxRank() + ")");
        }
        Date date = new Date((long) user.getUserDetail().get(0).getLastOnlineTimeSeconds() * (long) 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        lastonline.setText("Last Online: " + formattedDate);
        if (user.getUserDetail().get(0).getFirstName() == null)
            fname.setVisibility(GONE);
        else {
            fname.setVisibility(View.VISIBLE);
            fname.setText("First Name: " + user.getUserDetail().get(0).getFirstName());
        }
        if (user.getUserDetail().get(0).getLastName() == null)
            lname.setVisibility(GONE);
        else {
            lname.setVisibility(View.VISIBLE);
            lname.setText("Last Name: " + user.getUserDetail().get(0).getLastName());
        }
        if (user.getUserDetail().get(0).getEmail() == null)
            email.setVisibility(GONE);
        else {
            email.setVisibility(View.VISIBLE);
            email.setText("Email: " + user.getUserDetail().get(0).getEmail());
        }
        if (user.getUserDetail().get(0).getCountry() == null)
            country.setVisibility(GONE);
        else {
            country.setVisibility(View.VISIBLE);
            country.setText("Country: " + user.getUserDetail().get(0).getCountry());
        }
        if (user.getUserDetail().get(0).getRating() == null)
            rating.setVisibility(GONE);
        else {
            rating.setVisibility(View.VISIBLE);
            rating.setText("Rating: " + user.getUserDetail().get(0).getRating() + " (Max Rating: "
                    + user.getUserDetail().get(0).getMaxRating() + ")");
        }
        if (user.getUserDetail().get(0).getContribution() == null)
            contribution.setVisibility(GONE);
        else {
            contribution.setVisibility(View.VISIBLE);
            contribution.setText("Contribution: " + user.getUserDetail().get(0).getContribution());
        }
        if (user.getUserDetail().get(0).getFriendOfCount() == null)
            friends.setVisibility(GONE);
        else {
            friends.setVisibility(View.VISIBLE);
            friends.setText("Friend of: " + user.getUserDetail().get(0).getFriendOfCount());
        }
        if (user.getUserDetail().get(0).getOrganization() == null)
            organization.setVisibility(GONE);
        else {
            organization.setVisibility(View.VISIBLE);
            organization.setText("Organization: " + user.getUserDetail().get(0).getOrganization());
        }
        Date date1 = new Date((long) user.getUserDetail().get(0).getRegistrationTimeSeconds() * (long) 1000);
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.ENGLISH);
        sdf1.setTimeZone(TimeZone.getDefault());
        String formattedDate1 = sdf.format(date1);
        registeredon.setText("Registered On: " + formattedDate1);
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            if (cm.getActiveNetworkInfo().isConnected())
                return true;
            else
                return false;
        } else
            return false;
    }
}