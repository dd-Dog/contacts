package com.flyscale.contacts.main.options;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.telephony.TelephonyManager;
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
import com.flyscale.contacts.main.NewContactActivity;
import com.flyscale.contacts.util.SimCardState;

/**
 * Created by MrBian on 2018/1/16.
 */

public class NewContactOptionsActivity extends BaseActivity {

    private static final String TAG = "NewContactOptionsActivity";
    private static final int CREATE_NEW_CONTACT = 1020;
    private String[] mOptionsData;
    private ListView mOptions;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        if (SimCardState.hasSimCard(this)) {
            mOptionsData = getResources().getStringArray(R.array.phone_sim);
        } else {
            mOptionsData = getResources().getStringArray(R.array.phone);
        }
    }

    @Override
    protected void initView() {
        mOptions = (ListView) findViewById(R.id.main);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.save_position));
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
        Intent newContact = new Intent(this, NewContactActivity.class);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            newContact.putExtras(intent.getExtras());
        intent.putExtra(Constants.CONTACT_PHONE, getIntent().getStringExtra(Constants.CONTACT_PHONE));
        switch (position) {
            case 0:
                newContact.putExtra(Constants.NEW_CONTACT_TYPE, ContactBean.TYPE_LOCAL);
                break;
            case 1:
                newContact.putExtra(Constants.NEW_CONTACT_TYPE, ContactBean.TYPE_SIM);
                break;
        }
        startActivityForResult(newContact, CREATE_NEW_CONTACT);
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
                case CREATE_NEW_CONTACT:
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
