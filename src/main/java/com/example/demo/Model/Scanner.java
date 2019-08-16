package com.example.demo.Model;

/**
 * Created by snsoft on 19/12/2018.
 */
public class Scanner {
    private String u;
    private String a;
    private String m;
    private String s;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public Scanner(String u, String a, String m) {
        this.u = u;
        this.a = a;
        this.m = m;
        this.s = "money";
    }
}
