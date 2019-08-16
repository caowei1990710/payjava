package com.example.demo.Model;

/**
 * Created by snsoft on 11/4/2019.
 */
public class Bankpay {
    private String cardIndex;//支付key
    private String cardNo;//账号加密
    private String bankAccount;//转账名字
    private String money;//转账金额
    private String bankCode;//银行类型
    private String propsalNumber;//提案金额
    private String transerfee;//手续费
    private String payUrl;//付款url
    private String state;//状态
    private String lockTime;//提案锁定时间
    private String remark;//备注
    private String result;//银行值

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTranserfee() {
        return transerfee;
    }

    public void setTranserfee(String transerfee) {
        this.transerfee = transerfee;
    }

    public String getPropsalNumber() {
        return propsalNumber;
    }

    public void setPropsalNumber(String propsalNumber) {
        this.propsalNumber = propsalNumber;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(String cardIndex) {
        this.cardIndex = cardIndex;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public Bankpay() {
    }
}
