package com.example.demo.Model;

import com.example.demo.Utils.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by snsoft on 26/9/2017.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Proposal implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    private String orderNumber;
    private int amount;
    @NotEmpty(message = "提案不能为空")
    @Column(name = "deposit_number", unique = true)
    private String depositNumber;
    private Date creatTime;
    private Date updateTime;
    private Date overTime;
    private String platfrom;
    private String url;
    private String ip;
    private String callback;
    private String state;
    private String wechatName;
    private String notes;
    private String billNo;
    private String mobiletype;//0:pc,1:android,2:ios;
    private String userName;//游戏账号
    private String realName;//真实姓名

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBillNo() {
        return billNo;
    }

    public String getMobiletype() {
        return mobiletype;
    }

    public void setMobiletype(String mobiletype) {
        this.mobiletype = mobiletype;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private static final long serialVersionUID = 7247714666080613255L;

    public Date getOverTime() {
        return overTime;
    }

    public void setOverTime(Date overTime) {
        this.overTime = overTime;
    }


    public String getWechatName() {
        return wechatName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public int getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDepositNumber() {
        return depositNumber;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Proposal() {
        this.creatTime = new Date();
        this.updateTime = new Date();
        this.overTime = new Date(new Date().getTime() + 2 * 3600 * 1000);
        this.ip = "127.0.0.1";
        this.platfrom = "BI";
        this.state = Config.Pending;
        this.mobiletype = "0";
        this.url = "";
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "id=" + id +
                ", amount=" + amount +
                ", depositNumber='" + depositNumber + '\'' +
                ", creatTime=" + creatTime +
                ", updateTime=" + updateTime +
                ", platfrom='" + platfrom + '\'' +
                ", ip='" + ip + '\'' +
                ", mobiletype='" + mobiletype + '\'' +
                ", state='" + state + '\'' +
                ", realName='" + realName + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
