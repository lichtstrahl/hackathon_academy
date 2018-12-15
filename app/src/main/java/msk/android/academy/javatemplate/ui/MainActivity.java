package msk.android.academy.javatemplate.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import msk.android.academy.javatemplate.R;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.layoutBG)
    ViewGroup layoutBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            setupFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void setupFragment() {
        InfoFragment f = InfoFragment.getInstance("Rammstein", "Mutter");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutBG, f)
                .commit();
    }
}
