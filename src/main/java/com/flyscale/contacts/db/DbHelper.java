package com.flyscale.contacts.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MrBian on 2018/1/18.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static int n  = 0;
    static {
        n = 2;
    }

    public DbHelper(Context context, int version) {
        super(context, DbConstants.CONTACTS_DB_NAME, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbConstants.SQL_CREATE_SPEED_DIAL_NUMBERS);
        n=3;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
