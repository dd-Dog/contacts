package com.flyscale.contacts.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.util.ContactsDAO;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MrBian on 2018/1/13.
 */

public class SaveConfirmActivity extends Activity {

    private static final String TAG = "DeleteConfirmActivity";
    private TextView confirm;
    private TextView status;
    private TextView cancel;
    private String smsUri;
    private ContactBean contactBean;
    private boolean dataComplete = false;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_confirm);

        initView();
        initData();

    }

    private void initData() {
        contactBean = (ContactBean) getIntent().getSerializableExtra(Constants.CONTACT_BEAN);
        String newName = getIntent().getStringExtra(Constants.MODIFIED_NAME);
        String newPHone = getIntent().getStringExtra(Constants.MODIFIED_PHONE);
        action = getIntent().getStringExtra(Constants.ACTION);
        if (TextUtils.equals(action, Constants.SAVE_NEW_CONTACT)) {
            if (contactBean == null) {
                status.setText(getResources().getString(R.string.empty_name_phone));
                dataComplete = false;
                confirm.setVisibility(View.INVISIBLE);
            } else {
                status.setText(getResources().getString(R.string.save) + "?");
                dataComplete = true;
                confirm.setVisibility(View.VISIBLE);
            }
        } else if (TextUtils.equals(action, Constants.UPDATE_CONTACT)) {
            if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPHone)) {
                status.setText(getResources().getString(R.string.empty_name_phone));
                dataComplete = false;
                confirm.setVisibility(View.INVISIBLE);
            } else {
                status.setText(getResources().getString(R.string.save) + "?");
                dataComplete = true;
                confirm.setVisibility(View.VISIBLE);
            }
        }
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
                intent.putExtra(Constants.ACTION, Constants.SAVE_COMMONT_DONE);
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
                if (dataComplete) {
                    status.setText(getResources().getString(R.string.saving));
                    if (TextUtils.equals(action, Constants.UPDATE_CONTACT)) {
                        ContactBean newBean = new ContactBean(
                                getIntent().getStringExtra(Constants.MODIFIED_NAME),
                                getIntent().getStringExtra(Constants.MODIFIED_PHONE),
                                this.contactBean.getType());
                        ContactsDAO.update(this, this.contactBean, newBean);

                    } else if (TextUtils.equals(action, Constants.SAVE_NEW_CONTACT)) {
                        ContactsDAO.add(this, contactBean);
                    }
                    delayFinish();
                }
                return true;

            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;

        }
        return super.onKeyUp(keyCode, event);
    }
}
