package com.flyscale.contacts.bean;

import java.io.Serializable;

/**
 * Created by MrBian on 2018/1/18.
 */

public class SpeedDialBean implements Serializable{
    public int key;
    public String name;
    public String phone;

    public SpeedDialBean(int key, String name, String phone) {
        this.key = key;
        this.name = name;
        this.phone = phone;
    }

    public SpeedDialBean() {

    }

    public SpeedDialBean(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "SpeedDialBean{" +
                "key=" + key +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
