package com.example.demo.Model;

/**
 * Created by snsoft on 19/1/2018.
 */
public class Reutrn {
    private String url;
    private String code;
    private int state;

    public String getUrl() {
        return url;
    }

    public Reutrn() {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Reutrn{" +
                "url='" + url + '\'' +
                ", code='" + code + '\'' +
                ", state=" + state +
                '}';
    }
}
