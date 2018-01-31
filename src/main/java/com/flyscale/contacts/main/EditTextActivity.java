package com.flyscale.contacts.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.global.Constants;

/**
 * Created by MrBian on 2018/1/15.
 */

public class EditTextActivity extends Activity{

    private EditText et;
    private TextView back;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        TextView title = (TextView)findViewById(R.id.title);
        et = (EditText) findViewById(R.id.et);
        back = (TextView)findViewById(R.id.back);
        String str = getIntent().getStringExtra(Constants.INTENT_DATA);
        action = getIntent().getStringExtra(Constants.ACTION);
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
                }else {
                    back.setText(getResources().getString(R.string.clear));
                }
            }
        });
        if (TextUtils.equals(action, Constants.ACTION_EDIT_NAME)) {
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            title.setText(getResources().getString(R.string.name));
        }else if (TextUtils.equals(action, Constants.ACTION_EDIT_PHONE)) {
            et.setInputType(InputType.TYPE_CLASS_PHONE);
            title.setText(getResources().getString(R.string.mobile_phone));
        }
        et.setText(str);
        et.setSelection(et.getText().length());


    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Intent result = new Intent();
                result.putExtra(Constants.ACTION, action);
                String text = et.getText().toString().trim();
                result.putExtra(Constants.INTENT_DATA, text);
                setResult(RESULT_OK, result);
                finish();
                break;

            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
