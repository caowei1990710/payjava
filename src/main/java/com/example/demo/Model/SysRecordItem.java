package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 17/2/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysRecordItem {
    @Id
    @GeneratedValue
    private int sysId;
    private int id;
    private String transferId;
    private String account;
    private String organization;
    private String transferAmount;
    private String transferTime;
    private Date transferTimes;
    private String payerName;
    private String paymentAccount;
    private String transferFrom;
    private String beforeBalance;
    private String refBeforeBalance;
    private String balance;
    private String payerComment;
    private String creationTime;
    private String paymentOrganization;
    private Date creationTimes;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getRefBeforeBalance() {
        return refBeforeBalance;
    }

    public void setRefBeforeBalance(String refBeforeBalance) {
        this.refBeforeBalance = refBeforeBalance;
    }

    public String getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(String beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public String getPaymentOrganization() {
        return paymentOrganization;
    }

    public void setPaymentOrganization(String paymentOrganization) {
        this.paymentOrganization = paymentOrganization;
    }

    public int getSysId() {
        return sysId;
    }

    public Date getTransferTimes() {
        return transferTimes;
    }

    public void setTransferTimes(Date transferTimes) {
        this.transferTimes = transferTimes;
    }

    public Date getCreationTimes() {
        return creationTimes;
    }

    public void setCreationTimes(Date creationTimes) {
        this.creationTimes = creationTimes;
    }

    public void setSysId(int sysId) {
        this.sysId = sysId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(String transferTime) {
        this.transferTime = transferTime;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public String getTransferFrom() {
        return transferFrom;
    }

    public void setTransferFrom(String transferFrom) {
        this.transferFrom = transferFrom;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPayerComment() {
        return payerComment;
    }

    public void setPayerComment(String payerComment) {
        this.payerComment = payerComment;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "SysRecordItem{" +
                "sysId=" + sysId +
                ", id=" + id +
                ", transferId='" + transferId + '\'' +
                ", account='" + account + '\'' +
                ", organization='" + organization + '\'' +
                ", transferAmount='" + transferAmount + '\'' +
                ", transferTime='" + transferTime + '\'' +
                ", transferTimes=" + transferTimes +
                ", payerName='" + payerName + '\'' +
                ", paymentAccount='" + paymentAccount + '\'' +
                ", transferFrom='" + transferFrom + '\'' +
                ", balance='" + balance + '\'' +
                ", payerComment='" + payerComment + '\'' +
                ", creationTime='" + creationTime + '\'' +
                ", creationTime='" + creationTime + '\'' +
                ", beforeBalance=" + beforeBalance +
                ", number=" + number +
                ", refBeforeBalance=" + refBeforeBalance +
                '}';
    }
}
