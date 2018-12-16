package msk.android.academy.javatemplate.network.util;

import android.support.annotation.Nullable;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import msk.android.academy.javatemplate.ui.App;

public class NetworkObserver<T> implements Observer<T> {
    private Disposable disposable;
    @Nullable
    private Consumer<Throwable> error;
    @Nullable
    private Consumer<T> complete;

    public NetworkObserver(@Nullable Consumer<T> complete, @Nullable Consumer<Throwable> error) {
        this.error = error;
        this.complete = complete;
    }

    @Override
    public void onNext(T result) {
        try {
            if (complete != null) complete.accept(result);
        } catch (Exception e) {
            App.logE(e.getMessage());
        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onError(Throwable t) {
        try {
            if (error != null) error.accept(t);
        } catch (Exception e) {
            App.logE(e.getMessage());
        }
    }

    public void unsubscribe() {
        if (disposable != null) disposable.dispose();
    }
}
