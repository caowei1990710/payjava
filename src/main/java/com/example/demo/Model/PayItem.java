package com.example.demo.Model;

/**
 * Created by snsoft on 21/5/2018.
 */
public class PayItem {
    private String payoneBankcard;
    private Double tranonemoney;
    private Double payonefee;
    private String paytwoBankcard;
    private Double trantwomoney;
    private Double paytwofee;
    private String remark;
    private String propsalNumber;
    private String createUser;
    private String destBankCard;
    private String platfrom;
    private Double translatAmount;

    public Double getTranslatAmount() {
        return translatAmount;
    }

    public void setTranslatAmount(Double translatAmount) {
        this.translatAmount = translatAmount;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getDestBankCard() {
        return destBankCard;
    }

    public void setDestBankCard(String destBankCard) {
        this.destBankCard = destBankCard;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getPropsalNumber() {
        return propsalNumber;
    }

    public void setPropsalNumber(String propsalNumber) {
        this.propsalNumber = propsalNumber;
    }

    public PayItem() {
    }

    public String getPayoneBankcard() {
        return payoneBankcard;
    }

    public void setPayoneBankcard(String payoneBankcard) {
        this.payoneBankcard = payoneBankcard;
    }

    public String getPaytwoBankcard() {
        return paytwoBankcard;
    }

    public void setPaytwoBankcard(String paytwoBankcard) {
        this.paytwoBankcard = paytwoBankcard;
    }

    public Double getTranonemoney() {
        return tranonemoney;
    }

    public void setTranonemoney(Double tranonemoney) {
        this.tranonemoney = tranonemoney;
    }

    public Double getPayonefee() {
        return payonefee;
    }

    public void setPayonefee(Double payonefee) {
        this.payonefee = payonefee;
    }


    public Double getTrantwomoney() {
        return trantwomoney;
    }

    public void setTrantwomoney(Double trantwomoney) {
        this.trantwomoney = trantwomoney;
    }

    public Double getPaytwofee() {
        return paytwofee;
    }

    public void setPaytwofee(Double paytwofee) {
        this.paytwofee = paytwofee;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PayItem{" +
                "payoneBankcard='" + payoneBankcard + '\'' +
                ", tranonemoney=" + tranonemoney +
                ", payonefee=" + payonefee +
                ", paytwoBankcard='" + paytwoBankcard + '\'' +
                ", trantwomoney=" + trantwomoney +
                ", paytwofee=" + paytwofee +
                ", remark='" + remark + '\'' +
                ", propsalNumber='" + propsalNumber + '\'' +
                ", createUser='" + createUser + '\'' +
                ", destBankCard='" + destBankCard + '\'' +
                ", platfrom='" + platfrom + '\'' +
                ", translatAmount=" + translatAmount +
                '}';
    }
}
