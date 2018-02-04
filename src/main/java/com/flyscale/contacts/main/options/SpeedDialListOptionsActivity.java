package com.flyscale.contacts.main.options;

import android.annotation.SuppressLint;
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
import com.flyscale.contacts.bean.SpeedDialBean;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.main.ContactsListActivity;
import com.flyscale.contacts.main.DeleteConfirmActivity;
import com.flyscale.contacts.main.EditSpeedNumberActivity;
import com.flyscale.contacts.main.SpeedDialItemDetailActivity;

/**
 * Created by MrBian on 2018/1/16.
 */

public class SpeedDialListOptionsActivity extends BaseActivity {

    private static final String TAG = "ContactListOptions";
    private static final int SPEED_DIAL_DETAIL = 1003;
    private static final int DELETE_CONTACT = 1004;
    private static final int GET_SPPED_DIAL_NUM = 1016;
    private static final int DELETE_SPEED_DIAL = 1017;
    private static final int SELECT_CONTACT = 1019;
    private String[] mOptionsData;
    private ListView mOptions;
    private ContactBean contactBean;
    private SpeedDialBean speedDialBean;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_new_msg_options);
    }

    @Override
    protected void initData() {
        contactBean = (ContactBean) getIntent().getSerializableExtra(Constants.CONTACT_BEAN);
        speedDialBean = (SpeedDialBean) getIntent().getSerializableExtra(Constants.SPEED_DIAL_BEAN);
        mOptionsData = getResources().getStringArray(R.array.speeddialoptionsnotempty);
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
                Intent detail = new Intent(this, SpeedDialItemDetailActivity.class);
                detail.putExtras(getIntent().getExtras());
                startActivityForResult(detail, SPEED_DIAL_DETAIL);
                break;
            case 1:
                edit(speedDialBean);
                break;
            case 2:
                selectContact();
                break;
            case 3:
                Intent call = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + speedDialBean.phone));
                startActivity(call);
                break;
            case 4:
                Intent delete = new Intent(this, DeleteConfirmActivity.class);
                delete.putExtras(getIntent().getExtras());
                delete.putExtra(Constants.ACTION, Constants.DELETE_SPEED_DIAL);
                startActivityForResult(delete, DELETE_SPEED_DIAL);
                break;
        }
    }

    private void selectContact() {
        Intent select = new Intent(this, ContactsListActivity.class);
        select.putExtra(Constants.ACTION, Constants.SELECT_CONTACT);
        startActivityForResult(select, SELECT_CONTACT);
    }

    private void edit(SpeedDialBean mDialsDatum) {
        Intent intent = new Intent(this,
                EditSpeedNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SPEED_DIAL_BEAN, mDialsDatum);
        intent.putExtras(bundle);
        startActivityForResult(intent, GET_SPPED_DIAL_NUM);
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
                case DELETE_SPEED_DIAL:
                case SPEED_DIAL_DETAIL:
                case GET_SPPED_DIAL_NUM:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case SELECT_CONTACT:
                    ContactBean selectedContact = (ContactBean) data.getSerializableExtra(
                            Constants.CONTACT_BEAN);
                    Log.d(TAG, "selectedContact=" + selectedContact);
                    speedDialBean.name = selectedContact.getName();
                    speedDialBean.phone = selectedContact.getNumber();
                    Intent result = new Intent();
                    Bundle bundle = new Bundle();
                    result.putExtra(Constants.ACTION, Constants.SELECT_CONTACT);
                    bundle.putSerializable(Constants.SPEED_DIAL_BEAN, speedDialBean);
                    result.putExtras(bundle);
                    setResult(RESULT_OK, result);
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
