package com.flyscale.contacts.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.bean.SpeedDialBean;
import com.flyscale.contacts.db.DbHelper;
import com.flyscale.contacts.db.SpeedDialDAO;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.util.ContactsUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/1/13.
 */

public class DeleteConfirmActivity extends Activity {

    private static final String TAG = "DeleteConfirmActivity";
    private TextView confirm;
    private TextView status;
    private TextView cancel;
    private String smsUri;
    private ContactBean contactBean;
    private String action;
    private SpeedDialBean speedDailBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_confirm);

        initView();
        initData();

    }

    private void initData() {
        action = getIntent().getStringExtra(Constants.ACTION);
        speedDailBean = (SpeedDialBean) getIntent().getSerializableExtra(Constants.SPEED_DIAL_BEAN);
        contactBean = (ContactBean) getIntent().getSerializableExtra(Constants.CONTACT_BEAN);
    }

    private void initView() {
        confirm = (TextView) findViewById(R.id.confirm);
        status = (TextView) findViewById(R.id.status);
        cancel = (TextView) findViewById(R.id.back);
    }

    public void delayFinish() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.DELETE_DONE);
                setResult(RESULT_OK, intent);
                finish();
            }
        }, 3000);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MENU:
                status.setText(getResources().getString(R.string.deleting));
                if (TextUtils.equals(action, Constants.DELETE_SPEED_DIAL)) {
                    new SpeedDialDAO(this).delete(speedDailBean.key + "");
                }else if (TextUtils.equals(action, Constants.DELETE_CONTACT_MULTI)) {
                    ArrayList<ContactBean> beans = (ArrayList<ContactBean>)
                            getIntent().getSerializableExtra(Constants.CONTACT_MARKED_BEANS);
                    ContactsUtil.delete(this, beans);
                } else {
                    ContactsUtil.delete(this, contactBean.getName());
                }
                status.setText(getResources().getString(R.string.delete_success));
                delayFinish();
                return true;

            case KeyEvent.KEYCODE_BACK:
                finish();
                break;

        }
        return super.onKeyUp(keyCode, event);
    }
}
