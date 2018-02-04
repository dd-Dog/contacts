package com.flyscale.contacts.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.flyscale.contacts.R;
import com.flyscale.contacts.util.ContactsDAO;

/**
 * Created by MrBian on 2018/1/31.
 */

public class CapacityActivity extends Activity {
    private ListView mMainTree;
    private String[] mMainData;
    private MainTreeAdapter mMainTreeAdapter;
    private static final String TAG = "CapacityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        int count = 0;
        for (int i=0; i<1000; i++) {
            boolean b = ContactsDAO.addToSim(this, "小明" + i, "000--" + i);
            if (!b){
                break;
            }
            count ++;
        }
        Log.d(TAG, "count=" + count);
    }
    private void initData() {
        mMainData = getResources().getStringArray(R.array.sim_capacity);

    }
    private void initView() {
        mMainTree = (ListView) findViewById(R.id.main);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.capacity));
        mMainTree.setDivider(null);
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
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.item, parent, false);
            item.setText(mMainData[position % mMainData.length]);
            return item;
        }
    }
}
