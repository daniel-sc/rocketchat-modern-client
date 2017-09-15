package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.response.GenericAnswer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CompletableFutureWithMapper<T> extends CompletableFuture<T> {

    private final Function<GenericAnswer, T> mapper;

    public CompletableFutureWithMapper(Function<GenericAnswer, T> mapper) {
        this.mapper = mapper;
    }

    public boolean completeAndMap(GenericAnswer genericAnswer) {
        return complete(mapper.apply(genericAnswer));
    }
}
