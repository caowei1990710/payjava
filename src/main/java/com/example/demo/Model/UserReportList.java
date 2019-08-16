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
 * Created by snsoft on 22/5/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserReportList {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserReportList.class);
    @Id
    @GeneratedValue

    private int id;
    private String type;
    private String createUser;
    private Date createTime;
    private Double changeMoney;
    private Double befroeMoney;
    private Double lockMoney;
    private Double nowMoney;
    private String remark;
    private String platfrom;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Double getLockMoney() {
        return lockMoney;
    }

    public void setLockMoney(Double lockMoney) {
        this.lockMoney = lockMoney;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public UserReportList() {
        this.createTime = new Date();
    }

    public UserReportList(String type, Double changeMoney, Double befroeMoney, String remark, String createUser, String platfrom) {
        this.createTime = new Date();
        this.type = type;
        this.changeMoney = BankcardService.formatDouble1(changeMoney);
        this.befroeMoney = BankcardService.formatDouble1(befroeMoney);
        this.nowMoney = BankcardService.formatDouble1(befroeMoney + changeMoney);
        this.createUser = createUser;
        this.remark = remark;
        this.platfrom = platfrom;
        logger.error("金额变动:" + changeMoney + " 平台:" + platfrom);
    }
//    public UserReportList(Double payfee,Double befroeMoney, String remark, String createUser, String platfrom){
//        this.createTime = new Date();
//        this.type = type;
//        this.changeMoney = changeMoney;
//        this.befroeMoney = befroeMoney;
//        this.nowMoney = befroeMoney + changeMoney;
//        this.createUser = createUser;
//        this.remark = remark;
//        this.platfrom = platfrom;
//    }
//    //取消提款


    @Override
    public String toString() {
        return "UserReportList{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", changeMoney=" + changeMoney +
                ", befroeMoney=" + befroeMoney +
                ", nowMoney=" + nowMoney +
                ", remark='" + remark + '\'' +
                ", platfrom='" + platfrom + '\'' +
                '}';
    }
}
