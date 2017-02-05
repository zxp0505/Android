package com.sample.architect.mvc;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.rx.Result;

import rx.Observable;
import rx.functions.Func1;

public class Model {

    Observable<String> getWeather(final String cityNumber) {

        Observable<Result> observable = new RxVolley.Builder()
                .url("http://www.weather.com.cn/data/sk/" + cityNumber + ".html")
                .contentType(RxVolley.ContentType.JSON)
                .getResult();

        return observable.map(new Func1<Result, String>() {
            @Override
            public String call(Result result) {
                return new String(result.data);
            }
        });
    }
}
