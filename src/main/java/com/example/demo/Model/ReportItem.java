package com.example.demo.Model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 26/9/2017.
 */
public class ReportItem {
    @Id
    @GeneratedValue
    private int id;
    private String wechatName;
    //金额变动
    private Double remit;
    //前余额
    private Double currentCredit;
    //后余额
    private Double newCredit;
    //创建者
    private String createUser;
    //游戏账号
    private String userName;
    //备注
    private String remark;
    //创建时间
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public Double getRemit() {
        return remit;
    }

    public void setRemit(Double remit) {
        this.remit = remit;
    }

    public Double getCurrentCredit() {
        return currentCredit;
    }

    public void setCurrentCredit(Double currentCredit) {
        this.currentCredit = currentCredit;
    }

    public Double getNewCredit() {
        return newCredit;
    }

    public void setNewCredit(Double newCredit) {
        this.newCredit = newCredit;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ReportItem() {
        this.createTime = new Date();
    }
}
