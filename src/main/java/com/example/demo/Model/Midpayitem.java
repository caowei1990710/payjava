package com.example.demo.Model;

/**
 * Created by snsoft on 28/5/2018.
 */
public class Midpayitem {
    private String version;//版本号
    private String MerchaantNo;//商户号
    private Double amount;//付款金额
    private String type;//收款类型
    private String payer;//游戏账号
    private String sign;//商户签名
    private String url;//回调地址
    private String RealName;//真实姓名
    //    private String depositNumber;//提案号
    private String userRemark;//提案号
    private String formType;//充值渠道 0:pc ，1：安卓，2：ios
    private String fromType;//充值渠道 0:pc ，1：安卓，2：ios
    private String depositAmount;//充值金额
    private String isMobile;//0：PC,1:移动
    private String returnUrl;//同步回调地址
    private String callBackUrl;//异步回调地址
    private String nickName;//昵称

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getFormType() {
        return formType;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount) {
        this.depositAmount = depositAmount;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

//    public String getDepositNumber() {
//        return depositNumber;
//    }
//
//    public void setDepositNumber(String depositNumber) {
//        this.depositNumber = depositNumber;
//    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMerchaantNo() {
        return MerchaantNo;
    }

    public void setMerchaantNo(String merchaantNo) {
        MerchaantNo = merchaantNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "Midpayitem{" +
                "version='" + version + '\'' +
                ", MerchaantNo='" + MerchaantNo + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", payer='" + payer + '\'' +
                ", sign='" + sign + '\'' +
                ", url='" + url + '\'' +
//                ", depositNumber='" + depositNumber + '\'' +
                ", userRemark='" + userRemark + '\'' +
                ", formType='" + formType + '\'' +
                ", fromType='" + fromType + '\'' +
                '}';
    }

    public Midpayitem() {
    }
}
