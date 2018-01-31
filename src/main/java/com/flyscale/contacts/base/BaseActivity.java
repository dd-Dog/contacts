package com.flyscale.contacts.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by MrBian on 2018/1/16.
 */

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        initData();
        initView();
    }

    protected abstract void setContentView();
    protected abstract void initData();
    protected abstract void initView();
}
