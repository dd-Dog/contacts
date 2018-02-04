package com.flyscale.contacts.main.options;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.main.CopyStatusActivity;
import com.flyscale.contacts.main.DeleteConfirmActivity;


/**
 * Created by MrBian on 2018/1/11.
 */

public class MarkOptionsActivity extends Activity {

    private static final int DELETE_MSG_CONFIRM = 1007;
    private static final int GET_MARK_OPTIONS = 1008;
    private static final int COPYT_CONTACTS_CONFIRM = 1025;
    private ListView mOptions;
    private String[] mOptionsData;
    private String markOption;
    private String markPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg_options);

        initData();
        initView();
    }

    private void initData() {
        markOption = getIntent().getStringExtra(Constants.MARK_OPTION);
        markPoint = getIntent().getStringExtra(Constants.MARK_POINT);
        if (TextUtils.equals(markOption, Constants.MARK_ALL)) {
            mOptionsData = getResources().getStringArray(R.array.mark3);
        } else if (TextUtils.equals(markOption, Constants.CANCEL_ALL_MARKS)) {
            mOptionsData = getResources().getStringArray(R.array.mark2);
        } else if (TextUtils.equals(markOption, Constants.MARK_CURRENT_ITEM)) {
            mOptionsData = getResources().getStringArray(R.array.mark1);
        }

    }

    private void initView() {
        mOptions = (ListView) findViewById(R.id.main);
        mOptions.setDivider(null);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        mOptions.setAdapter(optionsAdapter);
        mOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOption(position);
            }
        });
    }

    private void handleOption(int position) {
        Intent mark = new Intent();
        mark.putExtra(Constants.ACTION, Constants.MARK_OPTION);
        switch (position) {
            case 0:
                if (TextUtils.equals(markPoint, Constants.MARK_TO_DELETE)) {
                    Intent delete = new Intent(MarkOptionsActivity.this, DeleteConfirmActivity.class);

                    delete.putExtra(Constants.ACTION, Constants.DELETE_CONTACT_MULTI);
                    delete.putExtras(getIntent().getExtras());
                    startActivityForResult(delete, DELETE_MSG_CONFIRM);
                }else if (TextUtils.equals(markPoint, Constants.MARK_TO_COPY)) {
                    Intent copy = new Intent(MarkOptionsActivity.this, CopyStatusActivity.class);
                    copy.putExtra(Constants.ACTION, Constants.COPY_CONTACTS);
                    copy.putExtras(getIntent().getExtras());
                    startActivityForResult(copy, COPYT_CONTACTS_CONFIRM);
                }
                break;
            case 1:
                if (TextUtils.equals(markOption, Constants.CANCEL_ALL_MARKS) ||
                        TextUtils.equals(markOption, Constants.MARK_CURRENT_ITEM)) {
                    mark.putExtra(Constants.MARK_OPTION, Constants.MARK_ALL);
                } else if (TextUtils.equals(markOption, Constants.MARK_ALL)) {
                    mark.putExtra(Constants.MARK_OPTION, Constants.CANCEL_ALL_MARKS);
                }
                setResult(RESULT_OK, mark);
                finish();
                break;
            case 3:
                mark.putExtra(Constants.MARK_OPTION, Constants.CANCEL_ALL_MARKS);
                setResult(RESULT_OK, mark);
                finish();
                break;
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                int position = mOptions.getSelectedItemPosition();
                handleOption(position);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DELETE_MSG_CONFIRM) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
            }
            finish();
        } else if (requestCode == GET_MARK_OPTIONS) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
            }
            finish();
        }else if (requestCode == COPYT_CONTACTS_CONFIRM) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class OptionsAdapter extends BaseAdapter {

        public OptionsAdapter() {
        }

        @Override
        public int getCount() {
            return mOptionsData.length;
        }

        @Override
        public String getItem(int position) {
            return mOptionsData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item, null);
            item.setText(mOptionsData[position]);
            return item;
        }
    }
}
