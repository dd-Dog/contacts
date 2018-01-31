package com.flyscale.contacts.main;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ContactDetailActivity extends BaseActivity {

    private static final String TAG = "ContactDetailActivity";
    private static final int EDIT_CONTACT = 1007;
    private static final int SAVE_MODIFY = 1008;
    private ListView mMainTree;
    private MainTreeAdapter mMainTreeAdapter;
    private String[] mMainData;
    private boolean editMode = false;
    private ContactBean contactBean;
    private TextView confirm;
    private TextView title;
    private String newName;
    private String newPhone;
    private boolean modifySuccess;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        contactBean = (ContactBean) getIntent().getSerializableExtra(Constants.CONTACT_BEAN);
        newName = contactBean.getName();
        newPhone = contactBean.getNumber();
        Log.d(TAG, "contactBean=" + contactBean);
        mMainData = new String[2];
        mMainData[0] = contactBean.getName();
        mMainData[1] = contactBean.getNumber();
    }

    @Override
    protected void initView() {
        mMainTree = (ListView) findViewById(R.id.main);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        title = (TextView) findViewById(R.id.title);
        confirm = (TextView) findViewById(R.id.confirm);
        confirm.setText(getResources().getString(R.string.edit));
        title.setText(getResources().getString(R.string.detail));

        mMainTree.setDivider(null);
        mMainTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOptions(position);
            }
        });
        mMainTree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!editMode) {
                    if (position == 0) {
                        confirm.setText(getResources().getString(R.string.edit));
                    } else if (position == 1) {
                        confirm.setText(getResources().getString(R.string.call));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @SuppressLint("MissingPermission")
    private void handleOptions(int position) {
        switch (position) {
            case 0:
                if (editMode) {
                    editText(position);
                } else {
                    switchEditMode();
                }
                break;
            case 1:
                if (editMode) {
                    editText(position);
                } else {
                    Intent call = new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + contactBean.getNumber()));
                    startActivity(call);
                }
                break;
        }
    }

    private void editText(int position) {
        Intent intent = new Intent(this, EditTextActivity.class);
        if (position == 0) {
            intent.putExtra(Constants.ACTION, Constants.ACTION_EDIT_NAME);
            intent.putExtra(Constants.INTENT_DATA, contactBean.getName());
        } else {
            intent.putExtra(Constants.ACTION, Constants.ACTION_EDIT_PHONE);
            intent.putExtra(Constants.INTENT_DATA, contactBean.getNumber());
        }
        startActivityForResult(intent, EDIT_CONTACT);
    }

    private void switchEditMode() {
        editMode = true;
        confirm.setText(getResources().getString(R.string.save));
        title.setText(getResources().getString(R.string.edit_contact));

    }

    private void exitEditMode() {
        editMode = false;
        confirm.setText(getResources().getString(R.string.edit));
        title.setText(getResources().getString(R.string.detail));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int position = mMainTree.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (editMode) {
                    save();
                } else {
                    handleOptions(position);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (editMode) {
                    save();
                } else {
                    Log.d(TAG, "modifySuccess=" + modifySuccess);
                    if (modifySuccess) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.ACTION, Constants.SAVE_COMMONT_DONE);
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void save() {
        Intent intent = new Intent(this, SaveConfirmActivity.class);
        intent.putExtra(Constants.ACTION, Constants.UPDATE_CONTACT);
        intent.putExtra(Constants.MODIFIED_NAME, newName);
        intent.putExtra(Constants.MODIFIED_PHONE, newPhone);
        intent.putExtras(getIntent().getExtras());
        startActivityForResult(intent, SAVE_MODIFY);
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
            } else if (requestCode == SAVE_MODIFY) {
                modifySuccess = true;
                exitEditMode();
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
