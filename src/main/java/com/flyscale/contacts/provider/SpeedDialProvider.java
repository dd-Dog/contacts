package com.flyscale.contacts.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.flyscale.contacts.db.DbConstants;
import com.flyscale.contacts.db.DbHelper;

/**
 * Created by MrBian on 2018/1/24.
 */

public class SpeedDialProvider extends ContentProvider {
    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DbConstants.AUTHORITY, DbConstants.SPEED_DIAL_TABLE_NAME,
                DbConstants.SPEED_DIAL_MATCH_CODE);
    }

    private DbHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext(), DbConstants.VERSION);

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String
            selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case DbConstants.SPEED_DIAL_MATCH_CODE:
                Cursor cursor = db.query(DbConstants.SPEED_DIAL_TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder, null, null);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[]
            selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String
            selection, String[] selectionArgs) {
        return 0;
    }
}
