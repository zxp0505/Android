package com.sample.architect.mvp.presenter;

import android.util.Log;

import com.sample.architect.mvp.model.IModel;
import com.sample.architect.mvp.model.Model;
import com.sample.architect.mvp.view.IView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

//P也不直接引用VIEW，而是VIEW的接口。
//定义VIEW的接口也比较麻烦，也可让P直接引用VIEW，这样VM还是可能还是有耦合，但达到了简化了C的目的，有点像VIEW-CONTROL或子的MVC

public class Presenter {

    private IView mView;
    private IModel mMode;

    public Presenter(final IView view) {
        mView = view;
        mMode = new Model();
    }

    public void updateInfo(final String cityNumber) {
        //Model
        mMode.getWeather(cityNumber)
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
                        //update view
                        mView.updateView(result);
                    }
                });
    }
}

