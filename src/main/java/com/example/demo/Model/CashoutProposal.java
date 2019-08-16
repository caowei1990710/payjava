package com.example.demo.Model;

import com.example.demo.Utils.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 15/5/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class CashoutProposal {
    @Id
    @GeneratedValue
    private int id;
    private String bankCard;
    private String bankDest;
    private String destName;
    private String destType;
    private Double amount;
    private Date createTime;
    private Date approveTime;
    private String angentName;
    private Date finshTime;
    private String state;
    private String remark;
    private String platfrom;
    private String sign;
    private String ip;
    private String content;
    private String uniqueName;
    @Column(name = "pay_posalunique", unique = true)
    private String payPosalunique;//唯一

    public String getPayPosalunique() {
        return payPosalunique;
    }

    public void setPayPosalunique(String payPosalunique) {
        this.payPosalunique = payPosalunique;
    }

    public String getUniqueName() {

        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAngentName() {
        return angentName;
    }

    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    public void setAngentName(String angentName) {
        this.angentName = angentName;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public int getId() {
        return id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public Date getFinshTime() {
        return finshTime;
    }

    public void setFinshTime(Date finshTime) {
        this.finshTime = finshTime;
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

    public String getBankDest() {
        return bankDest;
    }

    public void setBankDest(String bankDest) {
        this.bankDest = bankDest;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public CashoutProposal() {
        this.createTime = new Date();
        this.state = Config.Begining;
    }

    @Override
    public String toString() {
        return "CashoutProposal{" +
                "id=" + id +
                ", bankCard='" + bankCard + '\'' +
                ", bankDest='" + bankDest + '\'' +
                ", destName='" + destName + '\'' +
                ", destType='" + destType + '\'' +
                ", amount=" + amount +
                ", createTime=" + createTime +
                ", approveTime=" + approveTime +
                ", angentName='" + angentName + '\'' +
                ", finshTime=" + finshTime +
                ", state='" + state + '\'' +
                ", remark='" + remark + '\'' +
                ", platfrom='" + platfrom + '\'' +
                ", sign='" + sign + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
