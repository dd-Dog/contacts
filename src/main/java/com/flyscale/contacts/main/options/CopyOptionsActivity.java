package com.flyscale.contacts.main.options;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
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
import com.flyscale.contacts.main.ContactDetailActivity;
import com.flyscale.contacts.main.CopyStatusActivity;
import com.flyscale.contacts.main.DeleteConfirmActivity;
import com.flyscale.contacts.main.NoSimcardActivity;
import com.flyscale.contacts.util.SimCardState;

/**
 * Created by MrBian on 2018/1/16.
 */

public class CopyOptionsActivity extends BaseActivity {

    private static final String TAG = "ContactListOptions";
    private static final int COPY_CONTACTS = 1022;
    private static final int NO_SIMCARD = 1030;
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
        Log.d(TAG, "contactBean=" + contactBean);
        if (contactBean != null) {
            if (TextUtils.equals(contactBean.getType(), ContactBean.TYPE_LOCAL)) {
                mOptionsData = getResources().getStringArray(R.array.copy_to_sim);
                if (!SimCardState.hasSimCard(this)){
                    Intent nosim = new Intent(this, NoSimcardActivity.class);
                    startActivityForResult(nosim, NO_SIMCARD);
                    finish();
                }
            } else if (TextUtils.equals(contactBean.getType(), ContactBean.TYPE_SIM)) {
                mOptionsData = getResources().getStringArray(R.array.copy_to_phone);
            }
        }else {
            mOptionsData = getResources().getStringArray(R.array.copy_to_phone);
        }
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
                Intent detail = new Intent(this, CopyStatusActivity.class);
                detail.putExtras(getIntent().getExtras());
                startActivityForResult(detail, COPY_CONTACTS);
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
