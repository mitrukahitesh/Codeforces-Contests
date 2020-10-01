package com.hitesh.codeforces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class WebViewOfQuestion extends AppCompatActivity {
    private WebView web;
    private Button save;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_of_question);
        intent = getIntent();
        String URL = intent.getStringExtra("URL");
        web = (WebView) findViewById(R.id.web);
        save = (Button) findViewById(R.id.save);
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.startLoader();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPrintProcess();
            }
        });

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismissLoader();
                save.setVisibility(View.VISIBLE);
            }
        });
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.setInitialScale(80);
        web.loadUrl(URL);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack())
            web.goBack();
        else
            super.onBackPressed();
    }

    private void startPrintProcess() {
        PrintManager manager = (PrintManager) getSystemService(PRINT_SERVICE);
        String name = intent.getStringExtra("name");
        if (name != null) {
            PrintDocumentAdapter adapter = web.createPrintDocumentAdapter(name);
            manager.print(name, adapter, new PrintAttributes.Builder().build());
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}