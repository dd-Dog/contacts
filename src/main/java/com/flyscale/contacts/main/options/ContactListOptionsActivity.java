package com.flyscale.contacts.main.options;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.base.BaseActivity;
import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.main.ContactDetailActivity;
import com.flyscale.contacts.main.DeleteConfirmActivity;

/**
 * Created by MrBian on 2018/1/16.
 */

public class ContactListOptionsActivity extends BaseActivity {

    private static final String TAG = "ContactListOptions";
    private static final int CONTACT_DETAIL = 1003;
    private static final int DELETE_CONTACT = 1004;
    private static final int SEND_NEW_MSG = 1021;
    private static final int COPY_ONE = 1022;
    private static final int COPY_MULTI = 1023;
    private String[] mOptionsData;
    private ListView mOptions;
    private ContactBean contactBean;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_new_msg_options);
    }

    @Override
    protected void initData() {
        contactBean = (ContactBean) getIntent().getSerializableExtra(Constants.CONTACT_BEAN);
        mOptionsData = getResources().getStringArray(R.array.contactsoptions);
    }

    @Override
    protected void initView() {
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


    @SuppressLint("MissingPermission")
    private void handleOption(int position) {
        switch (position) {
            case 0:
                Intent detail = new Intent(this, ContactDetailActivity.class);
                detail.putExtras(getIntent().getExtras());
                startActivityForResult(detail, CONTACT_DETAIL);
                break;
            case 1:
                ComponentName componentName = new ComponentName("com.flyscale.mms", "com.flyscale" +
                        ".mms.main.NewMsgActivity");
                Intent newMsg = new Intent();
                newMsg.putExtra(Constants.ACTION, Constants.ACTION_SENDMSG_FROM_CONTACTS);
                newMsg.putExtra(Constants.NEW_MSG_NUM, contactBean.getNumber());
                newMsg.setComponent(componentName);
                startActivityForResult(newMsg, SEND_NEW_MSG);
                finish();
                break;
            case 2:
                Intent call = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + contactBean.getNumber()));
                startActivity(call);
                break;
            case 3:
                Intent delete = new Intent(this, DeleteConfirmActivity.class);
                delete.putExtras(getIntent().getExtras());
                startActivityForResult(delete, DELETE_CONTACT);
                break;
            case 4:
                markFinish();
                break;
            case 5:

                Intent copy = new Intent(this, CopyOptionsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CONTACT_BEAN, contactBean);
                Log.d(TAG, "contactBean=" + contactBean);
                copy.putExtras(bundle);
                startActivityForResult(copy, COPY_ONE);
                break;
            case 6:
                Intent copyDes = new Intent(this, CopyDesOptionsActivity.class);
                startActivityForResult(copyDes, COPY_MULTI);
                break;
        }
    }

    private void markFinish() {
        Intent mark = new Intent();
        mark.putExtra(Constants.ACTION, Constants.MARK_OPTION);
        mark.putExtra(Constants.MARK_OPTION, Constants.MARK_CURRENT_ITEM);
        mark.putExtra(Constants.MARK_POINT, Constants.MARK_TO_DELETE);
        setResult(RESULT_OK, mark);
        finish();
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
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case CONTACT_DETAIL:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case DELETE_CONTACT:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case SEND_NEW_MSG:
                    setResult(RESULT_OK, data);
                    finish();
                case COPY_ONE:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case COPY_MULTI:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
            }
        }
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
