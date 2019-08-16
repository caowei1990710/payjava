package com.example.demo.Model;

/**
 * Created by snsoft on 20/11/2018.
 */
public class ApiDeposit {
    private String CustomerId;
    private String Mode;
    private String BankCode;
    private String Money;
    private String UserId;
    private String Message;
    private String CallBackUrl;
    private String ReturnUrl;
    private String Sign;
    private String Mobiletype;//0:pc,1:android,2:ios;
    private String Nickname ;//客人游戏账号
    private String Realname;//客人真实姓名
    public String getMobiletype() {
        return Mobiletype;
    }

    public void setMobiletype(String mobiletype) {
        this.Mobiletype = mobiletype;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public String getBankCode() {
        return BankCode;
    }

    public void setBankCode(String bankCode) {
        BankCode = bankCode;
    }

    public String getMoney() {
        return Money;
    }

    public void setMoney(String money) {
        Money = money;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCallBackUrl() {
        return CallBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        CallBackUrl = callBackUrl;
    }

    public String getReturnUrl() {
        return ReturnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        ReturnUrl = returnUrl;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getRealname() {
        return Realname;
    }

    public void setRealname(String realname) {
        Realname = realname;
    }

    @Override
    public String toString() {
        return "ApiDeposit{" +
                "CustomerId='" + CustomerId + '\'' +
                ", Mode='" + Mode + '\'' +
                ", BankCode='" + BankCode + '\'' +
                ", Money='" + Money + '\'' +
                ", UserId='" + UserId + '\'' +
                ", Message='" + Message + '\'' +
                ", CallBackUrl='" + CallBackUrl + '\'' +
                ", ReturnUrl='" + ReturnUrl + '\'' +
                ", Sign='" + Sign + '\'' +
                ", Mobiletype='" + Mobiletype + '\'' +
                ", Nickname='" + Nickname + '\'' +
                ", Realname='" + Realname + '\'' +
                '}';
    }
}
