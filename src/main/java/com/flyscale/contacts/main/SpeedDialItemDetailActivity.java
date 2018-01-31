package com.flyscale.contacts.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.bean.SpeedDialBean;
import com.flyscale.contacts.global.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MrBian on 2018/1/13.
 */

public class SpeedDialItemDetailActivity extends Activity {

    private static final String TAG = "InBoxMsgDetailActivity";
    private static final int REPLY_MSG = 1006;
    private static final int GET_SPPED_DIAL_NUM = 1013;
    private TextView title;
    private TextView detail;
    private String boxtype;
    private SpeedDialBean speedDialBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);

        initView();
        initData();
    }

    private void initData() {
        speedDialBean = (SpeedDialBean) getIntent().getSerializableExtra(Constants.SPEED_DIAL_BEAN);
        boxtype = getIntent().getStringExtra(Constants.BOX_TYPE);
        title.setText(getResources().getString(R.string.detail));
        String content = getResources().getString(R.string.name) + ":\n" +
                        speedDialBean.name + "\n"
                        + getResources().getString(R.string.phonenumber) + ":\n"
                        + speedDialBean.phone + "\n";
        Log.d(TAG, "content=" + content);
        detail.setText(content);
    }

    private void edit(SpeedDialBean mDialsDatum) {
        Intent intent = new Intent(this,
                EditSpeedNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SPEED_DIAL_BEAN, mDialsDatum);
        intent.putExtras(bundle);
        startActivityForResult(intent, GET_SPPED_DIAL_NUM);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                edit(speedDialBean);
                break;
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_SPPED_DIAL_NUM) {
                setResult(RESULT_OK, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        title = (TextView)findViewById(R.id.title);
        detail = (TextView)findViewById(R.id.msg_detail);
        TextView confirm = (TextView)findViewById(R.id.confirm);
        confirm.setText(getResources().getString(R.string.edit));
    }
}
