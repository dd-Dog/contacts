package com.flyscale.contacts.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flyscale.contacts.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/1/17.
 */

public class NoSimcardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_emtpy);

        findViewById(R.id.confirm).setVisibility(View.INVISIBLE);
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText(getResources().getString(R.string.no_simcard));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }
}
