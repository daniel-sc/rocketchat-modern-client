package com.github.daniel_sc.rocketchat.modern_client;

import com.github.daniel_sc.rocketchat.modern_client.response.GenericAnswer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CompletableFutureWithMapper<T> extends CompletableFuture<T> {

    private final Function<GenericAnswer, T> mapper;

    /**
     * @param mapper maps result from GenericAnswer to desired type.
     *               May throw exception to indicate error.
     */
    public CompletableFutureWithMapper(Function<GenericAnswer, T> mapper) {
        this.mapper = mapper;
    }

    public boolean completeAndMap(GenericAnswer genericAnswer) {
        try {
            return complete(mapper.apply(genericAnswer));
        } catch (Exception e) {
            return completeExceptionally(e);
        }
    }
}
