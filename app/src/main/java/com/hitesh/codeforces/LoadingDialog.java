package com.hitesh.codeforces;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class LoadingDialog {

    Context context;
    AlertDialog dialog;

    public LoadingDialog(Context context){
        this.context = context;
    }

    public void startLoader() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.loading_layout, null);
        builder.setView(view)
                .setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public void dismissLoader(){
        dialog.dismiss();
    }

}
