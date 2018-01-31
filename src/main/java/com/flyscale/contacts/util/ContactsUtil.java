package com.flyscale.contacts.util;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.main.DeleteConfirmActivity;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/16.
 */

public class ContactsUtil {

    private static final String TAG = "ContactsUtil";

    /**
     * 获取手机联系人
     *
     * @param context
     * @return
     * @deprecated 该方法获取的名字有问题
     */
    public static ArrayList<ContactBean> getPhoneContacts(Context context) {
        ArrayList<ContactBean> contactBeans = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        if (phoneCursor != null) {
            contactBeans = new ArrayList<ContactBean>();
            while (phoneCursor.moveToNext()) {
                int nameIndex = phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME); //获取联系人name
                String name = phoneCursor.getString(nameIndex);
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Phone.NUMBER)); //获取联系人number
                String number = phoneCursor.getString(2);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                contactBeans.add(new ContactBean(name, phoneNumber, ContactBean.TYPE_LOCAL));
            }
            phoneCursor.close();
        }
        return contactBeans;
    }

    public static ArrayList<ContactBean> getLocalContacts(Context context) {
        ArrayList<ContactBean> contactBeans = null;
        Uri uri = Uri.parse("content://com.android.contacts/contacts"); // 访问所有联系人
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        if (null != cursor) {
            contactBeans = new ArrayList<ContactBean>();
            while (cursor.moveToNext()) {
                int contactsId = cursor.getInt(0);
                uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
                //某个联系人下面的所有数据
                Cursor dataCursor = resolver.query(uri, new String[]{"mimetype", "data1", "data2"},
                        null, null, null);
                String name = null;
                String phone = null;
                while (dataCursor.moveToNext()) {
                    String data = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    if ("vnd.android.cursor.item/name".equals(type)) {    // 如果他的mimetype类型是name
                        name = data;
                    } else if ("vnd.android.cursor.item/phone_v2".equals(type)) { //
                        phone = data;
                    }
                }
                contactBeans.add(new ContactBean(name, phone, ContactBean.TYPE_LOCAL));
            }
        }
        return contactBeans;
    }


    /**
     * 获取SIM卡联系人
     *
     * @param context
     * @return
     */
    public static ArrayList<ContactBean> getSIMContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();
        ArrayList<ContactBean> contactBeans = null;
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, null, null,
                null, null);
        if (phoneCursor != null) {
            contactBeans = new ArrayList<ContactBean>();
            while (phoneCursor.moveToNext()) {
                String name = phoneCursor.getString(phoneCursor.getColumnIndex("name"));
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex("number"));
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                contactBeans.add(new ContactBean(name, phoneNumber, ContactBean.TYPE_LOCAL));
            }
            phoneCursor.close();
        }
        return contactBeans;
    }

    /**
     * 删除联系人
     *
     * @param context
     * @param name
     */
    public static void delete(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                ContactsContract.Contacts.DISPLAY_NAME + "=?",
                new String[]{name}, null);
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        if (cursor.moveToFirst()) {
            do {
                long Id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data
                        .RAW_CONTACT_ID));
                ops.add(ContentProviderOperation.newDelete(
                        ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, Id))
                        .build());
                try {
                    context.getContentResolver()
                            .applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    /**
     * 更新联系人
     *
     * @param context
     * @param oldname
     * @param name
     * @param phone
     */
    public static void update(Context context, String oldname, String name, String phone) {

        Log.d(TAG, "update::oldname=" + oldname + ",name=" + name + ",phone=" + phone);
        if (TextUtils.isEmpty(oldname)) {
            return;
        }
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new
                        String[]{ContactsContract.Data.RAW_CONTACT_ID},
                ContactsContract.Contacts.DISPLAY_NAME + "=?", new String[]{oldname}, null);
        if (cursor == null || cursor.getCount() == 0){
            return;
        }
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
        cursor.close();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        //更新电话号码
        if (null != phone)
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?"
                                    + " AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(id), ContactsContract.CommonDataKinds
                                    .Phone.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .build());

        // 更新姓名
        if (null != name)
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?"
                                    + " AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(id), ContactsContract.CommonDataKinds
                                    .StructuredName.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
        }
    }

    /**
     * 添加联系人
     *
     * @param context
     * @param name
     * @param phone
     */
    public static void add(Context context, String name, String phone) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        //在名片表插入一个新名片
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // add name
        //添加一条新名字记录；对应RAW_CONTACT_ID为0的名片
        Log.d(TAG, "ContactsContract.Data.CONTENT_URI=" + ContactsContract.Data.CONTENT_URI);
        if (!name.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds
                            .StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());
        }


        // add phone
        if (!phone.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds
                            .Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "2")
                    //不知道为什么这里要传入2为参数，1的话会报错，插入失败
                    .build());
        }

        try {
            context.getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNameByPhone(Context context, String phone) {
        ArrayList<ContactBean> localContacts = getLocalContacts(context);
        for (int i = 0; i < localContacts.size(); i++) {
            if (TextUtils.equals(localContacts.get(i).getNumber(), phone)) {
                return localContacts.get(i).getName();
            }
        }
        return null;
    }

    public static void deleteAll(Context context) {

    }

    public static void delete(Context context, ArrayList<ContactBean> beans) {
        if (beans == null)
            return;
        for (int i=0; i<beans.size(); i++) {
            delete(context, beans.get(i).getName());
        }
    }
}
