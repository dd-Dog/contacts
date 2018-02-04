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
import com.flyscale.contacts.db.SpeedDialDAO;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.util.ContactsDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/1/13.
 */

public class CopyStatusActivity extends Activity {

    private static final String TAG = "DeleteConfirmActivity";
    private TextView confirm;
    private TextView status;
    private TextView cancel;
    private String action;
    private ContactBean contactBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_confirm);

        initView();
        initData();

    }

    private void initData() {
        action = getIntent().getStringExtra(Constants.ACTION);
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
                intent.putExtra(Constants.ACTION, Constants.COPY_DONE);
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
                if (TextUtils.equals(action, Constants.COPY_CONTACTS)) {
                    ArrayList<ContactBean> beans = (ArrayList<ContactBean>) getIntent
                            ().getSerializableExtra(Constants.CONTACT_MARKED_BEANS);
                    for (int i = 0; i < beans.size(); i++) {
                        copy(beans.get(i));
                    }
                    status.setText(getResources().getString(R.string.copyt_success));
                    delayFinish();
                    return true;
                }
                if (contactBean != null) {
                    copy(contactBean);
                }
                status.setText(getResources().getString(R.string.copyt_success));
                delayFinish();
                return true;

            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent();
                intent.putExtra(Constants.ACTION, Constants.COPY_DONE);
                setResult(RESULT_OK, intent);
                finish();
                break;

        }
        return super.onKeyUp(keyCode, event);
    }

    private void copy(ContactBean bean) {
        if (TextUtils.equals(bean.getType(), ContactBean.TYPE_LOCAL)) {
            ContactsDAO.addToSim(this, bean.getName(), bean.getNumber());
        } else {
            ContactsDAO.addToLocal(this, bean.getName(), bean.getNumber
                    ());
        }
    }
}
