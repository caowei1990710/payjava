package com.example.demo.Model;

/**
 * Created by snsoft on 21/11/2018.
 */
public class PayDeposit {
    private String CustomerId;
    private String OrderType;
    private String UserId;
    private String Sign;

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String orderType) {
        OrderType = orderType;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public PayDeposit() {
    }
}
