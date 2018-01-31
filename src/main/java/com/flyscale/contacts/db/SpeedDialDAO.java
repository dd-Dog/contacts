package com.flyscale.contacts.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.contacts.bean.SpeedDialBean;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/18.
 */

public class SpeedDialDAO {
    private DbHelper dbHelper;
    private static final String TAG = "SpeedDialDAO";

    public SpeedDialDAO(Context context) {
        dbHelper = new DbHelper(context, DbConstants.VERSION);
    }

    public SpeedDialBean findItem(String key) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "key=" + key);
        if (db != null) {
            Cursor cursor = db.query(DbConstants.SPEED_DIAL_TABLE_NAME, null,
                    DbConstants.SPEED_DIAL_KEY + "=?", new String[]{key}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int key2 = cursor.getInt(cursor.getColumnIndex(DbConstants.SPEED_DIAL_KEY));
                    String name = cursor.getString(cursor.getColumnIndex(DbConstants
                            .SPEED_DIAL_NAME));
                    String phone = cursor.getString(cursor.getColumnIndex(DbConstants
                            .SPEED_DIAL_PHONE));
                    return new SpeedDialBean(key2, name, phone);
                }
            }
        }
        return null;
    }

    public ArrayList<SpeedDialBean> findAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<SpeedDialBean> speedDialBeans = null;
        if (db != null) {
            speedDialBeans = new ArrayList<SpeedDialBean>();
            Cursor cursor = db.query(DbConstants.SPEED_DIAL_TABLE_NAME, null,
                    null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int key = cursor.getInt(cursor.getColumnIndex(DbConstants.SPEED_DIAL_KEY));
                    String name = cursor.getString(cursor.getColumnIndex(DbConstants
                            .SPEED_DIAL_NAME));
                    String phone = cursor.getString(cursor.getColumnIndex(DbConstants
                            .SPEED_DIAL_PHONE));
                    speedDialBeans.add(new SpeedDialBean(key, name, phone));
                }
            }
        }
        return speedDialBeans;
    }

    public boolean delete(String key) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int delete = 0;
        if (null != db) {
            delete = db.delete(DbConstants.SPEED_DIAL_TABLE_NAME, DbConstants.SPEED_DIAL_KEY
                            + "=?",
                    new String[]{key});
        }
        return delete != 0;
    }

    public boolean update(int key, String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int update = 0;
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone)) {
            return update != 0;
        }
        if (null != db) {
            ContentValues values = new ContentValues();
            if (!TextUtils.isEmpty(name))
                values.put(DbConstants.SPEED_DIAL_NAME, name);
            if (!TextUtils.isEmpty(phone))
                values.put(DbConstants.SPEED_DIAL_PHONE, name);
            update = db.update(DbConstants.SPEED_DIAL_TABLE_NAME, values,
                    DbConstants.SPEED_DIAL_KEY + "=?", new String[]{key + ""});
        }
        return update != 0;
    }

    public boolean insert(int key, String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int insert = 0;
        if (TextUtils.isEmpty(phone)) {
            return insert != 0;
        }
        if (null != db) {
            ContentValues values = new ContentValues();
            values.put(DbConstants.SPEED_DIAL_KEY, key);
            values.put(DbConstants.SPEED_DIAL_NAME, name);
            values.put(DbConstants.SPEED_DIAL_PHONE, phone);
            db.insert(DbConstants.SPEED_DIAL_TABLE_NAME, null, values);
        }

        return insert != 0;
    }

    public void update(SpeedDialBean dialBean) {
        update(dialBean.key, dialBean.name, dialBean.phone);
    }

    public void insert(SpeedDialBean dialBean) {
        insert(dialBean.key, dialBean.name, dialBean.phone);
    }
}
