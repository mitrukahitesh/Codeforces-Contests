package com.hitesh.codeforces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hitesh.codeforces.problemset.Questions;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.CustomVHFragment> {

    Context context;
    List<Questions> questions;

    public QuestionAdapter(Context context, List<Questions> questions) {
        this.context = context;
        this.questions = questions;
    }

    public List<Questions> getQuestions() {
        return this.questions;
    }

    @NonNull
    @Override
    public CustomVHFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.question_holder, parent, false);
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
                for (int i = 0; i < tags.size(); ++i) {
                    TextView t = new TextView(context);
                    t.setText(tags.get(i));
                    t.setTextSize(14);
                    t.setBackground(context.getDrawable(R.drawable.fortags));
                    t.setTextColor(Color.WHITE);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 10, 0);
                    t.setPadding(5, 5, 5, 5);
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
        ImageView info, share, print;
        LinearLayout layout;

        public CustomVHFragment(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            id = (TextView) itemView.findViewById(R.id.id);
            solvedby = (TextView) itemView.findViewById(R.id.solvedby);
            info = (ImageView) itemView.findViewById(R.id.info);
            share = (ImageView) itemView.findViewById(R.id.share);
            print = (ImageView) itemView.findViewById(R.id.print);
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
                    context.startActivity(Intent.createChooser(intent, "Share"));
                }
            });
            print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    String url = "https://codeforces.com/problemset/problem/" + questions.get(position).getId().replace('-', '/');
                    WebView webView = new WebView(context);
                    final LoadingDialog dialog = new LoadingDialog(context);
                    dialog.startLoader();
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            dialog.dismissLoader();
                            PrintManager manager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                            String name = questions.get(position).getName();
                            if (name != null) {
                                PrintDocumentAdapter adapter = view.createPrintDocumentAdapter(name);
                                manager.print(name, adapter, new PrintAttributes.Builder().build());
                            } else {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            super.onReceivedError(view, request, error);
                            dialog.dismissLoader();
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                            super.onReceivedHttpError(view, request, errorResponse);
                            dialog.dismissLoader();
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                            super.onReceivedSslError(view, handler, error);
                            dialog.dismissLoader();
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                    webView.loadUrl(url);
                }
            });
        }
    }
}
