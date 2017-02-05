package com.sample.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sample.R;

public class DialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        findViewById(R.id.butShowDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog();
            }
        });
    }

    private void showNormalDialog() {
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
    }
}

