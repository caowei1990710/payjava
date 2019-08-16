package com.example.demo.Model;

import java.util.Date;

/**
 * Created by snsoft on 17/5/2018.
 */
public class Reponse {
    private String url;
    private String username;
    private String nickname;
    private String realname;
    private Double amount;
    private String account;
    private Date overTime;
    private String payUrl;
    private String qrUrl;
    private String ReturnUrl;
    private String scannerUrl;

    public String getScannerUrl() {
        return scannerUrl;
    }

    public void setScannerUrl(String scannerUrl) {
        this.scannerUrl = scannerUrl;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getReturnUrl() {
        return ReturnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        ReturnUrl = returnUrl;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String qrurl) {
        this.payUrl = qrurl;
    }

    public Date getOverTime() {
        return overTime;
    }

    public void setOverTime(Date overTime) {
        this.overTime = overTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Reponse() {
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", realname='" + realname + '\'' +
                ", amount=" + amount +
                ", account='" + account + '\'' +
                ", overTime=" + overTime +
                ", payUrl='" + payUrl + '\'' +
                ", ReturnUrl='" + ReturnUrl + '\'' +
                ", scannerUrl'" + scannerUrl + '\'' +
                '}';
    }
}
