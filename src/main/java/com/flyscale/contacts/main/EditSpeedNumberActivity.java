package com.flyscale.contacts.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.bean.SpeedDialBean;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.util.ContactsUtil;

import java.io.Serializable;

/**
 * Created by MrBian on 2018/1/15.
 */

public class EditSpeedNumberActivity extends Activity {

    private static final int SELECT_CONTACT = 1014;
    private EditText et;
    private TextView back;
    private String action;
    private SpeedDialBean speedDialBean;
    private static final String TAG = "EditSpeedNumber";
    private TextView title;
    private TextView confirm;
    private boolean isContact = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        initData();
        initView();

    }

    private void initData() {
        speedDialBean = (SpeedDialBean) getIntent().getSerializableExtra(
                Constants.SPEED_DIAL_BEAN);
        action = getIntent().getStringExtra(Constants.ACTION);
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        et = (EditText) findViewById(R.id.et);
        back = (TextView) findViewById(R.id.back);
        confirm = (TextView) findViewById(R.id.confirm);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    back.setText(getResources().getString(R.string.back));
                    confirm.setText(getResources().getString(R.string.contacts));
                } else {
                    confirm.setText(getResources().getString(R.string.confirm));
                    back.setText(getResources().getString(R.string.clear));
                }
            }
        });

        et.setInputType(InputType.TYPE_CLASS_PHONE);
        et.setText(speedDialBean.phone);
        et.setSelection(et.getText().length());
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (!TextUtils.isEmpty(et.getText().toString())) {
                    myFinish();
                } else {
                    selectContact();
                }
                break;

            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void selectContact() {
        Intent select = new Intent(this, ContactsListActivity.class);
        select.putExtra(Constants.ACTION, Constants.SELECT_CONTACT);
        startActivityForResult(select, SELECT_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_CONTACT) {
                ContactBean selectedContact = (ContactBean) data.getSerializableExtra(
                        Constants.CONTACT_BEAN);
                Log.d(TAG, "selectedContact=" + selectedContact);
                speedDialBean.name = selectedContact.getName();
                speedDialBean.phone = selectedContact.getNumber();
                et.setText(speedDialBean.phone);
                et.setSelection(speedDialBean.phone.length());
                isContact = true;
//                myFinish();
            }
        }
    }

    private void myFinish() {
        Intent result = new Intent();
        result.putExtra(Constants.ACTION, action);
        result.putExtra(Constants.SPEED_DIAL_TYPE, Constants.SPEED_DIAL_TYPE_NUMBER);
        String text = et.getText().toString().trim();
        Bundle bundle = new Bundle();
        speedDialBean.phone = text;
        speedDialBean.name = ContactsUtil.getNameByPhone(this, text);
        Log.d(TAG, "speedDialBean=" + speedDialBean);
        bundle.putSerializable(Constants.SPEED_DIAL_BEAN, speedDialBean);
        result.putExtras(bundle);
        setResult(RESULT_OK, result);
        finish();
    }
}
