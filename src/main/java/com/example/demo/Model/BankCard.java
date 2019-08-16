package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by snsoft on 16/8/2017.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankCard implements Serializable {
    private static final long serialVersionUID = 7247714666080613254L;
    @Id
    @GeneratedValue
    private int id;
    /**
     * 卡源
     */
    private String bankSource;
    /**
     * 批次
     */
    private String orderNumber;
    private String bankType;
    private String name;
    private String account;
    private String teleNumber;
    private Date timeLimt;
    private Double money;
    private String personNumber;
    private Date personTimelimit;
    private Date createTime;
    private String state;
    public BankCard() {
        this.personTimelimit = new Date();
        this.timeLimt = new Date();
        this.createTime = new Date();
        this.state = "未确认";
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankSource() {
        return bankSource;
    }

    public void setBankSource(String bankSource) {
        this.bankSource = bankSource;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTeleNumber() {
        return teleNumber;
    }

    public void setTeleNumber(String teleNumber) {
        this.teleNumber = teleNumber;
    }

    public Date getTimeLimt() {
        return timeLimt;
    }

    public void setTimeLimt(Date timeLimt) {
        this.timeLimt = timeLimt;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    public Date getPersonTimelimit() {
        return personTimelimit;
    }

    public void setPersonTimelimit(Date personTimelimit) {
        this.personTimelimit = personTimelimit;
    }
}
