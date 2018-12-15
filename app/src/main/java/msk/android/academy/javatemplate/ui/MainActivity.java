package msk.android.academy.javatemplate.ui;

import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        setupFragment();
    }


    public void setupFragment() {
        InfoFragment f = InfoFragment.getInstance("Linkin Park", "Numb");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutBG, f)
                .commit();
    }
}
