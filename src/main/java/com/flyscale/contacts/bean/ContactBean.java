package com.flyscale.contacts.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by MrBian on 2018/1/16.
 */

public class ContactBean implements Serializable {
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_SIM = "sim";

    private String name;
    private String type;
    private String number;
    private long rawId;
    private boolean mark;

    @Override
    public String toString() {
        return "ContactBean{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", number='" + number + '\'' +
                ", rawId='" + rawId + '\'' +
                ", mark=" + mark +
                '}';
    }

    public ContactBean(String name,  String number, long rawId,String type, boolean mark) {
        this.name = name;
        this.type = type;
        this.number = number;
        this.rawId = rawId;
        this.mark = mark;
    }

    public long getRawId() {
        return rawId;
    }

    public void setRawId(long rawId) {
        this.rawId = rawId;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public ContactBean(String name, String number, String type) {
        this.name = name;
        this.type = type;
        this.number = number;
    }

    public static String getTypeLocal() {
        return TYPE_LOCAL;
    }

    public static String getTypeSim() {
        return TYPE_SIM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isLocal() {
        return TextUtils.equals(type, TYPE_LOCAL);
    }

    @Override
    public boolean equals(Object obj) {
        ContactBean bean = (ContactBean) obj;
        return TextUtils.equals(bean.getNumber(), number);
    }
}
