package com.hitesh.codeforces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hitesh.codeforces.problemset.Questions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionFragment extends Fragment {

    private final static String URL = "https://codeforces.com/api/problemset.problems";
    String lastResponse;
    String onlineResponse;
    RecyclerView recyclerView;
    ImageView sort, filter;
    Boolean sorted = false;
    String category;
    Map<String, List<Questions>> map = new HashMap<>();
    List<String> categories = new ArrayList<>();

    public QuestionFragment() {
        // Required empty public constructor
    }

    public QuestionFragment(String lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getLastResponse() {
        return lastResponse;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        sort = (ImageView) view.findViewById(R.id.sort);
        filter = (ImageView) view.findViewById(R.id.filter);
        setSortFilterListeners();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (lastResponse != null) {
            if (!map.containsKey("View all"))
                extractQuestions(getResponse(lastResponse));
            loadResponse();
            if (isInternetAvailable() && onlineResponse == null) {
                loadFreshResponse();
            }
        } else {
            if (isInternetAvailable()) {
                loadFreshResponse();
            } else {
                while (getContext() == null) {
                }
                Toast.makeText(getContext().getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
            }
        }
        return view;
    }

    public void loadResponse() {
        CustomAdapterFragment adapterFragment = new CustomAdapterFragment(getContext(), map.get(category));
        recyclerView.setAdapter(adapterFragment);
    }

    public com.hitesh.codeforces.problemset.Response getResponse(String response) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(response, com.hitesh.codeforces.problemset.Response.class);
    }

    public void loadFreshResponse() {
        StringRequest request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                com.hitesh.codeforces.problemset.Response freshResponse = getResponse(response);
                if (freshResponse.getStatus().equals("OK")) {
                    lastResponse = response;
                    onlineResponse = lastResponse;
                    extractQuestions(freshResponse);
                    loadResponse();
                } else {
                    if (lastResponse == null)
                        Toast.makeText(getContext().getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (lastResponse == null)
                    Toast.makeText(getContext().getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    public void extractQuestions(com.hitesh.codeforces.problemset.Response response) {
        categories.clear();
        map.clear();
        map.put("View all", new ArrayList<Questions>());
        Integer size = response.getResult().getProblems().size();
        for (int i = 0; i < size; ++i) {
            String name = response.getResult().getProblems().get(i).getName();
            String id = response.getResult().getProblems().get(i).getContestId() + "-" + response.getResult().getProblemStatistics().get(i).getIndex();
            Integer solvedBy = response.getResult().getProblemStatistics().get(i).getSolvedCount();
            List<String> tags = response.getResult().getProblems().get(i).getTags();
            Questions q = new Questions(name, id, solvedBy, tags);
            map.get("View all").add(q);
            for (int j = 0; j < tags.size(); ++j) {
                if (map.containsKey(tags.get(j)))
                    map.get(tags.get(j)).add(q);
                else {
                    categories.add(tags.get(j));
                    map.put(tags.get(j), new ArrayList<Questions>());
                    map.get(tags.get(j)).add(q);
                }
            }
        }
        category = "View all";
        Collections.sort(categories);
        categories.add(0, "View all");
    }

    public void setSortFilterListeners() {
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                List<Questions> setQuestions = ((CustomAdapterFragment) recyclerView.getAdapter()).getQuestions();
                if(lastResponse == null)
                    return;
                if (sorted) {
                    Collections.reverse(map.get(category));
                    loadResponse();
                } else {
                    Collections.sort(map.get(category));
                    sorted = true;
                    loadResponse();
                }
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Tag")
                        .setItems(categories.toArray(new CharSequence[categories.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sorted = false;
                                CustomAdapterFragment adapterFragment = new CustomAdapterFragment(getContext(), map.get(category = categories.get(i)));
                                recyclerView.setAdapter(adapterFragment);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
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