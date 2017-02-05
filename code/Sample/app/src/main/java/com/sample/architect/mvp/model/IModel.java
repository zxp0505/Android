package com.sample.architect.mvp.model;

import rx.Observable;

public interface IModel {
    Observable<String> getWeather(final String cityNumber);
}
