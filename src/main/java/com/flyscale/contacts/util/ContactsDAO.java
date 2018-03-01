package com.flyscale.contacts.util;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.contacts.bean.ContactBean;

import java.util.ArrayList;

/**
 * Created by MrBian on 2018/1/31.
 */

public class ContactsDAO {

    private static final String TAG = "ContactsDAO";

    /**
     *
     * 获取所有联系人
     * 该方法可以获取所有sim卡和本机联系人,都有rawid.但是新添加到sim卡的联系人需要重启后才能通过
     * 该方法读取，可能是因为开机的时候系统会从sim卡中重新加载。实时读取需要使用getSimContacts方法
     *
     * 但是这里要区分出来sim卡联系人和本机联系人,就需要对比,把local和sim两个数组的交集的类型
     * 改成sim卡类型的,并从sim中删除,因为需要保留rawId(getSimContacts方法中读取的不包含rawId)
     *
     * @param context
     * @return
     */
    public static ArrayList<ContactBean> getAllContacts(Context context) {
        //intersection中包含所有的联系人
        ArrayList<ContactBean> intersection = getLocalContacts(context);
        Log.d(TAG, "所有联系人有" + intersection.size() + "个," + "intersection=" + intersection);
        ArrayList<ContactBean> diffrence = new ArrayList<ContactBean>();
        diffrence.addAll(intersection);
        ArrayList<ContactBean> sim = getSimContacts(context);
        ArrayList<ContactBean> simRetains = new ArrayList<ContactBean>();
        simRetains.addAll(sim);
        Log.d(TAG, "sim=" + sim);
        //取交集,把local中交集部分的type改为sim类型
        intersection.retainAll(sim);

        //sim卡中去除交集
        simRetains.removeAll(intersection);
        Log.d(TAG, "simRetains=" + simRetains);

        Log.d(TAG, "取交集后intersection=" + intersection);
        for (int i = 0; i < intersection.size(); i++) {
            intersection.get(i).setType(ContactBean.TYPE_SIM);
        }
        Log.d(TAG, "修改后类型后intersection=" + intersection);
        //取差集
        diffrence.removeAll(sim);
        Log.d(TAG, "取差集后diffrence=" + diffrence);
        //交集和差集合并
        intersection.addAll(diffrence);
        Log.d(TAG, "合并之后intersection=" + intersection);

        //合并sim卡去除交集之后的
        intersection.addAll(simRetains);
        return intersection;
    }

    /**
     * 增加联系人
     */
    public static void add(Context context, ContactBean contactBean) {
        if (TextUtils.equals(ContactBean.TYPE_LOCAL, contactBean.getType())) {
            addToLocal(context, contactBean.getName(), contactBean.getNumber());
        } else if (TextUtils.equals(ContactBean.TYPE_SIM, contactBean.getType())) {
            addToSim(context, contactBean.getName(), contactBean.getNumber());
        }
    }

    /**
     * 更新联系人
     *
     * @param context
     * @param oldBean
     * @param newBean
     */
    public static void update(Context context, ContactBean oldBean, ContactBean newBean) {
//        updateToLocal(context, newBean.getRawId(), newBean.getName(), newBean
//                .getNumber());
        if (TextUtils.equals(ContactBean.TYPE_LOCAL, newBean.getType())) {
            updateToLocal(context, newBean.getRawId(), newBean.getName(), newBean
                    .getNumber());
        } else if (TextUtils.equals(ContactBean.TYPE_SIM, newBean.getType())) {
            updateToSim(context, oldBean.getName(), oldBean.getNumber(),
                    newBean.getName(), newBean.getNumber());
        }
    }

    /**
     * 删除联系人
     *
     * @param context
     * @param bean
     */
    public static boolean delete(Context context, ContactBean bean) {
        return deleteToLocal(context, bean.getRawId());
    }

    public static ArrayList<ContactBean> getTest(Context context) {
        ArrayList<ContactBean> infos = new ArrayList<ContactBean>();
        Cursor cur = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        if (cur.moveToFirst()) {
            int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            do {
                String contactId = cur.getString(idColumn);
                String disPlayName = cur.getString(displayNameColumn);
                // 查看该联系人有多少个电话号码。如果没有这返回值为0
                int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                Log.i("username", disPlayName);
                if (phoneCount > 0) {
                    Cursor phones = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = " + contactId, null, null);
                    if (phones.moveToFirst()) {
                        do {
                            // 遍历所有的电话号码
                            long rawId = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String phoneType = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            Log.i("phoneNumber", phoneNumber);
                            Log.i("phoneType", phoneType);
                            infos.add(new ContactBean(name, phoneNumber, rawId,
                                    (TextUtils.equals(phoneType, "2")?ContactBean.TYPE_SIM:ContactBean.TYPE_LOCAL),false));
                        } while (phones.moveToNext());
                    }
                }
            } while (cur.moveToNext());
        }
        return infos;
    }

    /**
     * 获取本机联系人信息
     *
     * @return
     */
    public static ArrayList<ContactBean> getLocalContacts(Context context) {
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
     * 通过电话号码获取姓名
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


    public static boolean exsitInLocal() {

        return false;
    }
    public static void addToLocal(Context context, String name, String phoneNumber) {
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
    }

    // 更新联系人
    public static boolean updateToLocal(Context context, long rawContactId, String name, String
            phone) {
        updateToLocal2(context, rawContactId, name, phone);
        return true;
    }

    /**
     * 更新联系人
     *
     * @param context
     * @param rawid
     * @param name
     * @param phone
     */
    public static void updateToLocal2(Context context, long rawid, String name, String phone) {

        Log.d(TAG, "updateToLocal::oldname=" + rawid + ",name=" + name + ",phone=" + phone);
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        //更新电话号码
        if (null != phone)
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?"
                                    + " AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(rawid), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .build());

        // 更新姓名
        if (null != name)
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?"
                                    + " AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(rawid), ContactsContract.CommonDataKinds
                                    .StructuredName.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
        }

    }

    public static boolean updateToLocal(Context context, long rawContactId, String name, String
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
        int update = resolver.update(uri, values, "mimetype_id=? and raw_contact_id=?",
                new String[]{"4", rawContactId + ""});
        Log.d(TAG, "updateToLocal:: update=" + update);
        return update > 0;
    }

    /**
     * 删除联系人
     * 删除本机联系人
     * 同时该方法也可以删除sim卡联系人
     *
     * @param context
     * @param rawContactId
     * @return
     */
    public static boolean deleteToLocal(Context context, long rawContactId) {

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.RawContacts._ID},
                "contact_id=?", new String[]{String.valueOf(rawContactId)}, null);
        int delete = 0;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            resolver.delete(uri, "_id=?", new String[]{id + ""});
            uri = Uri.parse("content://com.android.contacts/data");
            delete = resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
            cursor.close();
        }
        Log.d(TAG, "deleteToLocal:: delete=" + delete);
        return delete > 0;
    }


    /**
     * 获取SIM卡联系人
     * 查询时，只支持获取获取联系人，即query()函数的后面几个参数都为null（其它值不起作用）。
     * 同时，也不支持类似content://icc/and/0的查询。
     */
    public static ArrayList<ContactBean> getSimContacts(Context context) {
        ArrayList<ContactBean> contactBeans = new ArrayList<ContactBean>();
        Uri uri = Uri.parse("content://icc/adn");

        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(Contacts.People._ID));
            String name = cursor.getString(cursor.getColumnIndex(Contacts.People.NAME));
            String phoneNumber = cursor.getString(cursor
                    .getColumnIndex(Contacts.People.NUMBER));
            contactBeans.add(new ContactBean(name, phoneNumber, ContactBean.TYPE_SIM));
        }
        cursor.close();
        return contactBeans;
    }

    /**
     * 插入联系人
     * 插入联系人只要设置名字和电话号码就可以了，要注意的是姓名对应的是tag，而不是name。
     */

    public static boolean addToSim(Context context, String tag, String number) {
        Log.d(TAG, "addToSim::tag=" + tag + ",number=" + number);
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", tag);
        values.put("number", number);
        Uri insert = context.getContentResolver().insert(uri, values);
        Log.d(TAG, "insert=" + insert);
        return insert != null;
    }

    /**
     * 更新联系人
     * <p>
     * 更新联系人要注意的是，它是以原先的姓名和电话号码来匹配要更新的联系人的，故要指定4个属性。
     */

    public static void updateToSim(Context context, String tag, String number, String newTag, String
            newNumber) {
        Log.d(TAG, "updateToSim::tag=" + tag + ",number=" + number + ",newTag=" +
                newTag + "newNumber=" + newNumber);
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", tag);
        values.put("number", number);
        values.put("newTag", newTag);
        values.put("newNumber", newNumber);

        ContentResolver resolver = context.getContentResolver();
        Log.d(TAG, "resolver=" + resolver);
        int update = resolver.update(uri, values, null, null);
        Log.d(TAG, "update=" + update);
    }

    /**
     * 删除SIM联系人
     * 删除联系人，同样是以名字和电话号码来匹配的，故需要在delete的where参数中指定，
     * 下面的例子演示了删除所有SIM卡内的联系人。
     * <p>
     * 该方法删除sim卡联系人失败，但是使用deleteToLocal却可以成功，不知道 为什么
     */
    public static boolean deleteToSim(Context context, String name, String phoneNumber) {
        Uri uri = Uri.parse("content://icc/adn");
//        Uri uri = Uri.parse("content://icc/adn/subId/0");
        String where = "tag='" + name + "'";
        where += " AND number='" + phoneNumber + "'";
        int delete = context.getContentResolver().delete(uri, where, null);
        Log.d(TAG, "deleteToSim:: delete=" + delete);
        return delete > 0;
    }

}
