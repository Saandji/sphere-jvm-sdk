package io.sphere.sdk.utils;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Tools to simplify the work with {@link CompletionStage} and {@link CompletableFuture}.
 */
public final class CompletableFutureUtils {
    private CompletableFutureUtils() {
    }

    public static <T> CompletableFuture<T> successful(final T object) {
        return CompletableFuture.completedFuture(object);
    }

    static <T> Throwable blockForFailure(final CompletionStage<T> future) {
        try {
            future.toCompletableFuture().join();
            throw new NoSuchElementException(future + " did not complete exceptionally.");
        } catch (final CompletionException e1) {
            return e1.getCause();
        }
    }

    public static <T> CompletableFuture<T> failed(final Throwable e) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(e);
        return future;
    }

    public static <T> void transferResult(final CompletionStage<T> source,
                                          final CompletableFuture<T> target) {
        source.whenComplete((result, throwable) -> {
            final boolean isSuccessful = throwable == null;
            if (isSuccessful) {
                target.complete(result);
            } else {
                target.completeExceptionally(throwable);
            }
        });
    }

    public static <T> CompletionStage<T> onFailure(final CompletionStage<T> future, final Consumer<? super Throwable> consumer) {
        return future.whenCompleteAsync((value, throwable) -> {
            if (throwable != null) {
                consumer.accept(throwable);
            }
        });
    }

    public static <T> CompletionStage<T> onSuccess(final CompletionStage<T> future, final Consumer<? super T> consumer) {
        return future.whenCompleteAsync((value, throwable) -> {
            if (throwable == null) {
                consumer.accept(value);
            }
        });
    }

    public static <T> CompletionStage<T> recover(final CompletionStage<T> future, final Function<Throwable, ? extends T> f) {
        return future.exceptionally(f);
    }

    public static <T> CompletionStage<T> recoverWith(final CompletionStage<T> future, final Function<? super Throwable, CompletionStage<T>> f) {
        final CompletableFuture<T> result = new CompletableFuture<>();
        final BiConsumer<T, Throwable> action = (value, error) -> {
            if (value != null) {
                result.complete(value);
            } else {
                final CompletionStage<T> alternative = f.apply(error);
                alternative.whenComplete((alternativeValue, alternativeError) -> {
                    if (alternativeValue != null) {
                        result.complete(alternativeValue);
                    } else {
                        result.completeExceptionally(alternativeError);
                    }
                });
            }
        };
        future.whenComplete(action);
        return result;
    }

    public static <T, X extends Throwable> T orElseThrow(final CompletionStage<T> stage, Supplier<? extends X> exceptionSupplier) throws X, ExecutionException, InterruptedException {
        final CompletableFuture<T> future = stage.toCompletableFuture();
        if (future.isDone()) {
            return future.get();
        } else {
            throw exceptionSupplier.get();
        }
    }

    public static <T> T orElseGet(final CompletionStage<T> stage, final Supplier<T> other) throws ExecutionException, InterruptedException {
        final CompletableFuture<T> future = stage.toCompletableFuture();
        return future.isDone() ? future.get() : other.get();
    }

    public static <T, U> CompletionStage<U> map(final CompletionStage<T> future, final Function<? super T,? extends U> f) {
        return future.thenApply(f);
    }

    public static <T, U> CompletionStage<U> flatMap(final CompletionStage<T> future, final Function<? super T, CompletionStage<U>> f) {
        return future.thenCompose(f);
    }
}