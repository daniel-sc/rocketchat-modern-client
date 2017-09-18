package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.response.GenericAnswer;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.function.Function;

public class ObservableSubjectWithMapper<T> {
    private final Function<GenericAnswer, T> mapper;
    private final PublishSubject<T> subject;
    private final Observable<T> observable;

    public ObservableSubjectWithMapper(PublishSubject<T> subject, Observable<T> observable, Function<GenericAnswer, T> mapper) {
        this.mapper = mapper;
        this.subject = subject;
        this.observable = observable;
    }

    public void next(GenericAnswer result) {
        subject.onNext(mapper.apply(result));
    }

    public Observable<T> getObservable() {
        return observable;
    }

    public Subject<T> getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return "ObservableSubjectWithMapper{" +
                "mapper=" + mapper +
                ", observable=" + observable +
                '}';
    }
}
