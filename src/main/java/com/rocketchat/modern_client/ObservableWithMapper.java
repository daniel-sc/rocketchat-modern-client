package com.rocketchat.modern_client;

import com.rocketchat.modern_client.response.GenericAnswer;
import io.reactivex.subjects.PublishSubject;

import java.util.function.Function;

public class ObservableWithMapper<T> {
    final Function<GenericAnswer, T> mapper;
    final PublishSubject<T> observable;

    public ObservableWithMapper(PublishSubject<T> observable, Function<GenericAnswer, T> mapper) {
        this.mapper = mapper;
        this.observable = observable;
    }

    public void next(GenericAnswer result) {
        observable.onNext(mapper.apply(result));
    }
}
