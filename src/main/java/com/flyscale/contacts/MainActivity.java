package com.flyscale.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.contacts.main.CapacityActivity;
import com.flyscale.contacts.main.ContactsListActivity;
import com.flyscale.contacts.main.NewContactActivity;
import com.flyscale.contacts.main.OwnNumberActivity;
import com.flyscale.contacts.main.SearchActivity;
import com.flyscale.contacts.main.SpeedDialSettingsActivity;
import com.flyscale.contacts.main.options.NewContactOptionsActivity;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ListView mMainTree;
    private String[] mMainData;
    private MainTreeAdapter mMainTreeAdapter;
    private Class[] mActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        mMainData = getResources().getStringArray(R.array.main);
        mActivities = new Class[mMainData.length];
        mActivities[0] = ContactsListActivity.class;
        mActivities[1] = NewContactOptionsActivity.class;
        mActivities[2] = SearchActivity.class;
        mActivities[3] = SpeedDialSettingsActivity.class;
        mActivities[4] = OwnNumberActivity.class;
        mActivities[5] = CapacityActivity.class;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mMainTree = (ListView) findViewById(R.id.main);
        mMainTreeAdapter = new MainTreeAdapter();
        mMainTree.setAdapter(mMainTreeAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.app_name));

        mMainTree.setDivider(null);
        mMainTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, mActivities[position]);
                startActivityForResult(intent, position);
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                int selectedItemPosition = mMainTree.getSelectedItemPosition();
                Intent intent = new Intent(MainActivity.this, mActivities[selectedItemPosition]);
                startActivityForResult(intent, selectedItemPosition);
                return true;
        }
        return super.onKeyUp(keyCode, event);
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
