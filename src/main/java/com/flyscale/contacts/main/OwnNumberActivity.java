package com.flyscale.contacts.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class OwnNumberActivity extends BaseActivity {

    private static final String TAG = "OwnNumberActivity";
    private static final int EDIT_CONTACT = 1007;
    private static final int SAVE_MODIFY = 1008;
    private ListView mMainTree;
    private MainTreeAdapter mMainTreeAdapter;
    private String[] mMainData;
    private TextView confirm;
    private TextView title;
    private String newName;
    private String newPhone;
    private boolean modifySuccess;
    private String ownNumber;
    private String ownName;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        SharedPreferences sp = getSharedPreferences(Constants.CONTACTS_SP, Context
                .MODE_PRIVATE);
        ownName = sp.getString(Constants.OWN_NAME, "");
        ownNumber = sp.getString(Constants.OWN_NUMBER, "");
        Log.d(TAG, "ownName=" + ownName + ",ownNumber=" + ownNumber);
        newName = ownName;
        newPhone = ownNumber;
        mMainData = getResources().getStringArray(R.array.newcontacts);
        if (!TextUtils.isEmpty(ownName))
            mMainData[0] = ownName;
        if (!TextUtils.isEmpty(ownNumber))
            mMainData[1] = ownNumber;
    }

    @Override
    protected void initView() {
        mMainTree = (ListView) findViewById(R.id.main);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        title = (TextView) findViewById(R.id.title);
        confirm = (TextView) findViewById(R.id.confirm);
        confirm.setText(getResources().getString(R.string.save));
        title.setText(getResources().getString(R.string.local_number));

        mMainTree.setDivider(null);
        mMainTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText(position);
            }
        });

    }

    private void editText(int position) {
        Intent intent = new Intent(this, EditTextActivity.class);
        if (position == 0) {
            intent.putExtra(Constants.ACTION, Constants.ACTION_EDIT_NAME);
            intent.putExtra(Constants.INTENT_DATA, newName);
        } else {
            intent.putExtra(Constants.ACTION, Constants.ACTION_EDIT_PHONE);
            intent.putExtra(Constants.INTENT_DATA, newPhone);
        }
        startActivityForResult(intent, EDIT_CONTACT);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int position = mMainTree.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                    save();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void save() {
        SharedPreferences sp = getSharedPreferences(Constants.CONTACTS_SP, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.OWN_NAME, newName);
        editor.putString(Constants.OWN_NUMBER, newPhone);
        editor.commit();
        finish();
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
