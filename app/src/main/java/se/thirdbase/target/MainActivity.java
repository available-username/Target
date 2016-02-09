package se.thirdbase.target;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import se.thirdbase.target.view.TargetView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout layout = (FrameLayout)findViewById(R.id.main_layout_id);

        TargetView targetView = new TargetView(this);

        layout.addView(targetView);
    }
}
