package com.example.demo.Model;

import java.io.Serializable;

/**
 * Created by snsoft on 20/7/2017.
 */
public class Address implements Serializable{
    private int id;
    private String city;
    private String province;
    private static final long serialVersionUID = 7247714666080613254L;
    public Address(int id, String city, String province) {
        this.id = id;
        this.city = city;
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
