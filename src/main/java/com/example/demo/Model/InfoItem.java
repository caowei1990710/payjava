package com.example.demo.Model;

/**
 * Created by snsoft on 20/12/2017.
 */
public class InfoItem {
    private String transferId;
    private String payerNickname;
    private String transferAmount;
    private String transferTime;
    private String sendAccount;

    public String getSendAccount() {
        return sendAccount;
    }

    public void setSendAccount(String sendAccount) {
        this.sendAccount = sendAccount;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getPayerNickname() {
        return payerNickname;
    }

    public void setPayerNickname(String payerNickname) {
        this.payerNickname = payerNickname;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(String transferTime) {
        this.transferTime = transferTime;
    }

    @Override
    public String toString() {
        return "InfoItem{" +
                "transferId='" + transferId + '\'' +
                ", payerNickname='" + payerNickname + '\'' +
                ", transferAmount='" + transferAmount + '\'' +
                ", transferTime='" + transferTime + '\'' +
                ", sendAccount='" + sendAccount + '\'' +
                '}';
    }
}
