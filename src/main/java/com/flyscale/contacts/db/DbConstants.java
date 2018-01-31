package com.flyscale.contacts.db;

/**
 * Created by MrBian on 2018/1/18.
 */

public class DbConstants {


    /**
     * 数据库
     */
    public static final String CONTACTS_DB_NAME = "contacts.db";
    public static final String SPEED_DIAL_NAME = "name";
    public static final String SPEED_DIAL_PHONE = "phone";
    public static final String SPEED_DIAL_TABLE_NAME = "speed_dial";
    public static final String SPEED_DIAL_KEY = "key";
    public static final String SQL_CREATE_SPEED_DIAL_NUMBERS =
            "CREATE TABLE " + SPEED_DIAL_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SPEED_DIAL_KEY + " INTETER UNIQUE," +
                    SPEED_DIAL_NAME + " TEXT," +
                    SPEED_DIAL_PHONE + " TEXT)";
    public static final int VERSION = 1;
    public static final String AUTHORITY = "com.flyscale.contacts.provider";
    public static final int SPEED_DIAL_MATCH_CODE = 1;
}
