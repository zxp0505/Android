package com.sample.architect.mvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sample.R;
import com.sample.architect.mvp.presenter.Presenter;

//MVP, 有一部分工作交给了P，MVC中原来的C简单了。
//同时，M与V进一步解耦，V感受不M的存在，是隔离的。

public class MVPActivity extends Activity implements IView {

    private EditText cityNOInput;
    private TextView city;

    private Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);
        initView();

        //Presenter
        mPresenter = new Presenter(this);
    }

    //初始化View
    private void initView() {
        cityNOInput = (EditText) findViewById(R.id.zoneId);
        city = (TextView) findViewById(R.id.info);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use Presenter
                getWeather(cityNOInput.getText().toString().trim());
            }
        });
    }

    private void getWeather(String cityNumber) {
        //Presenter
        mPresenter.updateInfo(cityNumber);
    }

    @Override
    public void updateView(String info) {
        city.setText(info);
    }
}
