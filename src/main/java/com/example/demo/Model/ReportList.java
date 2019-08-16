package com.example.demo.Model;

import com.example.demo.Service.BankcardService;
import com.example.demo.Utils.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 18/5/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportList {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReportList.class);
    @Id
    @GeneratedValue
    private int id;
    private String platfrom;
    private String account;
    private String accountType;
    private String type;
    private String createUser;
    private Date createTime;
    private Double changeMoney;
    private Double befroeMoney;
    private Double nowMoney;
    private String nickName;
    private String username;
    private String remark;
    private String state;
    private String destBankcard;
    private String ip;
    private String destip;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getDestip() {
        return destip;
    }

    public void setDestip(String destip) {
        this.destip = destip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDestBankcard() {
        return destBankcard;
    }

    public void setDestBankcard(String destBankcard) {
        this.destBankcard = destBankcard;
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

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getChangeMoney() {
        return changeMoney;
    }

    public void setChangeMoney(Double changeMoney) {
        this.changeMoney = BankcardService.formatDouble1(changeMoney);
    }

    public Double getBefroeMoney() {
        return befroeMoney;
    }

    public void setBefroeMoney(Double befroeMoney) {
        this.befroeMoney = BankcardService.formatDouble1(befroeMoney);
    }

    public Double getNowMoney() {
        return nowMoney;
    }

    public void setNowMoney(Double nowMoney) {
        this.nowMoney = BankcardService.formatDouble1(nowMoney);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ReportList() {
        this.createTime = new Date();
        this.state = Config.Normal;
    }

    public ReportList(String type, Double changeMoney, Double befroeMoney, String account, String accountType, String ip, String destip, String platfrom, String createUser, String remark) {
        this.createTime = new Date();
        this.account = account;
        this.accountType = accountType;
        this.changeMoney = BankcardService.formatDouble1(changeMoney);
        this.befroeMoney = BankcardService.formatDouble1(befroeMoney);
        this.nowMoney = BankcardService.formatDouble1(befroeMoney + changeMoney);
        this.platfrom = platfrom;
        this.createUser = createUser;
        this.state = Config.Normal;
        this.type = type;
        this.ip = ip;
        this.destip = destip;
        this.remark = remark;
        logger.error("金额变动:" + changeMoney + " 账号:" + account + " 类型:" + type + " 备注:" + remark);
    }
}
