package com.example.demo.Model;

import java.util.ArrayList;

/**
 * Created by snsoft on 7/5/2018.
 */
public class DepositList {
    private String alipayAccount;
    private ArrayList<Deposit> depositRecords;

    public ArrayList<Deposit> getDepositRecords() {
        return depositRecords;
    }

    public void setDepositRecords(ArrayList<Deposit> depositRecords) {
        this.depositRecords = depositRecords;
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public DepositList() {
    }

    @Override
    public String toString() {
        return "DepositList{" +
                "alipayAccount='" + alipayAccount + '\'' +
                ", depositRecords=" + depositRecords +
                '}';
    }
}
