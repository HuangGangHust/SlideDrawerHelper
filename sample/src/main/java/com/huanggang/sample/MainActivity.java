package com.huanggang.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Button btnSample1 = (Button) findViewById(R.id.btn_sample_1);
        btnSample1.setOnClickListener(this);

        Button btnSample2 = (Button) findViewById(R.id.btn_sample_2);
        btnSample2.setOnClickListener(this);

        Button btnSample3 = (Button) findViewById(R.id.btn_sample_3);
        btnSample3.setOnClickListener(this);
    }

    /**
     * 启动Activity
     */
    private <T extends Activity> void startActivity(Class<T> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sample_1:
                startActivity(Sample1Activity.class);
                break;

            case R.id.btn_sample_2:
                startActivity(Sample2Activity.class);
                break;

            case R.id.btn_sample_3:
                startActivity(Sample3Activity.class);
                break;

            default:
                break;
        }
    }// end onClick

}
