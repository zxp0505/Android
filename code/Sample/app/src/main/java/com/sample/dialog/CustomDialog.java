package com.sample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.sample.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
        this.setTitle("Title");
    }
}
