package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by snsoft on 29/8/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPosal implements Serializable {
    private static final long serialVersionUID = 7247714666080613244L;
    @Id
    @GeneratedValue
    private int id;
    @NotEmpty(message = "提案不能为空")
    @Column(name = "deposit_number", unique = true)
    private String depositNumber;
    private Date creatTime;
    private Date updateTime;
    private Date overTime;
    private String platfrom;
    private String state;
    private Double amount;
    private String nickName;
    private String realName;
    private String payUrl;
    //引导页面
    private String qrUrl;
    private String payAccont;
    private String remark;
    private String result;
    private String ReturnUrl;
    private String CallBackUrl;
    private String amountString;
    private String ip;
    private String formIp;//来源ip
    private String formPlace;//来源地址
    private String mobiletype;//0:pc,1:android,2:ios;
    private String getpaytype;//0:pid跳转，2多图跳转，银行卡就是手续费
    private Double realAmount;//实际付款金额
    private Double payFee;//手续费
    private String payType;//支付渠道

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Column(name = "pay_posalunique", unique = true)
    private String payPosalunique;//唯一

    public String getQrUrl() {
        return qrUrl;
    }

    public String getFormIp() {
        return formIp;
    }

    public void setFormIp(String formIp) {
        this.formIp = formIp;
    }

    public String getFormPlace() {
        return formPlace;
    }

    public void setFormPlace(String formPlace) {
        this.formPlace = formPlace;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getPayPosalunique() {
        return payPosalunique;
    }

    public void setPayPosalunique(String payPosalunique) {
        this.payPosalunique = payPosalunique;
    }

    public Double getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(Double realAmount) {
        this.realAmount = realAmount;
    }

    public Double getPayFee() {
        return payFee;
    }

    public void setPayFee(Double payFee) {
        this.payFee = payFee;
    }

    public String getGetpaytype() {
        return getpaytype;
    }

    public void setGetpaytype(String getpaytype) {
        this.getpaytype = getpaytype;
    }

    public String getMobiletype() {
        return mobiletype;
    }

    public void setMobiletype(String mobiletype) {
        this.mobiletype = mobiletype;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAmountString() {
        return amountString;
    }

    public void setAmountString(String amountString) {
        this.amountString = amountString;
    }

    public String getReturnUrl() {
        return ReturnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        ReturnUrl = returnUrl;
    }

    public String getCallBackUrl() {
        return CallBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        CallBackUrl = callBackUrl;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPayAccont() {
        return payAccont;
    }

    public void setPayAccont(String payAccont) {
        this.payAccont = payAccont;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getOverTime() {
        return overTime;
    }

    public void setOverTime(Date overTime) {
        this.overTime = overTime;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public PayPosal() {
        this.mobiletype = "0";
    }
}
