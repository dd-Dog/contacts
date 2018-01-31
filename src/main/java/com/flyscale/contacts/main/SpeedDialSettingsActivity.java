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
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.base.BaseActivity;
import com.flyscale.contacts.bean.SpeedDialBean;
import com.flyscale.contacts.db.SpeedDialDAO;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.main.options.SpeedDialListOptionsActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by MrBian on 2018/1/16.
 */

public class SpeedDialSettingsActivity extends BaseActivity {

    private static final String TAG = "SpeedDialSettings";
    private static final int GET_SPPED_DIAL_NUM = 1011;
    private static final int GET_SPEED_DIAL_OPTIONS = 1012;
    private Set<String> numbers;
    private ArrayList<SpeedDialBean> mDials;
    private SpeedDialBean[] mDialsData;
    private ListView mMainTree;
    private MainTreeAdapter mMainTreeAdapter;
    private SpeedDialDAO speedDialDAO;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        speedDialDAO = new SpeedDialDAO(this);
        mDialsData = new SpeedDialBean[10];
        refreshData();
        Log.d(TAG, "mDials=" + mDials);
    }

    private void refreshData() {
        mDials = speedDialDAO.findAll();
        for (int i = 0; i < mDialsData.length; i++) {
            mDialsData[i] = new SpeedDialBean(i);
        }
        for (int i = 0; i < mDials.size(); i++) {
            mDialsData[mDials.get(i).key] = mDials.get(i);
        }
    }

    @Override
    protected void initView() {
        TextView confirm = (TextView) findViewById(R.id.confirm);
        TextView title = (TextView) findViewById(R.id.title);
        TextView back = (TextView) findViewById(R.id.back);
        confirm.setText(getResources().getString(R.string.options));
        title.setText(getResources().getString(R.string.speed_dial));
        back.setText(getResources().getString(R.string.exit));

        mMainTree = (ListView) findViewById(R.id.main);
        mMainTree.setDivider(null);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        mMainTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit(mDialsData[position]);
            }
        });
    }

    private void edit(SpeedDialBean mDialsDatum) {
        Intent intent = new Intent(SpeedDialSettingsActivity.this,
                EditSpeedNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SPEED_DIAL_BEAN, mDialsDatum);
        intent.putExtras(bundle);
        startActivityForResult(intent, GET_SPPED_DIAL_NUM);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int position = mMainTree.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                SpeedDialBean item = mDialsData[position];
                if (TextUtils.isEmpty(item.phone)) {
                    edit(mDialsData[position]);
                } else {
                    Intent options = new Intent(this, SpeedDialListOptionsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.SPEED_DIAL_BEAN, mDialsData[position]);
                    options.putExtras(bundle);
                    startActivityForResult(options, GET_SPEED_DIAL_OPTIONS);
                }
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            String action = null;
            if (data != null) {
                action = data.getStringExtra(Constants.ACTION);
            }
            int position = mMainTree.getSelectedItemPosition();
            if (requestCode == GET_SPPED_DIAL_NUM) {
                SpeedDialBean resultBean = (SpeedDialBean) data.getSerializableExtra(
                        Constants.SPEED_DIAL_BEAN);
                Log.d(TAG, "speedDialBean=" + resultBean);
                mDialsData[position] = resultBean;
                if (null != speedDialDAO.findItem(resultBean.key + "")) {
                    speedDialDAO.update(mDialsData[position]);
                } else {
                    speedDialDAO.insert(mDialsData[position]);
                }
            } else if (requestCode == GET_SPEED_DIAL_OPTIONS) {
                if (TextUtils.equals(action, Constants.DELETE_DONE)) {
                    refreshData();
                    mMainTreeAdapter.notifyDataSetChanged();
                } else if (TextUtils.equals(action, Constants.SELECT_CONTACT)) {
                    SpeedDialBean resultBean = (SpeedDialBean) data.getSerializableExtra(
                            Constants.SPEED_DIAL_BEAN);
                    mDialsData[position] = resultBean;
                    speedDialDAO.update(mDialsData[position]);
                }
            }
            mMainTreeAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MainTreeAdapter extends BaseAdapter {

        public MainTreeAdapter() {
        }

        @Override
        public int getCount() {
            return mDialsData.length;
        }

        @Override
        public SpeedDialBean getItem(int position) {
            return mDialsData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item, parent, false);
            SpeedDialBean bean = mDialsData[position];
            if (TextUtils.isEmpty(bean.phone)) {
                item.setText("M" + position + "(" +
                        getResources().getString(R.string.empty) + ")");
            } else {
                item.setText("M" + position + "(" + (!TextUtils.isEmpty(bean.name) ?
                        bean.name : bean.phone) + ")");
            }
            return item;
        }
    }
}
