package com.sample.architect.mvc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sample.R;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

//MVCActivity相当于MVC中的Control.

public class MVCActivity extends Activity {

    private EditText cityNOInput;
    private TextView city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvc);
        initView();
    }

    //初始化View
    private void initView() {
        cityNOInput = (EditText) findViewById(R.id.zoneId);
        city = (TextView) findViewById(R.id.info);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Control call model
                getWeather(cityNOInput.getText().toString().trim());
            }
        });
    }

    private void getWeather(String cityNumber) {
        //Model
        new Model().getWeather(cityNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String result) {
                        Log.d("TAG", "result:" + result);
                        //Model notify control
                        updateView(result);
                    }
                });
    }

    //control update view
    private void updateView(final String result) {
        city.setText(result);
    }
}
