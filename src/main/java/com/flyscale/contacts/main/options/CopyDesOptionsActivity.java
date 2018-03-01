package com.flyscale.contacts.main.options;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
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
import com.flyscale.contacts.main.ContactsListActivity;
import com.flyscale.contacts.main.CopyStatusActivity;
import com.flyscale.contacts.main.NoSimcardActivity;
import com.flyscale.contacts.util.SimCardState;

/**
 * Created by MrBian on 2018/1/16.
 */

public class CopyDesOptionsActivity extends BaseActivity {

    private static final String TAG = "ContactListOptions";
    private static final int COPY_CONTACTS = 1022;
    private static final int NO_SIMCARD = 1031;
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
        mOptionsData = getResources().getStringArray(R.array.copy_des);
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
        Intent copyType = new Intent(this, ContactsListActivity.class);
        copyType.putExtra(Constants.ACTION, Constants.MARK_OPTION);
        copyType.putExtra(Constants.MARK_OPTION, Constants.MARK_CURRENT_ITEM);
        copyType.putExtra(Constants.MARK_POINT, Constants.MARK_TO_COPY);
        switch (position) {
            case 0:
                if (!SimCardState.hasSimCard(this)){
                    Intent nosim = new Intent(this, NoSimcardActivity.class);
                    startActivityForResult(nosim, NO_SIMCARD);
                    finish();
                }
                copyType.putExtra(Constants.COPY_TYPE, Constants.PHONE_TO_SIM);
                break;
            case 1:
                copyType.putExtra(Constants.COPY_TYPE, Constants.SIM_TO_PHONE);
                break;
        }
        setResult(RESULT_OK, copyType);
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
                case COPY_CONTACTS:
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
