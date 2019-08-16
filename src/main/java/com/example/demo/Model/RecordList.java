package com.example.demo.Model;

import java.util.ArrayList;

/**
 * Created by snsoft on 18/12/2017.
 */
public class RecordList {
    private String alipayAccount;
    private ArrayList<RecordItem> depositRecords;

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public ArrayList<RecordItem> getDepositRecords() {
        return depositRecords;
    }

    public void setDepositRecords(ArrayList<RecordItem> depositRecords) {
        this.depositRecords = depositRecords;
    }

    public RecordList() {
    }

    @Override
    public String toString() {
        return "RecordList{" +
                "alipayAccount='" + alipayAccount + '\'' +
                ", depositRecords=" + depositRecords +
                '}';
    }
}
