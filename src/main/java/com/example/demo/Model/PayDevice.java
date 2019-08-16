package com.example.demo.Model;

import com.example.demo.Utils.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 27/10/2017.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayDevice {
    @Id
    @GeneratedValue
    private int id;
    private String bankCard;
    private int amount;
    private String state;
    private String platFrom;
    private String passWord;
    private String note;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPlatFrom() {
        return platFrom;
    }

    public void setPlatFrom(String platFrom) {
        this.platFrom = platFrom;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public PayDevice() {
        this.createTime = new Date();
        this.state = Config.Normal;
        this.platFrom = Config.OA;
    }
}
