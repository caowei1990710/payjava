package com.example.demo.Model;

/**
 * Created by snsoft on 21/1/2019.
 */
public class DepositIn {
    private String depositNumber;//支付宝单号
    private String userReamrk;//订单号
    private String amount;//金额
    private int Status;
    private String Time;//支付时间
    private String note;//客人备注
    private int Type;
    private String sign;//签名
    private String realName;//真实姓名

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getDepositNumber() {
        return depositNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public String getUserReamrk() {
        return userReamrk;
    }

    public void setUserReamrk(String userReamrk) {
        this.userReamrk = userReamrk;
    }


    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "DepositIn{" +
                "depositNumber='" + depositNumber + '\'' +
                ", userReamrk='" + userReamrk + '\'' +
                ", Status=" + Status +
                ", Time='" + Time + '\'' +
                ", note='" + note + '\'' +
                ", Type=" + Type +
                ", realName=" + realName +
                ", sign='" + sign + '\'' +
                '}';
    }
}
