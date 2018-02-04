package com.flyscale.contacts.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.base.BaseActivity;
import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.global.Constants;

/**
 * Created by MrBian on 2018/1/16.
 */

public class NewContactActivity extends BaseActivity {
    private static final String TAG = "ContactDetailActivity";
    private static final int EDIT_CONTACT = 1009;
    private static final int SAVE_NEW_CONTACT = 1010;
    private ListView mMainTree;
    private MainTreeAdapter mMainTreeAdapter;
    private String[] mMainData;
    private ContactBean contactBean;
    private String newName;
    private String newPhone;
    private String mType;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        String action = getIntent().getStringExtra(Constants.ACTION);
        mMainData = getResources().getStringArray(R.array.newcontacts);
        if (TextUtils.equals(action, Constants.EXTRACT_SAVE)
                || TextUtils.equals(action, Constants.SAVE_NEW_CONTACT)) {
            String name = getIntent().getStringExtra(Constants.CONTACT_NAME);
            String phone = getIntent().getStringExtra(Constants.CONTACT_PHONE);
            if (!TextUtils.isEmpty(name))
                mMainData[0] = name;
            if (!TextUtils.isEmpty(phone))
                mMainData[1] = phone;
        }
        mType = getIntent().getStringExtra(Constants.NEW_CONTACT_TYPE);
        Log.d(TAG, "mType=" + mType);
    }

    @Override
    protected void initView() {
        mMainTree = (ListView) findViewById(R.id.main);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        TextView confirm = (TextView) findViewById(R.id.confirm);
        confirm.setText(getResources().getString(R.string.save));
        title.setText(getResources().getString(R.string.new_contact));

        mMainTree.setDivider(null);
        mMainTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewContactActivity.this, EditTextActivity.class);
                if (position == 0) {
                    intent.putExtra(Constants.ACTION, Constants.ACTION_EDIT_NAME);
                    intent.putExtra(Constants.INTENT_DATA, newName);
                } else {
                    intent.putExtra(Constants.ACTION, Constants.ACTION_EDIT_PHONE);
                    intent.putExtra(Constants.INTENT_DATA, newPhone);
                }
                startActivityForResult(intent, EDIT_CONTACT);
            }
        });

    }

    private void save() {
        Intent intent = new Intent(this, SaveConfirmActivity.class);
        intent.putExtra(Constants.ACTION, Constants.SAVE_NEW_CONTACT);
        ContactBean newBean = new ContactBean(newName, newPhone, mType);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.CONTACT_BEAN, newBean);
        intent.putExtras(bundle);
        startActivityForResult(intent, SAVE_NEW_CONTACT);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                save();
                return true;
            case KeyEvent.KEYCODE_BACK:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_CONTACT) {
                String action = data.getStringExtra(Constants.ACTION);
                if (TextUtils.equals(action, Constants.ACTION_EDIT_NAME)) {
                    newName = data.getStringExtra(Constants.INTENT_DATA);
                    mMainData[0] = newName;
                } else if (TextUtils.equals(action, Constants.ACTION_EDIT_PHONE)) {
                    newPhone = data.getStringExtra(Constants.INTENT_DATA);
                    mMainData[1] = newPhone;
                }
                mMainTreeAdapter.notifyDataSetChanged();
            } else if (requestCode == SAVE_NEW_CONTACT) {
                setResult(RESULT_OK, data);

                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MainTreeAdapter extends BaseAdapter {

        public MainTreeAdapter() {
        }

        @Override
        public int getCount() {
            return mMainData.length;
        }

        @Override
        public String getItem(int position) {
            return mMainData[position % mMainData.length];
        }

        @Override
        public long getItemId(int position) {
            return position % mMainData.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = null;

            if (convertView == null) {
                item = getLayoutInflater().inflate(R.layout.item_contacts, parent, false);
            } else {
                item = convertView;
            }
            TextView tv = (TextView) item.findViewById(R.id.tv);
            tv.setText(mMainData[position]);
            ImageView icon = (ImageView) item.findViewById(R.id.contact_icon);
            icon.setImageResource(position == 0 ?
                    R.drawable.phone_book_name : R.drawable.phone_book_phone);
            return item;
        }
    }
}
