package msk.android.academy.javatemplate;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class EditTextListener implements TextWatcher {
    private static final String TAG = "ListenerEditText";
    private PublishSubject<String> subject;
    private Disposable disposable;

    public EditTextListener(EditText ed) {
        subject = PublishSubject.create();
        ed.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Не используется
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        subject.onNext(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Не используется
    }

    public void subscribe(Action1<String> action1) {
        disposable = subject.subscribe(
                action1::run,
                e -> Log.e(TAG, e.getMessage())
        );
    }

    public void unsubscribe() {
        disposable.dispose();
    }
}