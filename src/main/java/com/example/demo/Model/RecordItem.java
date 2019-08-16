package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 18/12/2017.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordItem {
    @Id
    @GeneratedValue
    private int recordId;
    private String account;
    private String balance;
    private String beforeBalance;
    private String id;
    private String platfrom;
    private String plafrom;
    private String senderAccount;
    private String senderComment;
    private String senderName;
    private String senderNickname;
    private String transferAmount;
    private String remark;
    private Date transferTime;
    private Date createTime;
    private int state;
    private int times;
    private int type;
    private int number;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPlafrom() {
        return plafrom;
    }

    public void setPlafrom(String plafrom) {
        this.plafrom = plafrom;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getSenderComment() {
        return senderComment;
    }

    public void setSenderComment(String senderComment) {
        this.senderComment = senderComment;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public Date getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(Date transferTime) {
        this.transferTime = transferTime;
    }

    public RecordItem() {
        this.state = 1;
        this.times = 0;
        this.type = 7;
        this.beforeBalance = 100 + "";
        this.createTime = new Date();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(String beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    @Override
    public String toString() {
        return "RecordItem{" +
                "id=" + id +
                ", balance='" + balance + '\'' +
                ", recordId='" + recordId + '\'' +
                ", platfrom='" + platfrom + '\'' +
                ", plafrom='" + plafrom + '\'' +
                ", senderAccount='" + senderAccount + '\'' +
                ", senderComment='" + senderComment + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderNickname='" + senderNickname + '\'' +
                ", transferAmount='" + transferAmount + '\'' +
                ", transferTime=" + transferTime +
                ", type=" + type +
                ", times=" + times +
                ", number=" + number +
                ", beforeBalance=" + beforeBalance +
                '}';
    }
}
