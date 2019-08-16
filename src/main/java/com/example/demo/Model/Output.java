package com.example.demo.Model;

import com.example.demo.Service.BankcardService;
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
 * Created by snsoft on 9/5/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Output implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @NotEmpty(message = "提案号")
    @Column(name = "deposit_number", unique = true)
    private String depositNumber;
    private String fromBank;
    private String destBank;
    private Double amount;
    private String fromBankType;
    private String destBankType;
    private String destName;
    private String destNickname;
    private Date createTime;
    private Date finishTime;
    private String state;
    private Double payfee;
    private String createUser;
    private String platfrom;
    private Integer times;
    private String ip;
    private static final long serialVersionUID = 7247714666080613258L;

    public String getDestNickname() {
        return destNickname;
    }

    public void setDestNickname(String destNickname) {
        this.destNickname = destNickname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public String getDepositNumber() {
        return depositNumber;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getDestBankType() {
        return destBankType;
    }

    public void setDestBankType(String destBankType) {
        this.destBankType = destBankType;
    }

    public String getFromBankType() {
        return fromBankType;
    }

    public void setFromBankType(String fromBankType) {
        this.fromBankType = fromBankType;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public Double getPayfee() {
        return payfee;
    }

    public void setPayfee(Double payfee) {
        this.payfee = payfee;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Output() {
        this.createTime = new Date();
        this.state = Config.Normal;
        this.times = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromBank() {
        return fromBank;
    }

    public void setFromBank(String fromBank) {
        this.fromBank = fromBank;
    }

    public String getDestBank() {
        return destBank;
    }

    public void setDestBank(String destBank) {
        this.destBank = destBank;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = BankcardService.formatDouble1(amount);
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public String toString() {
        return "Output{" +
                "id=" + id +
                ", depositNumber='" + depositNumber + '\'' +
                ", fromBank='" + fromBank + '\'' +
                ", destBank='" + destBank + '\'' +
                ", amount=" + amount +
                ", fromBankType='" + fromBankType + '\'' +
                ", destBankType='" + destBankType + '\'' +
                ", destName='" + destName + '\'' +
                ", createTime=" + createTime +
                ", finishTime=" + finishTime +
                ", state='" + state + '\'' +
                ", payfee=" + payfee +
                ", createUser='" + createUser + '\'' +
                ", platfrom='" + platfrom + '\'' +
                ", ip='" + ip + '\'' +
                ", destNickname='" + destNickname + '\'' +
                '}';
    }
}
