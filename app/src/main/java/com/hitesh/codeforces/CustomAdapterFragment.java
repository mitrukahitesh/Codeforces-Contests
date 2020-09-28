package com.hitesh.codeforces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hitesh.codeforces.problemset.Problem;
import com.hitesh.codeforces.problemset.Questions;
import com.hitesh.codeforces.problemset.Response;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterFragment extends RecyclerView.Adapter<CustomAdapterFragment.CustomVHFragment> {

    Context context;
    List<Questions> questions;

    public CustomAdapterFragment(Context context, List<Questions> questions) {
        this.context = context;
        this.questions = questions;
    }

    public List<Questions> getQuestions(){
        return this.questions;
    }

    @NonNull
    @Override
    public CustomVHFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.question_layout, parent, false);
        return new CustomVHFragment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomVHFragment holder, final int position) {
        holder.name.setText(questions.get(position).getName());
        Runnable thread = new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                List<String> tags;
                tags = questions.get(position).getTags();
                holder.layout.removeAllViews();
                for (int i = 0; i < tags.size(); ++i){
                    TextView t = new TextView(context);
                    t.setText(tags.get(i));
                    t.setTextSize(18);
                    t.setBackground(context.getDrawable(R.drawable.fortags));
                    t.setTextColor(Color.WHITE);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 10, 0);
                    t.setPadding(3, 3, 3, 3);
                    t.setLayoutParams(layoutParams);
                    holder.layout.addView(t);
                }
            }
        };
        thread.run();
        holder.id.setText(questions.get(position).getId());
        holder.solvedby.setText(questions.get(position).getSolvedBy() + "");
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class CustomVHFragment extends RecyclerView.ViewHolder {
        TextView name, id, solvedby;
        ImageView info, share;
        LinearLayout layout;
        public CustomVHFragment(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            id = (TextView) itemView.findViewById(R.id.id);
            solvedby = (TextView) itemView.findViewById(R.id.solvedby);
            info = (ImageView) itemView.findViewById(R.id.info);
            share = (ImageView) itemView.findViewById(R.id.share);
            layout = (LinearLayout) itemView.findViewById(R.id.linearlayout);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String url = "https://codeforces.com/problemset/problem/" + questions.get(position).getId().replace('-', '/');
                    Intent intent = new Intent(context, WebViewOfQuestion.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("name", questions.get(position).getName() + " " + questions.get(position).getId());
                    context.startActivity(intent);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String url = "https://codeforces.com/problemset/problem/" + questions.get(position).getId().replace('-', '/');
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Check this problem at Codeforces\n\n" + url);
                    context.startActivity(intent);
                }
            });
        }
    }
}
