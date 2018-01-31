package com.flyscale.contacts.main;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.base.BaseActivity;
import com.flyscale.contacts.global.Constants;

/**
 * Created by MrBian on 2018/1/16.
 */

public class SearchActivity extends BaseActivity {

    private TextView back;
    private EditText et;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.search));
        et = (EditText) findViewById(R.id.et);
        back = (TextView) findViewById(R.id.back);
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
                } else {
                    back.setText(getResources().getString(R.string.clear));
                }
            }
        });
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MENU:
                String keyword = et.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    startActivity(new Intent(this, EmptyTextActivity.class));
                } else {
                    search(keyword);
                }
                break;
            case KeyEvent.KEYCODE_BACK:

                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void search(String keyword) {
        Intent search = new Intent(this, ContactsListActivity.class);
        search.putExtra(Constants.ACTION, Constants.ACTION_SEARCH_CONTACT);
        search.putExtra(Constants.INTENT_DATA, keyword);
        startActivity(search);
        finish();
    }
}
