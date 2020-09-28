package com.hitesh.codeforces;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class ContestFragment extends Fragment {

    private static final String URL = "https://codeforces.com/api/contest.list";
    String lastResponseString;
    RecyclerView recycler;
    String onlineResponse;
    SwitchCompat include;

    public ContestFragment() {
        lastResponseString = null;
    }

    public ContestFragment(String lastResponse) {
        this.lastResponseString = lastResponse;
    }

    public String getLastResponse() {
        return lastResponseString;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contest, container, false);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        include = (SwitchCompat) view.findViewById(R.id.include);
        setIncludeChangeListener();
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        if (lastResponseString != null) {
            loadLastResponse(lastResponseString);
            if (isInternetAvailable() && onlineResponse == null) {
                loadFreshResponse();
            }
        } else {
            if (isInternetAvailable()) {
                loadFreshResponse();
            } else
                Toast.makeText(getContext().getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    public void loadResponse(ArrayList<Result> response) {
        CustomAdapter adapter = new CustomAdapter(getContext(), response);
        recycler.setAdapter(adapter);
    }

    public Contests getContests(String response) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(response, Contests.class);
    }

    public ArrayList<Result> getResult(Contests contests) {
        ArrayList<Result> lastResponse = new ArrayList<>();
        for (int i = 0; i < contests.getResult().size(); ++i) {
            if (contests.getResult().get(i).getPhase().equals("BEFORE") || contests.getResult().get(i).getPhase().equals("CODING")) {
                lastResponse.add(contests.getResult().get(i));
            } else {
                break;
            }
        }
        return lastResponse;
    }

    public ArrayList<Result> getAllResult(Contests contests) {
        ArrayList<Result> lastResponse = new ArrayList<>();
        for (int i = 0; i < contests.getResult().size(); ++i) {
            lastResponse.add(contests.getResult().get(i));
        }
        return lastResponse;
    }

    public void loadLastResponse(String response) {
        Contests contests = getContests(response);
        loadResponse(getResult(contests));
    }

    public void loadFreshResponse() {
        StringRequest request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Contests contests = getContests(response);
                if (contests.getStatus().equals("OK")) {
                    lastResponseString = response;
                    onlineResponse = response;
                    if (include.isChecked())
                        loadResponse(getAllResult(contests));
                    else
                        loadResponse(getResult(contests));
                } else {
                    if (lastResponseString == null)
                        Toast.makeText(getContext().getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (lastResponseString == null)
                    Toast.makeText(getContext().getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void setIncludeChangeListener() {
        include.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(lastResponseString == null) {
//                    include.setChecked(false);
//                    return;
//                }
                if (b) {
                    loadResponse(getAllResult(getContests(lastResponseString)));
                } else {
                    loadResponse(getResult(getContests(lastResponseString)));
                }
            }
        });
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