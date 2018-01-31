package com.flyscale.contacts.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.flyscale.contacts.bean.ContactBean;
import com.flyscale.contacts.main.ContactsListActivity;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/31.
 */

public class ContactsDAO {


    /**
     * 获取所有的联系人信息
     *
     * @return
     */
    public static ArrayList<ContactBean> getAllContacts(Context context) {
        ArrayList<ContactBean> infos = new ArrayList<ContactBean>();

        ContentResolver cr = context.getContentResolver();

        // 查询的uri
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // 查询那些字段
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 名字
                ContactsContract.CommonDataKinds.Phone.NUMBER,// 电话
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID // 联系人id
        };
        Cursor cursor = cr.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                long contactId = cursor.getLong(2);

                ContactBean info = new ContactBean(name, number, contactId, ContactBean
                        .TYPE_LOCAL, false);
                infos.add(info);
            }
            cursor.close();
        }
        return infos;
    }


    /**
     * 通过名字获取电话号码
     */
    public static void getPhoneByName(Context context, String name1, long rawContactId) {
        //使用ContentResolver查找联系人数据
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        //遍历查询结果，找到所需号码
        while (cursor.moveToNext()) {
            //获取联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts
                    ._ID));
            //获取联系人的名字
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract
                    .CommonDataKinds.Phone.DISPLAY_NAME));
            if (name1.equals(contactName)) {
                //使用ContentResolver查找联系人的电话号码和用户名
                Cursor phone = context.getContentResolver().query(ContactsContract
                        .CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract
                        .CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phone.moveToNext()) {
                    String phoneNumber1 = phone.getString(phone.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.NUMBER));//电话号码
                    String phoneName1 = phone.getString(phone.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.DISPLAY_NAME));//姓名
                }
                //获取邮箱信息
                Cursor emails = context.getContentResolver().query(ContactsContract
                        .CommonDataKinds.Email.CONTENT_URI, null, ContactsContract
                        .CommonDataKinds.Email.CONTACT_ID + "=" + contactId, null, null);
                while (emails.moveToNext()) {
                    String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract
                            .CommonDataKinds.Email.DATA));
                }
                //获取IM信息
                Cursor cursorQQ = context.getContentResolver().query(ContactsContract.Data
                        .CONTENT_URI, null, ContactsContract.Data.RAW_CONTACT_ID + "=" +
                        rawContactId + " AND " + "mimetype_id=2", null, null);
                while (cursorQQ.moveToNext()) {
                    //获取公司信息
                    String im1 = cursorQQ.getString(cursorQQ.getColumnIndex(ContactsContract
                            .CommonDataKinds.Im.DATA));
                }
                //获取公司信息
                Cursor cursorCompany = context.getContentResolver().query(ContactsContract.Data
                        .CONTENT_URI, null, ContactsContract.Data.RAW_CONTACT_ID + "=" +
                        rawContactId + " AND " + "mimetype_id = 4", null, null);
                while (cursorCompany.moveToNext()) {
                    String company1 = cursorCompany.getString(cursorCompany.getColumnIndex
                            ("data1"));
                    String position1 = cursorCompany.getString(cursorCompany.getColumnIndex
                            ("data4"));
                }
            }
        }
    }

    //增加联系人
    public static void add(Context context, String name, String phoneNumber) {
        addContact(context, name, phoneNumber, null, null, null, null);
    }

    public static void addContact(Context context, String name, String phoneNumber, String email,
                                  String company, String position, String im) {
        /* 往 raw_contacts 中添加数据，并获取添加的id号*/
      /* 往 raw_contacts 中添加数据，并获取添加的id号*/
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentValues values = new ContentValues();
        ContentResolver resolver = context.getContentResolver();
        long rawContactId = ContentUris.parseId(resolver.insert(uri, values));
        //插入data表
        uri = Uri.parse("content://com.android.contacts/data");
        // 向data表插入数据
        if (!TextUtils.isEmpty(name)) {
            values.put("raw_contact_id", rawContactId);
            values.put("mimetype", "vnd.android.cursor.item/name");
            values.put("data2", name);
            resolver.insert(uri, values);
        }
        // 向data表插入电话号码
        if (!TextUtils.isEmpty(phoneNumber)) {
            values.clear();
            values.put("raw_contact_id", rawContactId);
            values.put("mimetype", "vnd.android.cursor.item/phone_v2");
            values.put("data2", "2");
            values.put("data1", phoneNumber);
            resolver.insert(uri, values);
        }
        //向data表中插入邮箱
        if (!TextUtils.isEmpty(email)) {
            // 添加Email
            values.clear();
            values.put("raw_contact_id", rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Email.DATA, email);
            values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract
                    .CommonDataKinds.Email.TYPE_WORK);
            resolver.insert(uri, values);
        }
        //向data表中插入联系人的组织
        if (!TextUtils.isEmpty(company) && !TextUtils.isEmpty(position)) {
            //organization
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.CommonDataKinds.Organization.MIMETYPE, ContactsContract
                    .CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Organization.LABEL, name);
            values.put(ContactsContract.CommonDataKinds.Organization.TITLE, position);
            values.put(ContactsContract.CommonDataKinds.Organization.COMPANY, company);
            values.put(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract
                    .CommonDataKinds.Organization.TYPE_WORK);
            context.getContentResolver().insert(uri, values);
        }
        //向data表中插入联系人的QQ
        if (!TextUtils.isEmpty(im)) {
            //im
            values.clear();
            values.put("raw_contact_id", rawContactId);
            values.put("mimetype", "vnd.android.cursor.item/im");
            values.put(ContactsContract.CommonDataKinds.Im.DATA, im);
            values.put(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds
                    .Im.TYPE_WORK);
            resolver.insert(uri, values);
        }
    }

    // 更新联系人
    public static void update(Context context, long rawContactId, String name, String phone) {
        updataContact(context, rawContactId, name, phone, null, null, null, null);
    }

    public static void updataContact(Context context, long rawContactId, String name, String
            number, String email, String company, String position, String im) {
        Uri uri = Uri.parse("content://com.android.contacts/data");//对data表的所有数据操作
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        //更新电话号码
        values.put("data1", number);
        resolver.update(uri, values, "mimetype_id=? and raw_contact_id=?", new String[]{"5",
                rawContactId + ""});
        //更新联系人姓名
        values.clear();
        values.put("data1", name);
        resolver.update(uri, values, "mimetype_id=? and raw_contact_id=?", new String[]{"7",
                rawContactId + ""});
        //更新email
        values.clear();
        values.put("data1", email);
        resolver.update(uri, values, "mimetype_id=? and raw_contact_id=?", new String[]{"1",
                rawContactId + ""});
        //更新im
        values.clear();
        values.put("data1", im);
        resolver.update(uri, values, "mimetype_id=? and raw_contact_id=?", new String[]{"2",
                rawContactId + ""});
        //更新company
        values.clear();
        values.put("data1", company);
        values.put("data3", name);
        values.put("data4", position);
        resolver.update(uri, values, "mimetype_id=? and raw_contact_id=?", new String[]{"4",
                rawContactId + ""});
    }

    // 删除联系人
    public static void delete(Context context, long rawContactId) {

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.RawContacts._ID},
                "contact_id=?", new String[]{String.valueOf(rawContactId)}, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            resolver.delete(uri, "_id=?", new String[]{id + ""});
            uri = Uri.parse("content://com.android.contacts/data");
            resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
            cursor.close();
        }
    }

    public static ArrayList<ContactBean> getSIMContacts(ContactsListActivity contactsListActivity) {
        ArrayList<ContactBean> contactBeans = new ArrayList<ContactBean>();

        return contactBeans;
    }
}
