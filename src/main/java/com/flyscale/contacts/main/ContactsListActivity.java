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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyscale.contacts.R;
import com.flyscale.contacts.base.BaseActivity;
import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.global.Constants;
import com.flyscale.contacts.main.options.ContactListOptionsActivity;
import com.flyscale.contacts.main.options.MarkOptionsActivity;
import com.flyscale.contacts.util.ContactsDAO;
import com.flyscale.contacts.util.ContactsUtil;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/16.
 */

public class ContactsListActivity extends BaseActivity {

    private static final String TAG = "ContactsListActivity";
    private static final int CONTACT_DETAIL = 1001;
    private static final int GET_CONTACT_OPTIONS = 1002;
    private static final int GET_CONTACT_MARK_OPTIONS = 1006;
    private ArrayList<ContactBean> mContacts;
    private ListView mContactsList;
    private TextView cancel;
    private boolean selectMode = false;
    private ContactsListAdapter listAdapter;
    private boolean markSituation = false;
    private ArrayList<ContactBean> mSearchResults;
    private String markPoint;
    private boolean pickMode;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_inbox);
    }

    @Override
    protected void initData() {
        listAdapter = new ContactsListAdapter();
        String action = getIntent().getStringExtra(Constants.ACTION);
        mContacts = new ArrayList<ContactBean>();
        mSearchResults = new ArrayList<ContactBean>();

        if (TextUtils.equals(action, Constants.ACTION_SEARCH_CONTACT)) {
            mContacts.clear();
            mContacts = ContactsDAO.getAllContacts(this);
//            mSimContacts = ContactsDAO.getSIMContacts(this);
//            mContacts.addAll(mPhoneContacts);
//            mContacts.addAll(mSimContacts);
            String keyword = getIntent().getStringExtra(Constants.INTENT_DATA);
            for (int i = 0; i < mContacts.size(); i++) {
                ContactBean contactBean = mContacts.get(i);
                if (contactBean.getNumber().contains(keyword) ||
                        contactBean.getName().contains(keyword)) {
                    mSearchResults.add(contactBean);
                }
            }
            mContacts = mSearchResults;
            listAdapter.notifyDataSetChanged();
            pickMode = false;
            selectMode = false;
        } else {
            if (TextUtils.equals(action, Constants.SELECT_CONTACT)) {
                selectMode = true;
                pickMode = false;
            } else if (TextUtils.equals(action, Constants.PICK_CONTACT)) {
                pickMode = true;
                selectMode = false;
            }
            refreshData();
        }

        ArrayList<ContactBean> localContacts = ContactsUtil.getLocalContacts(this);
        Log.d(TAG, "localContacts=" + localContacts);
//        ArrayList<ContactBean> simContactsBefore = ContactsDAO.getSimContacts(this);
//        Log.d(TAG, "simContactsBefore=" + simContactsBefore);
//        ContactsDAO.deleteToSim(this, "边建彪1111", "15033262664");
//        ArrayList<ContactBean> simContactsAfter = ContactsDAO.getSimContacts(this);
//        Log.d(TAG, "simContactsAfter=" + simContactsAfter);
//        ContactsDAO.getTest(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*onResume调用发生在onActivityResult之后*/
//        refreshData();
        listAdapter.notifyDataSetChanged();
    }

    private void refreshData() {
        mContacts.clear();
        mContacts = ContactsDAO.getAllContacts(this);
        listAdapter.notifyDataSetChanged();
    }

    private void refreshView() {
        findViewById(R.id.empty).setVisibility((mContacts.size()) == 0 ?
                View.VISIBLE : View.GONE);
        mContactsList.setVisibility((mContacts.size()) == 0 ?
                View.GONE : View.VISIBLE);
    }

    private void unMarkAll() {
        markSituation = false;
        for (int i = 0; i < mContacts.size(); i++) {
            mContacts.get(i).setMark(false);
        }
        listAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.back));
    }

    private void markItem(int position, boolean mark) {
        markSituation = true;
        mContacts.get(position).setMark(mark);
        listAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    private void unMarkItem(int position) {
        markItem(position, false);
        listAdapter.notifyDataSetChanged();
    }

    private void markAll() {
        markSituation = true;
        for (int i = 0; i < mContacts.size(); i++) {
            mContacts.get(i).setMark(true);
        }
        listAdapter.notifyDataSetChanged();
        cancel.setText(getResources().getString(R.string.cancel));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        int position = mContactsList.getSelectedItemPosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (mContacts == null || mContacts.size() == 0) {
                    finish();
                    return true;
                }
                if (selectMode) {
                    selectFinish();
                    return true;
                }
                if (markSituation) {
                    ContactBean contactBean = mContacts.get(position);
                    contactBean.setMark(!contactBean.isMark());
                    listAdapter.notifyDataSetChanged();
                } else {
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                if (mContacts == null || mContacts.size() == 0) {
                    finish();
                    return true;
                }
                if (selectMode) {
                    selectFinish();
                    return true;
                }
                if (pickMode) {
                    returnPick(position);
                    return true;
                }
                if (markSituation) {
                    Intent intent = new Intent(this, MarkOptionsActivity.class);
                    Bundle bundle = new Bundle();
                    Log.d(TAG, "markedbeans=" + getMarkedBeans());
                    bundle.putSerializable(Constants.CONTACT_MARKED_BEANS,
                            getMarkedBeans());
                    intent.putExtra(Constants.MARK_OPTION, getCurrentMarkOption());
                    intent.putExtra(Constants.MARK_POINT, markPoint);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, GET_CONTACT_MARK_OPTIONS);
                } else {
                    Intent options = new Intent(this, ContactListOptionsActivity.class);
                    Bundle bundle = new Bundle();
                    if (mContacts != null && mContacts.size() > 0) {
                        bundle.putSerializable(Constants.CONTACT_BEAN, mContacts.get(position));
                    }
                    options.putExtras(bundle);
                    startActivityForResult(options, GET_CONTACT_OPTIONS);
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (markSituation) {
                    unMarkAll();
                    return true;
                } else {
                    finish();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void selectFinish() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.CONTACT_BEAN,
                mContacts.get(mContactsList.getSelectedItemPosition()));
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private ArrayList<ContactBean> getMarkedBeans() {
        ArrayList<ContactBean> markedBeans = new ArrayList<ContactBean>();
        for (int i = 0; i < mContacts.size(); i++) {
            if (mContacts.get(i).isMark()) {
                markedBeans.add(mContacts.get(i));
            }
        }
        return markedBeans;
    }

    private String getCurrentMarkOption() {
        int markCount = 0;
        for (int i = 0; i < mContacts.size(); i++) {
            if (mContacts.get(i).isMark()) {
                markCount++;
            }
        }
        if (markCount == mContacts.size()) {
            return Constants.MARK_ALL;
        } else if (markCount == 0) {
            return Constants.CANCEL_ALL_MARKS;
        } else {
            return Constants.MARK_CURRENT_ITEM;
        }
    }

    @Override
    protected void initView() {
        mContactsList = (ListView) findViewById(R.id.main);
        findViewById(R.id.empty).setVisibility((mContacts.size()) == 0 ?
                View.VISIBLE : View.GONE);
        mContactsList.setVisibility((mContacts.size()) == 0 ?
                View.GONE : View.VISIBLE);
        cancel = (TextView) findViewById(R.id.back);
        mContactsList.setAdapter(listAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        TextView confirm = (TextView) findViewById(R.id.confirm);
        title.setText(getResources().getString(R.string.app_name));

        if (!selectMode && !(mContacts == null || mContacts.size() == 0))
            confirm.setText(getResources().getString(R.string.options));
        if (pickMode) {
            confirm.setText(getResources().getString(R.string.confirm));
        }
        mContactsList.setDivider(null);
        mContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectMode) {
                    selectFinish();
                } else if (pickMode) {
                    returnPick(position);
                } else {
                    readDetail(position);
                }
            }
        });

    }

    private void returnPick(int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ACTION, Constants.GET_CONTACT_BEAN);
        intent.putExtra(Constants.CONTACT_NAME, mContacts.get(position).getName());
        intent.putExtra(Constants.CONTACT_PHONE, mContacts.get(position).getNumber());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void readDetail(int position) {
        Intent detail = new Intent(this, ContactDetailActivity.class);
        Bundle bundle = new Bundle();
        Log.d(TAG, "contactbean=" + mContacts.get(position));
        bundle.putSerializable(Constants.CONTACT_BEAN, mContacts.get(position));
        detail.putExtras(bundle);
        startActivityForResult(detail, CONTACT_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String action = data.getStringExtra(Constants.ACTION);
            Log.d(TAG, "action=" + action);
            int position = mContactsList.getSelectedItemPosition();
            if (TextUtils.equals(action, Constants.DELETE_DONE)) {
                refreshData();
                refreshView();
                unMarkAll();
            } else if ((TextUtils.equals(action, Constants.MARK_OPTION))) {
                String markOption = data.getStringExtra(Constants.MARK_OPTION);
                markPoint = data.getStringExtra(Constants.MARK_POINT);
                Log.d(TAG, "markOption=" + markOption);
                if (TextUtils.equals(markOption, Constants.MARK_CURRENT_ITEM)) {
                    markItem(position, true);
                } else if (TextUtils.equals(markOption, Constants.MARK_ALL)) {
                    markAll();
                } else if (TextUtils.equals(markOption, Constants.CANCEL_CURRENT_MARK)) {
                    unMarkItem(position);
                } else if (TextUtils.equals(markOption, Constants.CANCEL_ALL_MARKS)) {
                    unMarkAll();
                }
            } else if (TextUtils.equals(action, Constants.SAVE_COMMONT_DONE)) {
                refreshData();
            }else if (TextUtils.equals(action, Constants.COPY_DONE)){
                refreshData();
                refreshView();
                unMarkAll();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ContactsListAdapter extends BaseAdapter {

        public ContactsListAdapter() {
        }

        @Override
        public int getCount() {
            return mContacts.size();
        }

        @Override
        public ContactBean getItem(int position) {
            return mContacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position % mContacts.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getLayoutInflater().inflate(R.layout.item_contacts, parent, false);
            }
            ContactBean contactBean = mContacts.get(position);
            ImageView icon = (ImageView) view.findViewById(R.id.contact_icon);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            cb.setVisibility(markSituation ? View.VISIBLE : View.GONE);
            if (markSituation) {
                Log.d(TAG, "isMark=" + contactBean.isMark());
                cb.setChecked(contactBean.isMark());
            }
            icon.setImageResource(contactBean.isLocal() ? R.drawable.contact_nv_icon
                    : R.drawable.contact_sim_icon);
            tv.setText(contactBean.getName());

            return view;
        }
    }
}
