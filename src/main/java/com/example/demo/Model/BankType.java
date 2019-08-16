package com.example.demo.Model;

/**
 * Created by snsoft on 24/8/2017.
 */
public class BankType {
    private int bankId;
    private String bankName;
    private String Code;
    public BankType(int bankId, String bankName,String Code) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.Code = Code;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
